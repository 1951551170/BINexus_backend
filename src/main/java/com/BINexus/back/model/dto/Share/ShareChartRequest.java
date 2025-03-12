package com.BINexus.back.model.dto.Share;

import lombok.Data;

import java.io.Serializable;

@Data
public class ShareChartRequest implements Serializable {

    private Long chartId;

    private Long operatorId;

    private static final long serialVersionUID = 100L;
}
