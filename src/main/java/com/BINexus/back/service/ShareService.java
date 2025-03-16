package com.BINexus.back.service;

import com.BINexus.back.model.entity.Share;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ShareService extends IService<Share> {
    String createShareLink(Long chartId, Long operatorId);
    Share getShareByEncryptedUrl(String encryptedUrl, Long operatorId) throws Exception;
}