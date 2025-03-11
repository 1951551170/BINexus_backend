package com.BINexus.back.service.impl;

import com.BINexus.back.common.BaseResponse;
import com.BINexus.back.mapper.InvitationMapper;
import com.BINexus.back.model.entity.Invitation;
import com.BINexus.back.model.vo.GenInvitationCodeVo;
import com.BINexus.back.service.InvitationService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.time.LocalDateTime;


@Service
public class InvitationServiceImpl extends ServiceImpl<InvitationMapper, Invitation> implements InvitationService {

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
            return new BaseResponse<>(500, "邀请码生成失败", null);
        }
    }

    /**
     * 生成指定长度的随机字符串
     */
    public static String generateRandomString(int length) {
        // 可以指定字符集，这里是默认的字母数字组合
        return RandomStringUtils.randomAlphanumeric(length);
    }
}
