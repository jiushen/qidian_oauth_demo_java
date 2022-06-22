package com.qidian.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class GuavaService {

    // 模拟db操作,实际需要持久化到db
    private Map<String, String> userMap = new HashMap<>();

    @Cacheable(value = "visitor", key = "'visitor'.concat(#visitorId)")
    public String findGoodsIdByVisitorId(String visitorId) {
        log.info("findGoodsIdByVisitorId query from db, visitorId: {}", visitorId);
        return userMap.get(visitorId);
    }

    @CachePut(value = "visitor", key = "'visitor'.concat(#visitorId)")
    public String update(String visitorId, String goodsId) {
        log.info("update db, visitorId: {},goodsId:{}", visitorId, goodsId);
        userMap.put(visitorId, goodsId);
        return goodsId;
    }
}
