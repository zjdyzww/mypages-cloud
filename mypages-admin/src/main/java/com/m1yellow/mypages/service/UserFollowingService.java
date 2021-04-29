package com.m1yellow.mypages.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.m1yellow.mypages.bo.UserFollowingBo;
import com.m1yellow.mypages.entity.UserFollowing;
import com.m1yellow.mypages.excavation.bo.UserInfoItem;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户关注表 服务类
 * </p>
 *
 * @author M1Yellow
 * @since 2021-04-13
 */
public interface UserFollowingService extends IService<UserFollowing> {

    /**
     * 获取用户信息
     *
     * @param following
     * @return
     */
    UserInfoItem doExcavate(UserFollowingBo following);

    /**
     * 保存同步用户信息
     *
     * @param userInfoItem
     * @param following
     * @return
     */
    boolean saveUserInfo(UserInfoItem userInfoItem, UserFollowing following);

    /**
     * 根据 id 逻辑删除
     *
     * @param following
     * @return
     */
    boolean deleteById(UserFollowing following);

    /**
     * 自定义查询一个对象
     *
     * @param params 参数封装 map
     * @return
     */
    UserFollowingBo getUserFollowing(Map params);

    /**
     * 自定义查询列表
     *
     * @param params
     * @return
     */
    List<UserFollowingBo> queryUserFollowingList(Map params);

    /**
     * 获取关注用户的类型 id 列表
     *
     * @param params
     * @return
     */
    List<Long> queryTypeIdList(Map params);

    /**
     * 获取关注用户所在平台的id或标识
     * @param following
     * @return
     */
    String getUserKeyFromMainPage(UserFollowingBo following);

}
