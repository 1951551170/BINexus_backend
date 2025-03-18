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

    @GetMapping("/create")
    public BaseResponse<String> createShareLink(@RequestParam Long chartId, HttpServletRequest request) {
            Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
            User currentUser = (User) userObj;
            if (currentUser == null || currentUser.getId() == null) {
                throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
            }
            String shareLink = shareService.createShareLink(chartId, currentUser.getId());
            return new BaseResponse<>(shareLink);
    }

    @GetMapping("/url/{encryptedUrl}")
    public BaseResponse<Share> getSharedAnalysis(@PathVariable String encryptedUrl, HttpServletRequest request) throws Exception {
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        Share share = shareService.getShareByEncryptedUrl(encryptedUrl, currentUser.getId());
        if (share == null) {
            throw new BusinessException("未找到分享");
        }
        return new BaseResponse<>(share);

    }
}