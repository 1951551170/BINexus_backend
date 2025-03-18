package com.BINexus.back.service.impl;

import com.BINexus.back.mapper.ShareMapper;
import com.BINexus.back.model.entity.Share;
import com.BINexus.back.service.ChartService;
import com.BINexus.back.service.ShareService;
import com.BINexus.back.utils.AESEncryptor;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class ShareServiceImpl extends ServiceImpl<ShareMapper, Share> implements ShareService {

    @Resource
    private ShareMapper shareMapper;

    @Resource
    private ChartService chartService;

    private final String secretKeyBase64 = "cGV0cmVjZVNlY3JldEtleQ=="; //

    @Override
    public String createShareLink(Long chartId, Long operatorId) {
        Share share = new Share();
        share.setChartId(chartId);
        share.setOperatorId(operatorId);
        share.setCreateTime(LocalDateTime.now());
        String encryptedUrl = null;
        try {
            AESEncryptor aesEncryptor = new AESEncryptor(secretKeyBase64);
            encryptedUrl = aesEncryptor.encrypt(chartId.toString());
        }catch(Exception e) {
            throw new RuntimeException("加密失败");
        }

        share.setUrl(encryptedUrl);
        this.save(share);
        return "http://localhost:8080/share/url/" + encryptedUrl;
    }

    @Override
    public Share getShareByEncryptedUrl(String encryptedUrl, Long operatorId) throws Exception {
        AESEncryptor aesEncryptor = new AESEncryptor(secretKeyBase64);
        String decryptedUrl = aesEncryptor.decrypt(encryptedUrl);

        Long chartId = Long.parseLong(decryptedUrl);
        QueryWrapper<Share> wrapper = new QueryWrapper<>();
        wrapper.eq("chartId", chartId);

        Share share = this.getOne(wrapper);
        if (share == null) {
            throw new Exception("分享链接不存在");
        }

        return share;
    }
}