package com.BINexus.back.service;


import com.BINexus.back.common.BaseResponse;
import com.BINexus.back.model.dto.chart.GenChartByAiRequest;
import com.BINexus.back.model.entity.Chart;
import com.BINexus.back.model.vo.BiResponse;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
* @author petrece
* @description 针对表【chart(图表信息表)】的数据库操作Service
* @createDate 2023-11-09 22:00:59
*/
public interface ChartService extends IService<Chart> {

    BiResponse genChartByAiAsyncMq(MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest, HttpServletRequest request);
}
