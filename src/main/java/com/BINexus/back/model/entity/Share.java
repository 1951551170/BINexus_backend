package com.BINexus.back.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分享
 * @TableName
 */
@TableName(value ="share")
@Data
public class Share {
    @TableId(type = IdType.AUTO)
    private Long id;

    //chartID
    private Long chartId;

    //生成的url
    private String url;

    //创建人
    private Long operatorId;

    //创建时间
    private LocalDateTime createTime;
}
