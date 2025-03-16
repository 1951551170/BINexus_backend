package com.BINexus.back.controller;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class QianWenAI {

    @Value("${aliyun.Key}")
    private String key;

    @Value("${aliyun.model}")
    private String model;

    public GenerationResult callWithMessage(String con) throws ApiException, NoApiKeyException, InputRequiredException {
        String moban =
                "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
                "分析需求：\n" +
                "{数据分析的需求或者目标}\n" +
                "原始数据：\n" +
                "{csv格式的原始数据，用,作为分隔符}\n" +
                "请根据这两部分内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）\n" +
                "【【【【【\n" +
                "{前端 Echarts V5 的 option 配置对象json代码，必须是严格的json形式，注意这个json中不要使用双引号而不是单引号，然后合理地将数据进行可视化，不要生成任何多余的内容，比如注释或包括这句话，只要代码部分}\n" +
                "【【【【【\n" +
                "{明确的数据分析结论、越详细越好，不要生成多余的注释}";

        Generation gen = new Generation();
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content(moban + con)
                .build();
        GenerationParam param = GenerationParam.builder()
                .apiKey(key)
                .model(model)
                .messages(Arrays.asList(userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();
        return gen.call(param);
    }
}