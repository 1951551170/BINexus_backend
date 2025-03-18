package com.BINexus.back.controller;

import cn.hutool.core.io.FileUtil;
import com.BINexus.back.common.BaseResponse;
import com.BINexus.back.common.DeleteRequest;
import com.BINexus.back.common.ErrorCode;
import com.BINexus.back.common.ResultUtils;
import com.BINexus.back.constant.CommonConstant;
import com.BINexus.back.exception.BusinessException;
import com.BINexus.back.exception.ThrowUtils;
import com.BINexus.back.manager.AiManager;
import com.BINexus.back.manager.RedisLimiterManager;
import com.BINexus.back.model.dto.Share.ShareChartRequest;
import com.BINexus.back.model.dto.chart.ChartEditRequest;
import com.BINexus.back.model.dto.chart.ChartQueryRequest;
import com.BINexus.back.model.dto.chart.GenChartByAiRequest;
import com.BINexus.back.model.entity.Chart;
import com.BINexus.back.model.entity.User;
import com.BINexus.back.model.vo.BiResponse;
import com.BINexus.back.service.ChartService;
import com.BINexus.back.service.UserService;
import com.BINexus.back.utils.ExcelUtils;
import com.BINexus.back.utils.SqlUtils;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 帖子接口
 *
 *
 */
@RestController
@RequestMapping("/chart")
@Slf4j
public class ChartController {

    @Resource
    private ChartService chartService;

    @Resource
    private UserService userService;

    @Resource
    private AiManager aiManager;


    @Resource
    private RedisLimiterManager redisLimiterManager;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private QianWenAI qianWenAI;


    // region 增删改查

    /**
     * 通过分享创建
     *
     */
    @PostMapping("/addByShare")
    public BaseResponse<Boolean> addChart(@RequestBody ShareChartRequest shareChartRequest) {
        if (shareChartRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long chartId = shareChartRequest.getChartId();
        Chart chartDTO = chartService.getById(chartId);
        Chart newChart = new Chart();
        BeanUtils.copyProperties(chartDTO,newChart);
        newChart.setUserId(shareChartRequest.getOperatorId());
        newChart.setSource((byte)1);
        newChart.setId(null);
        boolean result = chartService.save(newChart);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteChart(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldChart.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = chartService.removeById(id);
        return ResultUtils.success(b);
    }


    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Chart> getChartById(@RequestParam long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = chartService.getById(id);
        if (chart == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(chart);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<Chart>> listChartByPage(@RequestBody ChartQueryRequest chartQueryRequest,
                                                     HttpServletRequest request) {
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = chartService.page(new Page<>(current, size),
                getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartPage);
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<Chart>> listMyChartByPage(@RequestBody ChartQueryRequest chartQueryRequest,
                                                       HttpServletRequest request) {
        if (chartQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        chartQueryRequest.setUserId(loginUser.getId());
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = chartService.page(new Page<>(current, size),
                getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartPage);
    }

    // endregion


    /**
     * 编辑（用户）
     *
     * @param chartEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editChart(@RequestBody ChartEditRequest chartEditRequest, HttpServletRequest request) {
        if (chartEditRequest == null || chartEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartEditRequest, chart);


        User loginUser = userService.getLoginUser(request);
        long id = chartEditRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldChart.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = chartService.updateById(chart);
        return ResultUtils.success(result);
    }

    /**
     * 智能分析（同步）
     *
     * @param multipartFile
     * @param genChartByAiRequest
     * @param request
     * @return
     */
    @PostMapping("/gen")
    public BaseResponse<BiResponse> genChartByAi(MultipartFile multipartFile,
                                                 GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) throws NoApiKeyException, InputRequiredException {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();

        // 校验
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR,"目标为空");
        ThrowUtils.throwIf(StringUtils.isBlank(name)&&name.length()>100, ErrorCode.PARAMS_ERROR,"名称过长");
        if (name!=null){
            throw new BusinessException(ErrorCode.TOKEN_EXPIRE);
        }
        //获取文件
        long size = multipartFile.getSize();
        String fileName = multipartFile.getOriginalFilename();
        //校验文件大小
        long MAX_SIZE = 1024 * 1024 * 1L;
        ThrowUtils.throwIf(size>MAX_SIZE,ErrorCode.PARAMS_ERROR,"文件大小超过限制");

        //校验文件后缀
        String suffix = FileUtil.getSuffix(fileName);
        final List<String> validFileSuffixList = Arrays.asList("xlsx");
        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix), ErrorCode.PARAMS_ERROR, "文件后缀非法");


        User loginUser= userService.getLoginUser(request);
        // 限流判断，每个用户一个限流器
        redisLimiterManager.doRateLimit("genChartByAi_" + loginUser.getId());


        //long biModelId = CommonConstant.BI_MODEL_ID;

        //开始拼接
        StringBuilder userInput = new StringBuilder();
        //1.1拼接需求
        userInput.append("分析需求：").append("\n");

        //1.2拼接目标与图表类型
        String userGoal=goal;
        if(StringUtils.isNotBlank(chartType)){
            userGoal+="，请使用："+chartType;
        }
        userInput.append(userGoal).append("\n");

        //1.3拼接数据
        userInput.append("原始数据：").append("\n");

        //压缩后的数据
        String csvData = ExcelUtils.excelToCsv(multipartFile);
        userInput.append(csvData).append("\n");

       //调用AI
        //String que=aiManager.doChat(biModelId,userInput.toString());


        GenerationResult result = qianWenAI.callWithMessage(userInput.toString());
//            System.out.println("思考过程：");
//            System.out.println(result.getOutput().getChoices().get(0).getMessage().getReasoningContent());
        System.out.println("回复内容："+ result.getOutput().getChoices().get(0).getMessage().getContent()+"\n");
        String aiResult = result.getOutput().getChoices().get(0).getMessage().getContent();






        //格式化ai生成结果
        String[] split = aiResult.split("【【【【【");//因为设置了ai回答用【【【【【间隔
        if (split.length<3){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"AI生成错误");
        }
        String genChart=split[1].trim();//trim()可以去掉多余的空格换行等
        String genResult=split[2].trim();
        //插入到数据库

        Chart chart=new Chart();
        chart.setName(name);
        chart.setGoal(goal);//分析目标
        chart.setChartData(csvData);//原始数据
        chart.setChartType(chartType);
        chart.setGenChart(genChart);//ai给的图标的json格式
        chart.setGenResult(genResult);//ai给的结论
        chart.setStatus("succeed");
        chart.setUserId(loginUser.getId());
        boolean saveResult= chartService.save(chart);
        ThrowUtils.throwIf(!saveResult,ErrorCode.SYSTEM_ERROR,"图标保存失败");
        BiResponse biResponse=new BiResponse();
        biResponse.setGenChart(genChart);
        biResponse.setGenResult(genResult);
        biResponse.setChartId(chart.getId());
        return ResultUtils.success(biResponse);

    }

    /**
     * 智能分析（异步）
     *
     * @param multipartFile
     * @param genChartByAiRequest
     * @param request
     * @return
     */
    @PostMapping("/gen/async")
    public BaseResponse<BiResponse> genChartByAiAsync(@RequestPart("file") MultipartFile multipartFile,
                                                      GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();

        // 校验
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR,"目标为空");
        ThrowUtils.throwIf(StringUtils.isBlank(name)&&name.length()>100, ErrorCode.PARAMS_ERROR,"名称过长");

        long size = multipartFile.getSize();
        String fileName = multipartFile.getOriginalFilename();

        long MAX_SIZE = 1024 * 1024 * 1L;
        ThrowUtils.throwIf(size>MAX_SIZE,ErrorCode.PARAMS_ERROR,"文件大小超过限制");

        String suffix = FileUtil.getSuffix(fileName);
        final List<String> validFileSuffixList = Arrays.asList("xlsx");
        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix), ErrorCode.PARAMS_ERROR, "文件后缀非法");


        User loginUser= userService.getLoginUser(request);
        // 限流判断，每个用户一个限流器
        redisLimiterManager.doRateLimit("genChartByAi_" + loginUser.getId());




        long biModelId = CommonConstant.BI_MODEL_ID;

        //根据用户提交的信息拼成给ai的问语
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");

        //拼接目标
        String userGoal=goal;
        if(StringUtils.isNotBlank(chartType)){
            userGoal+="，请使用："+chartType;
        }

        userInput.append(userGoal).append("\n");
        userInput.append("原始数据：").append("\n");
        String csvData = ExcelUtils.excelToCsv(multipartFile);//excel转csv
        userInput.append(csvData).append("\n");

        //插入到数据库
        Chart chart=new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        chart.setStatus("wait");
        chart.setUserId(loginUser.getId());
        boolean saveResult= chartService.save(chart);
        ThrowUtils.throwIf(!saveResult,ErrorCode.SYSTEM_ERROR,"图标保存失败");


        //新建一个任务，然后交给线程池

        //TODO 要try-catch任务队列满了抛的异常
        CompletableFuture.runAsync(()->{
            //先修改图表任务状态为“执行中”。等执行成功后，修改为“已完成”、保存执行结果；执行失败后，状态修改为“失败”，记录任务失败信息
            Chart updateChart=new Chart();
            updateChart.setId(chart.getId());
            updateChart.setStatus("running");
            boolean b=chartService.updateById(updateChart);
            if (!b){
                handleChartUpdateError(chart.getId(), "更新图表执行中状态失败");
                return;
            }

            //调用AI
            String result=aiManager.doChat(biModelId,userInput.toString());

            //分解ai给的结果
            String[] split = result.split("【【【【【");
            if (split.length<3){
                handleChartUpdateError(chart.getId(), "AI生成错误");
                return;
            }
            String genChart=split[1].trim();
            String genResult=split[2].trim();
            Chart updateChartResult=new Chart();
            updateChartResult.setId(chart.getId());
            updateChartResult.setGenChart(genChart);
            updateChartResult.setGenResult(genResult);
            updateChartResult.setStatus("succeed");
            boolean updateResult=chartService.updateById(updateChartResult);
            if (!updateResult){
                handleChartUpdateError(chart.getId(), "更新图表成功状态失败");
            }
        },threadPoolExecutor);

        BiResponse biResponse=new BiResponse();
        biResponse.setChartId(chart.getId());
        return ResultUtils.success(biResponse);
    }


    /**
     * 智能分析（异步消息队列）
     *
     * @param multipartFile
     * @param genChartByAiRequest
     * @param request
     * @return
     */
    @PostMapping("/gen/async/mq")
    public BaseResponse<BiResponse> genChartByAiAsyncMq(@RequestPart("file") MultipartFile multipartFile,
                                                        GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        BiResponse result = chartService.genChartByAiAsyncMq(multipartFile, genChartByAiRequest, request);
        return ResultUtils.success(result);
    }


    private void handleChartUpdateError(long chartId,String execMessage){
        Chart updateChartResult=new Chart();
        updateChartResult.setId(chartId);
        updateChartResult.setStatus("failed");
        updateChartResult.setExecMessage(execMessage);
        boolean updateResult=chartService.updateById(updateChartResult);
        if (!updateResult){
            log.error("更新图表失败状态 失败"+chartId+","+execMessage);
        }
    }

    /**
     * 获取查询包装类
     *
     * @param chartQueryRequest
     * @return
     */
    private QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest) {
        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        if (chartQueryRequest == null) {
            return queryWrapper;
        }

        Long id = chartQueryRequest.getId();
        String name = chartQueryRequest.getName();
        String goal = chartQueryRequest.getGoal();
        String chartType = chartQueryRequest.getChartType();
        Long userId = chartQueryRequest.getUserId();
        String sortField = chartQueryRequest.getSortField();
        String sortOrder = chartQueryRequest.getSortOrder();

        queryWrapper.eq(id!=null && id>0,"id",id);
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.eq(StringUtils.isNotBlank(goal), "goal", goal);
        queryWrapper.eq(StringUtils.isNotBlank(chartType), "chartType", chartType);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

}
