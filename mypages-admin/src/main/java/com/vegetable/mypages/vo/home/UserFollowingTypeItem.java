package com.vegetable.mypages.vo.home;

import com.vegetable.mypages.entity.UserFollowingType;
import com.vegetable.mypages.entity.UserOpinion;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 首页平台内容数据-用户关注用户类型数据封装对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "UserFollowingTypeItem对象", description = "用户关注用户类型数据封装对象")
public class UserFollowingTypeItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户关注类型信息封装对象")
    private UserFollowingType userFollowingTypeInfo;

    @ApiModelProperty(value = "用户对关注类型的看法列表")
    private List<UserOpinion> userOpinionList;

    @ApiModelProperty(value = "用户在某类型下的关注用户列表封装对象")
    private List<UserFollowingItem> userFollowingList;

}
