package com.qidian.demo.util;

import java.util.ArrayList;

class ByteGroup {
    ArrayList<Byte> byteContainer = new ArrayList<>();

    /**
     * 获取字符流
     *
     * @return bytes
     */
    public byte[] toBytes() {
        byte[] bytes = new byte[byteContainer.size()];
        for (int i = 0; i < byteContainer.size(); i++) {
            bytes[i] = byteContainer.get(i);
        }
        return bytes;
    }

    /**
     * 字符流添加
     *
     * @param bytes 字符流
     */
    public void addBytes(byte[] bytes) {
        for (byte b : bytes) {
            byteContainer.add(b);
        }
    }

    public int size() {
        return byteContainer.size();
    }
}
