package com.qidian.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.qidian.demo.model.Result;
import com.qidian.demo.service.ThirdDemoService;
import com.qidian.demo.service.ThirdQidianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/app")
public class OpenApiController {


    @Autowired
    private ThirdDemoService thirdDemoService;

    @Autowired
    private ThirdQidianService thirdQidianService;


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
     * @param goodsId
     * @return
     */
    @RequestMapping("/getGoodsList")
    public Result getGoodsList(@RequestParam(value = "limit", required = false) Integer limit,
                               @RequestParam(value = "page", required = false) Integer page,
                               @RequestParam(value = "index", required = false) Integer index,
                               @RequestParam(value = "sort", required = false) String sort,
                               @RequestParam(value = "type", required = false) String type,
                               @RequestParam(value = "keywords", required = false) String keywords,
                               @RequestParam(value = "goodsId", required = false) String goodsId) {
        return Result.createSuccess(thirdDemoService.getGoodsList(limit, page, index, sort, goodsId, type, keywords));
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

        String custId = thirdQidianService.getCuinBySocialAccount(uid, "3");
        if (StringUtils.isEmpty(custId)) {
            return Result.createSuccess();
        }
        String openId = thirdQidianService.getOpenIdByCustId(custId);
        if (StringUtils.isEmpty(openId)) {
            return Result.createSuccess();
        }
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
}
