package com.BINexus.back.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 邀请相关
 * @TableName invitation
 */
@TableName(value ="invitation")
@Data
public class Invitation {
    @ApiModelProperty("id")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty("邀请码")
    private String code;

    @ApiModelProperty("操作者")
    private Long operatorId;

    @ApiModelProperty("接收者")
    private Long receiverId;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("状态 0-未使用 1-已使用")
    private Byte status;
}
