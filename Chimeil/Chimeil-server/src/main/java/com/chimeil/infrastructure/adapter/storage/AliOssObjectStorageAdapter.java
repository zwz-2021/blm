package com.chimeil.infrastructure.adapter.storage;

import com.chimeil.utils.AliOssUtil;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class AliOssObjectStorageAdapter implements ObjectStorageAdapter {

        private final AliOssUtil aliOssUtil;

    @Override
    public String upload(byte[] bytes, String objectName) {
        return aliOssUtil.upload(bytes, objectName);
    }
}
