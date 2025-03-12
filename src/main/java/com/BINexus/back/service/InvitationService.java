package com.BINexus.back.service;

import com.BINexus.back.common.BaseResponse;
import com.BINexus.back.model.entity.Invitation;
import com.BINexus.back.model.vo.GenInvitationCodeVo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface InvitationService extends IService<Invitation> {
    public BaseResponse<GenInvitationCodeVo> genInvitationCode(Long operatorId);

    BaseResponse<Boolean> acceptInvite(Long id, String code);

}