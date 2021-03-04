package com.sendi.telecom.results;

import lombok.Data;

/**
 * @param <T>
 * @author hqx
 */
@Data
public class Result<T> {
    /***
     * success.
     */
    private boolean success = true;
    /**
     * message.
     */
    private String message = "操作成功！";
    /**
     * return code.
     */
    private Integer code = 200;
    /**
     * timestamp.
     */
    private long timestamp = System.currentTimeMillis();
    /**
     * data.
     */
    private T data;

    /**
     * no parameter constructor.
     */
    public Result() {

    }

    /**
     * error return.
     *
     * @param message
     * @return
     */
    public Result<T> error500(String message) {
        this.message = message;
        this.code = RespCodeConstant.INTERNAL_SERVER_ERROR_500;
        this.success = false;
        return this;
    }

    /**
     * success return.
     *
     * @param message
     * @return
     */
    public Result<T> success(String message) {
        this.message = message;
        this.code = RespCodeConstant.OK_200;
        this.success = true;
        return this;
    }

    /**
     * ok return .
     *
     * @return
     */
    public static Result<Object> ok() {
        Result<Object> r = new Result<Object>();
        r.setSuccess(true);
        r.setCode(RespCodeConstant.OK_200);
        r.setMessage("成功");
        return r;
    }

    /**
     * ok return.
     *
     * @param msg
     * @return
     */
    public static Result<Object> ok(String msg) {
        Result<Object> r = new Result<Object>();
        r.setSuccess(true);
        r.setCode(RespCodeConstant.OK_200);
        r.setMessage(msg);
        return r;
    }

    /**
     * .
     *
     * @param data
     * @return
     */
    public static Result<Object> ok(Object data) {
        Result<Object> r = new Result<Object>();
        r.setSuccess(true);
        r.setCode(RespCodeConstant.OK_200);
        r.setData(data);
        return r;
    }

    /**
     * .
     *
     * @param msg
     * @return
     */
    public static Result<Object> error(String msg) {
        return error(RespCodeConstant.INTERNAL_SERVER_ERROR_500, msg);
    }

    /**
     * .
     *
     * @param data
     * @param message
     * @return
     */
    public static Result<Object> error(Object data, String message) {
        Result<Object> r = new Result<Object>();
        r.setSuccess(false);
        r.setCode(RespCodeConstant.INTERNAL_SERVER_ERROR_500);
        r.setData(data);
        r.setMessage(message);
        return r;
    }

    /**
     * .
     *
     * @param code
     * @param msg
     * @return
     */
    public static Result<Object> error(int code, String msg) {
        Result<Object> r = new Result<Object>();
        r.setCode(code);
        r.setMessage(msg);
        r.setSuccess(false);
        return r;
    }
}
