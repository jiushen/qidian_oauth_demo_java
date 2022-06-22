package com.qidian.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.qidian.demo.model.Result;
import com.qidian.demo.sdk.CommonUtil;
import com.qidian.demo.service.GuavaService;
import com.qidian.demo.service.ThirdDemoService;
import com.qidian.demo.service.ThirdQidianService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/app")
@Slf4j
public class OpenApiController {

    @Autowired
    private ThirdDemoService thirdDemoService;

    @Autowired
    private ThirdQidianService thirdQidianService;

    @Autowired
    private GuavaService guavaService;

    /**
     * 获取用户信息
     *
     * @param uid
     * @return
     */
    @RequestMapping("/getUserInfo")
    public Result getUserInfo(@RequestParam(value = "uid", required = true) String uid) {
        String custId = thirdQidianService.getCuinBySocialAccount(uid, "3");
        if (StringUtils.isEmpty(custId)) {
            return Result.createSuccess();
        }
        String openId = thirdQidianService.getOpenIdByCustId(custId);
        if (StringUtils.isEmpty(openId)) {
            return Result.createSuccess();
        }
        return Result.createSuccess(thirdDemoService.getUserInfo(openId));
    }

    /**
     * @param limit
     * @param page
     * @param index
     * @param sort
     * @param type
     * @param keywords
     * @param uid
     * @return
     */
    @RequestMapping("/getGoodsList")
    public Result getGoodsList(@RequestParam(value = "limit", required = false) Integer limit,
                               @RequestParam(value = "page", required = false) Integer page,
                               @RequestParam(value = "index", required = false) Integer index,
                               @RequestParam(value = "sort", required = false) String sort,
                               @RequestParam(value = "type", required = false) String type,
                               @RequestParam(value = "keywords", required = false) String keywords,
                               @RequestParam(value = "uid", required = false) String uid) {
        if (!StringUtils.isEmpty(keywords)) {
            return Result.createSuccess(thirdDemoService.getGoodsList(limit, page, index, sort, "", type, keywords));
        }
        return Result.createSuccess(thirdDemoService.getGoodsList(limit, page, index, sort, guavaService.findGoodsIdByVisitorId(uid), type, keywords));
    }

    /**
     * 获取订单列表
     *
     * @param limit
     * @param page
     * @param index
     * @param sort
     * @param uid
     * @return
     */
    @RequestMapping("/getOrderList")
    public Result getOrderList(@RequestParam(value = "limit", required = false) Integer limit,
                               @RequestParam(value = "page", required = false) Integer page,
                               @RequestParam(value = "index", required = false) Integer index,
                               @RequestParam(value = "sort", required = false) String sort,
                               @RequestParam(value = "keywords", required = false) String keywords,
                               @RequestParam(value = "uid", required = false) String uid) {
        log.info("getOrderList uid: {}", uid);
        String custId = thirdQidianService.getCuinBySocialAccount(uid, "3");
        if (StringUtils.isEmpty(custId)) {
            return Result.createSuccess();
        }
        String openId = thirdQidianService.getOpenIdByCustId(custId);
        if (StringUtils.isEmpty(openId)) {
            return Result.createSuccess();
        }
        log.info("getOrderList openId: {}", openId);

        return Result.createSuccess(thirdDemoService.getOrderList(limit, page, index, sort, openId, keywords));
    }

    /**
     * 根据社交账号获取cust_id
     *
     * @param type
     * @param cid
     * @param authorization
     * @return
     */
    @RequestMapping("/getCuinBySocialAccount")
    public Result getCuinBySocialAccount(@RequestParam(value = "type", required = true) String type,
                                         @RequestParam(value = "cid", required = true) String cid,
                                         @RequestHeader(value = "Authorization", required = false) String authorization) {
        return Result.createSuccess(thirdQidianService.getCuinBySocialAccount(cid, type));
    }

    /**
     * 发送消息
     *
     * @param params
     * @param authorization
     * @return
     */
    @RequestMapping("/sendToC")
    public Result sendToC(@RequestBody JSONObject params,
                          @RequestHeader(value = "Authorization", required = false) String authorization) {
        thirdQidianService.sendToC(params);
        return Result.createSuccess();
    }

    /**
     * @param nonce
     * @param timestamp
     * @param msg_signature
     * @param textXml
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/callback", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public String callback(@RequestParam(value = "nonce", required = false) String nonce,
                           @RequestParam(value = "timestamp", required = false) String timestamp,
                           @RequestParam(value = "msg_signature", required = false) String msg_signature,
                           @RequestBody String textXml) throws Exception {
        log.info("callback textXml: {}", JSONObject.toJSONString(textXml));
        CommonUtil commonUtil = new CommonUtil();
        JSONObject externalItem = commonUtil.getEvents("vYNrO7BWxEr91oUkXXf9l4nzLuAXYWUqYiXs8rTT8wn", "token", msg_signature.toString(), timestamp, nonce,
                textXml);
        log.info("callback externalItem : {}", JSONObject.toJSONString(externalItem));
        guavaService.update(externalItem.getString("visitor_id"), externalItem.getString("qidian_ex1"));
        log.info("callback guava cache : {}", guavaService.findGoodsIdByVisitorId(externalItem.getString("visitor_id")));
        return JSONObject.toJSONString(externalItem);
    }
}
