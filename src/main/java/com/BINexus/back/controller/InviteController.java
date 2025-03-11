package com.BINexus.back.controller;


import com.BINexus.back.annotation.AuthCheck;
import com.BINexus.back.common.BaseResponse;
import com.BINexus.back.constant.UserConstant;
import com.BINexus.back.model.vo.genInvitationCodeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/invite")
public class InviteController {

    //TODO：后续可以加个管理页面，让验证码失效等。现在整个小页面，生产过哪些，是否被用了，谁用的。页面上整个按钮就可以增加一个
    @GetMapping("/genCode")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<genInvitationCodeVo> genInvitationCode(){
        //先生成一个加密的邀请码，随机12位字符串
        //保存到数据库
        //返回信息
    }

    //接受验证码
        //产看验证码是否存在
        //不能存在抛异常
        //存在查看接受者是不是邀请者，是就报错

        //TODO：用户表角色加个New枚举 ，表示没有权限使用ai，不能正常使用
        //查查这个用户当前是不是user，是就抛异常"您已经可以正常使用系统，请把邀请码留给别人吧~"
        //把用户表当前角色改为为User，
        //把当前用户设置为接受者

}
