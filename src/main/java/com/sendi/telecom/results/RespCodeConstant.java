package com.sendi.telecom.results;

/**
 * @author hqx
 */
public interface RespCodeConstant {
    /**
     * 401.
     */
    int NOAUTHRIZED = 401; //无权限
    /**
     * 503.
     */
    int TIMEOUT = 503; //超时服务响应
    /**
     * 999.
     */
    int BACK_ERVICE_DOWN = 999; //后端服务降级响应
    /**
     * 500.
     */
    Integer INTERNAL_SERVER_ERROR_500 = 500; //服务响应错误
    /**
     * 200.
     */
    Integer OK_200 = 200; //响应成功

    /**
     * ignore.
     */
    void get();
}
