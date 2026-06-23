package com.chimeil.infrastructure.adapter.storage;

import com.chimeil.utils.AliOssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AliOssObjectStorageAdapter implements ObjectStorageAdapter {

    @Autowired
    private AliOssUtil aliOssUtil;

    @Override
    public String upload(byte[] bytes, String objectName) {
        return aliOssUtil.upload(bytes, objectName);
    }
}
