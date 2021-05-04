package com.m1yellow.mypages.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 用户与关注用户关联表
 * </p>
 *
 * @author M1Yellow
 * @since 2021-05-03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "UserFollowingRelation对象", description = "用户与关注用户关联表")
public class UserFollowingRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "表id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "关联用户id")
    private Long userId;

    @ApiModelProperty(value = "关联关注用户id")
    private Long followingId;

    @ApiModelProperty(value = "关联平台id，冗余字段，用于提升查询效率")
    private Long platformId;

    @ApiModelProperty(value = "关联关注类型表id，1-默认分类，用于变更类型")
    private Long typeId;

    @ApiModelProperty(value = "优先级由低到高：1-10，默认5。8-思想、学习；7-美食、营养；6、健身、锻炼；5-兴趣、生活；4~其他")
    private Integer sortNo;

    @ApiModelProperty(value = "本条数据是否已删除，1-是；0-否，默认0")
    @TableLogic
    private Boolean isDeleted;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;


}
