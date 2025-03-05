package com.BINexus.back.service.impl;


import com.BINexus.back.mapper.ChartMapper;
import com.BINexus.back.model.entity.Chart;
import com.BINexus.back.service.ChartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
* @author petrece
* @description 针对表【chart(图表信息表)】的数据库操作Service实现
* @createDate 2023-11-09 22:00:59
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService {
}




