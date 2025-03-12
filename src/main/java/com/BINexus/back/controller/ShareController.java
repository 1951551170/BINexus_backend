package com.BINexus.back.controller;

import com.BINexus.back.common.BaseResponse;
import com.BINexus.back.common.ErrorCode;
import com.BINexus.back.constant.UserConstant;
import com.BINexus.back.exception.BusinessException;
import com.BINexus.back.model.entity.Share;
import com.BINexus.back.model.entity.User;
import com.BINexus.back.service.ShareService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/share")
public class ShareController {

    @Resource
    private ShareService shareService;

    @PostMapping("/create")
    public BaseResponse<String> createShareLink(Long chartId, HttpServletRequest request) {
        try {
            Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
            User currentUser = (User) userObj;
            if (currentUser == null || currentUser.getId() == null) {
                throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
            }
            String shareLink = shareService.createShareLink(chartId, currentUser.getId());
            return new BaseResponse<>(shareLink);
        } catch (Exception e) {
            throw new BusinessException("失败: " + e.getMessage());
        }
    }

    @GetMapping("/{encryptedUrl}")
    public BaseResponse<Share> getSharedAnalysis(@PathVariable String encryptedUrl) throws Exception {
        Share share = shareService.getShareByEncryptedUrl(encryptedUrl);
        if (share == null) {
            throw new RuntimeException("未找到分享");
        }
        return new BaseResponse<>(share);

    }
}