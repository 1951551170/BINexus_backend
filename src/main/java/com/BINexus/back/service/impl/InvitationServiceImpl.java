package com.BINexus.back.service.impl;

import com.BINexus.back.common.BaseResponse;
import com.BINexus.back.exception.BusinessException;
import com.BINexus.back.mapper.InvitationMapper;
import com.BINexus.back.mapper.UserMapper;
import com.BINexus.back.model.entity.Invitation;
import com.BINexus.back.model.entity.User;
import com.BINexus.back.model.vo.GenInvitationCodeVo;
import com.BINexus.back.service.InvitationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class InvitationServiceImpl extends ServiceImpl<InvitationMapper, Invitation> implements InvitationService {

    @Autowired
    InvitationMapper invitationMapper;

    @Autowired
    UserMapper userMapper;

    private static final int INVITATION_CODE_LENGTH = 12;

    @Override
    public BaseResponse<GenInvitationCodeVo> genInvitationCode(Long operatorId) {
        // 创建一个新的邀请实体
        Invitation invitation = new Invitation();

        // 设置邀请码
        String invitationCode = generateRandomString(INVITATION_CODE_LENGTH);
        invitation.setCode(invitationCode);

        // 设置操作者ID
        invitation.setOperatorId(operatorId);

        // 默认状态为未使用
        invitation.setStatus((byte) 0);


        // 保存到数据库
        if (this.save(invitation)) {
            // 构造成功响应
            GenInvitationCodeVo responseVo = new GenInvitationCodeVo();
            responseVo.setInvitationCode(invitationCode);
            return new BaseResponse<>(responseVo);
        } else {
            // 如果保存失败，构造错误响应
            throw new BusinessException("邀请码生成失败");
        }
    }
//产看验证码是否存在
    //不能存在抛异常
    //存在查看接受者是不是邀请者，是就报错

    //TODO：用户表角色加个New枚举 ，表示没有权限使用ai，不能正常使用
    //查查这个用户当前是不是user，是就抛异常"您已经可以正常使用系统，请把邀请码留给别人吧~"
    //把用户表当前角色改为为User，
    //把当前用户设置为接受者
    @Override
    public BaseResponse<Boolean> acceptInvite(Long receiverId, String code) {
        // 使用 QueryWrapper 来构建查询条件
        QueryWrapper<Invitation> wrapper = new QueryWrapper<>();
        wrapper.eq("code", code)
                .eq("status", (byte) 0); // 状态为未使用

        // 查找邀请码
        Invitation invitation = this.getOne(wrapper);

        if (invitation == null) {
            throw new RuntimeException("无效的邀请码");
        }

        // 检查邀请码是否已经被使用
        if (invitation.getStatus() == 1) {
            throw new RuntimeException("该邀请码已被使用");
        }

        // 检查接收者是否已经是邀请者
        if (invitation.getReceiverId() != null && invitation.getOperatorId().equals(receiverId)) {
            throw new RuntimeException("操作失败");
        }

        // 获取用户信息
        User user = userMapper.selectById(receiverId);

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查用户当前角色
        if ("user".equalsIgnoreCase(user.getUserRole())) {
            throw new RuntimeException("您已经可以正常使用系统，请把邀请码留给别人吧~");
        }

        // 更新用户角色为User
        user.setUserRole("user"); // 根据实际情况调整设置角色的方式
        userMapper.updateById(user);

        // 更新邀请码状态和接收者ID
        invitation.setStatus((byte) 1);
        invitation.setReceiverId(receiverId);
        invitationMapper.updateById(invitation);

        return new BaseResponse<>(true);
    }

    @Override
    public BaseResponse<List<Invitation>> getAllInvitationCode(Long id) {
        log.info("用户id为" + id);
        // 创建查询条件
        LambdaQueryWrapper<Invitation> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Invitation::getOperatorId, id);

        // 查询所有符合条件的邀请码
        List<Invitation> invitations = this.list(wrapper);
        log.info("结果为" + invitations);
        if (invitations.isEmpty()) {
            throw new RuntimeException("您没有邀请码");
        }
        // 构造成功响应
        return new BaseResponse<>(invitations);
    }

    /**
     * 生成指定长度的随机字符串
     */
    public static String generateRandomString(int length) {
        // 可以指定字符集，这里是默认的字母数字组合
        return RandomStringUtils.randomAlphanumeric(length);
    }
}
