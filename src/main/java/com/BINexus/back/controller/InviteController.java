package com.BINexus.back.controller;


import com.BINexus.back.annotation.AuthCheck;
import com.BINexus.back.common.BaseResponse;
import com.BINexus.back.constant.UserConstant;
import com.BINexus.back.model.entity.Invitation;
import com.BINexus.back.model.entity.User;
import com.BINexus.back.model.vo.GenInvitationCodeVo;
import com.BINexus.back.service.InvitationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/invite")
public class InviteController {
    @Autowired
    private InvitationService invitationService;

    //TODO：后续可以加个管理页面，让验证码失效等。现在整个小页面，生产过哪些，是否被用了，谁用的。页面上按个按钮就可以增加一个
    @GetMapping("/genCode")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<GenInvitationCodeVo> genInvitationCode(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        return invitationService.genInvitationCode(currentUser.getId());
    }

    //接受验证码
    @PostMapping("/acceptInvite")
    public BaseResponse<Boolean> acceptInvite(HttpServletRequest request,String code){
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        return invitationService.acceptInvite(currentUser.getId(),code);
    }

    @GetMapping("/getAllInvitationCode")
    public BaseResponse<List<Invitation>> getAllInvitationCode(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        BaseResponse<List<Invitation>> allInvitationCode = invitationService.getAllInvitationCode(currentUser.getId());
        log.info(""+allInvitationCode);
        return allInvitationCode;
    }

}
