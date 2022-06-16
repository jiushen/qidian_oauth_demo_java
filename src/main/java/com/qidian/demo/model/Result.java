package com.qidian.demo.model;

import java.beans.Transient;
import java.io.Serializable;

/**
 * 返回结果
 *
 * @author Joe
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1956544564021082972L;

    public static final int SUCCESS_CODE = 1;// 成功

    public static final int ERROR_CODE = 9999;// 未知错误

    /**
     * 成功
     */
    @SuppressWarnings("rawtypes")
    public static final Result SUCCESS = createSuccess();

    /**
     * 结果体
     */
    protected T message;

    /**
     * 状态码
     */
    protected int code;

    /**
     * 信息
     */
    protected String result;

    private Result() {
        super();
    }

    public static <T> Result<T> create() {
        return new Result<>();
    }

    public static <T> Result<T> create(int code) {
        Result<T> r = create();
        r.setCode(code);
        if (code == 1) {
            r.setResult("ok");
        } else {
            r.setResult("fail");
        }
        return r;
    }

    @SuppressWarnings("unchecked")
    public static final <T> Result<T> success() {
        return SUCCESS;
    }

    public static <T> Result<T> createSuccess() {
        return create(SUCCESS_CODE);
    }

    public static <T> Result<T> createSuccess(T message) {
        Result<T> r = createSuccess();
        r.setMessage(message);
        return r;
    }

    public static <T> Result<T> createSuccess(T data, String result) {
        Result<T> r = createSuccess(data);
        r.setResult(result);
        return r;
    }

    public T getMessage() {
        return message;
    }

    public Result<T> setMessage(T message) {
        this.message = message;
        return this;
    }

    public int getCode() {
        return code;
    }

    public Result<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public String getResult() {
        return result;
    }

    public Result<T> setResult(String result) {
        this.result = result;
        return this;
    }

    @Transient
    public boolean isSuccess() {
        return SUCCESS_CODE == code;
    }
}
