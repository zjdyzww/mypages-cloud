package cn.m1yellow.mypages.controller;


import cn.m1yellow.mypages.common.api.CommonResult;
import cn.m1yellow.mypages.common.aspect.DoCache;
import cn.m1yellow.mypages.common.aspect.WebLog;
import cn.m1yellow.mypages.common.constant.GlobalConstant;
import cn.m1yellow.mypages.common.exception.AtomicityException;
import cn.m1yellow.mypages.common.util.FastJsonUtil;
import cn.m1yellow.mypages.common.util.ObjectUtil;
import cn.m1yellow.mypages.common.util.RedisUtil;
import cn.m1yellow.mypages.entity.UserFollowingRelation;
import cn.m1yellow.mypages.entity.UserFollowingType;
import cn.m1yellow.mypages.service.UserFollowingRelationService;
import cn.m1yellow.mypages.service.UserFollowingService;
import cn.m1yellow.mypages.service.UserFollowingTypeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户关注分类表 前端控制器
 * </p>
 *
 * @author M1Yellow
 * @since 2021-04-13
 */
@RestController
@RequestMapping("/type")
public class UserFollowingTypeController {

    private static final Logger logger = LoggerFactory.getLogger(UserFollowingTypeController.class);


    @Autowired
    private UserFollowingService userFollowingService;
    @Autowired
    private UserFollowingTypeService userFollowingTypeService;
    @Autowired
    private UserFollowingRelationService userFollowingRelationService;
    @Autowired
    private RedisUtil redisUtil;


    @ApiOperation("添加/更新类型")
    @RequestMapping(value = "add", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @WebLog
    @DoCache
    public CommonResult<UserFollowingType> add(UserFollowingType type) {

        // id为0表示默认类型，默认类型系统自动管理，用户不能自己创建或编辑
        if (type == null || (type.getId() != null && type.getId() < 1)) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        // 校验必须参数
        if (type.getUserId() == null || type.getPlatformId() == null || StringUtils.isBlank(type.getTypeName())) {
            logger.error("typeName must be not null.");
            return CommonResult.failed("请检查必须参数");
        }

        // 先判断用户是否已经有相同名称的标签
        QueryWrapper<UserFollowingType> typeQueryWrapper = new QueryWrapper<>();
        typeQueryWrapper.eq("user_id", type.getUserId());
        typeQueryWrapper.eq("platform_id", type.getPlatformId());
        typeQueryWrapper.eq("type_name", type.getTypeName());
        UserFollowingType existedType = userFollowingTypeService.getOne(typeQueryWrapper);
        if (existedType != null) {
            // 存在相同名称的标签，则使用原来的id，执行更新操作
            type.setId(existedType.getId());
        }

        // 去字符串字段两边空格
        ObjectUtil.stringFiledTrim(type);

        if (!userFollowingTypeService.saveOrUpdate(type)) {
            logger.error("添加/更新类型失败");
            return CommonResult.failed("操作失败");
        }

        // 新增或修改记录后，清空缓存
        String cacheKey = GlobalConstant.USER_TYPE_LIST_CACHE_KEY + type.getUserId() + "_" + type.getPlatformId();
        redisUtil.del(cacheKey);
        logger.info(">>>> type add 删除用户对应类型列表缓存，cache key: {}", cacheKey);

        return CommonResult.success(type);
    }


    @ApiOperation("获取分类类型列表")
    @RequestMapping(value = "list", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @WebLog
    public CommonResult<List<UserFollowingType>> list(@RequestParam Long userId, @RequestParam Long platformId) {

        if (userId == null || platformId == null) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        // 先从缓存中获取
        String cacheKey = GlobalConstant.USER_TYPE_LIST_CACHE_KEY + userId + "_" + platformId;
        String cacheStr = ObjectUtil.getString(redisUtil.get(cacheKey));
        if (StringUtils.isNotBlank(cacheStr)) {
            List<UserFollowingType> userFollowingTypeList = FastJsonUtil.json2List(cacheStr, UserFollowingType.class);
            if (userFollowingTypeList != null && userFollowingTypeList.size() > 0) {
                return CommonResult.success(userFollowingTypeList);
            }
        }

        // TODO 添加游离于数据库类型表之外的默认类型，方便共用
        List<UserFollowingType> UserFollowingTypeMergeList = userFollowingTypeService.getUserFollowingTypeMergeList(userId, platformId, null);

        // 查询完成之后，设置缓存
        if (UserFollowingTypeMergeList != null && UserFollowingTypeMergeList.size() > 0) {
            redisUtil.set(cacheKey, FastJsonUtil.bean2Json(UserFollowingTypeMergeList), GlobalConstant.USER_PLATFORM_TYPE_LIST_CACHE_TIME);
        }

        return CommonResult.success(UserFollowingTypeMergeList);
    }


    @ApiOperation("移除分类类型")
    @Transactional // 加入事务支持
    @RequestMapping(value = "remove", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @WebLog
    @DoCache
    public CommonResult<String> remove(@RequestParam Long userId, @RequestParam Long platformId, @RequestParam Long typeId) {

        // id为0表示默认类型，默认类型系统自动管理
        if (userId == null || typeId == null || typeId < 1 || platformId == null) {
            logger.error("请求参数错误");
            return CommonResult.failed("请求参数错误");
        }

        // 查询类型记录
        QueryWrapper<UserFollowingType> typeQueryWrapper = new QueryWrapper<>();
        typeQueryWrapper.eq("user_id", userId);
        typeQueryWrapper.eq("platform_id", platformId);
        typeQueryWrapper.eq("id", typeId);
        UserFollowingType type = userFollowingTypeService.getOne(typeQueryWrapper);
        if (type == null) {
            logger.error("用户类型信息查询失败");
            return CommonResult.failed("用户类型信息查询失败");
        }

        // 删除类型记录，删除之后，其下的关注用户分配到默认分类
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("platform_id", platformId);
        params.put("id", typeId);
        if (!userFollowingTypeService.removeByMap(params)) {
            logger.error("移除失败，userId: {}, platformId: {}, typeId: {}", userId, platformId, typeId);
            throw new AtomicityException("操作失败");
        }

        // 先查询要删除的类型下是否有关注用户
        QueryWrapper<UserFollowingRelation> relationQueryWrapper = new QueryWrapper<>();
        relationQueryWrapper.eq("user_id", userId);
        relationQueryWrapper.eq("platform_id", platformId);
        relationQueryWrapper.eq("type_id", typeId);
        relationQueryWrapper.select("id");
        List<UserFollowingRelation> followingList = userFollowingRelationService.list(relationQueryWrapper);

        if (followingList != null && followingList.size() > 0) {
            // TODO 更改关注用户类型，注意！！更新记录为0的结果是false，有可能数据库记录本来就是0条，这个判断不符合业务要求
            boolean result = userFollowingRelationService.changeUserFollowingTypeByTypeId(userId, platformId, typeId,
                    GlobalConstant.USER_FOLLOWING_DEFAULT_TYPE_ID);
            if (!result) {
                logger.error("删除类型后变更其下用户类型失败");
                throw new AtomicityException("删除类型后变更其下用户类型失败");
            }
        }

        // 清除类型缓存
        String cacheKey = GlobalConstant.USER_TYPE_LIST_CACHE_KEY + userId + "_" + platformId;
        redisUtil.del(cacheKey);
        logger.info(">>>> remove type 清空类型缓存，cacheKey: {}", cacheKey);


        // 清空关注用户缓存
        // cacheKey 格式：USER_FOLLOWING_PAGE_LIST_CACHE_1_3_9
        cacheKey = GlobalConstant.USER_FOLLOWING_PAGE_LIST_CACHE_KEY + userId + "_" + platformId + "_" + typeId;
        userFollowingService.operatingFollowingItemPageCache(cacheKey, null, null, true, null);
        logger.info(">>>> remove type 清空关注用户缓存，cacheKey: {}", cacheKey);
        // 清除默认分类的用户缓存
        cacheKey = GlobalConstant.USER_FOLLOWING_PAGE_LIST_CACHE_KEY + userId + "_" + platformId + "_" + 0;
        userFollowingService.operatingFollowingItemPageCache(cacheKey, null, null, true, null);
        logger.info(">>>> remove type 清空关注用户缓存，cacheKey: {}", cacheKey);

        return CommonResult.success();
    }

}
