package com.BINexus.back.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.BINexus.back.model.entity.Share;
import java.util.List;

public interface ShareService extends IService<Share> {
    String createShareLink(Long chartId, Long operatorId) throws Exception;
    Share getShareByEncryptedUrl(String encryptedUrl) throws Exception;
}