package com.BINexus.back.service.impl;

import com.BINexus.back.mapper.ShareMapper;
import com.BINexus.back.model.entity.Share;
import com.BINexus.back.service.ShareService;
import com.BINexus.back.utils.AESEncryptor;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
public class ShareServiceImpl extends ServiceImpl<ShareMapper, Share> implements ShareService {

    @Resource
    private ShareMapper shareMapper;

    private final String secretKeyBase64 = "cGV0cmVjZVNlY3JldEtleUtleUtleQ=="; // petreceSecretKeyKeyKey

    @Override
    public String createShareLink(Long chartId, Long operatorId) throws Exception {
        Share share = new Share();
        share.setChartId(chartId);
        share.setOperatorId(operatorId);
        share.setCreateTime(LocalDateTime.now());

        AESEncryptor aesEncryptor = new AESEncryptor(secretKeyBase64);
        String encryptedUrl = aesEncryptor.encrypt(share.getId().toString());
        share.setUrl(encryptedUrl);
        this.save(share);
        return "http://localHost:8081/api/share/" + encryptedUrl;
    }

    @Override
    public Share getShareByEncryptedUrl(String encryptedUrl) throws Exception {
        AESEncryptor aesEncryptor = new AESEncryptor(secretKeyBase64);
        String decryptedUrl = aesEncryptor.decrypt(encryptedUrl);

        Long id = Long.parseLong(decryptedUrl);
        return this.getById(id);
    }
}