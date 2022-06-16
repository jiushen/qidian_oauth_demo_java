package com.qidian.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.qidian.demo.model.Result;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/index")
public class IndexController {

    /**
     * @param username
     * @param password
     * @return
     */
    @RequestMapping("/demo")
    public Result demo(@RequestParam(value = "username", required = true) String username,
                       @RequestParam(value = "password", required = true) String password,
                       @RequestHeader(value = "Authorization", required = true) String authorization) {
        JSONObject object = new JSONObject();
        object.put("username", username);
        object.put("password", password);
        object.put("authorization", authorization);
        return Result.createSuccess(object);
    }
}
