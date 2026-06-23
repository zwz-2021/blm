package com.chimeil.infrastructure.adapter.storage;

public interface ObjectStorageAdapter {

    String upload(byte[] bytes, String objectName);
}
