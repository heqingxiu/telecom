package com.sendi.telecom.results;

/**
 * @author hqx
 * @param <T>
 */
public class RequestParams<T> {
    /**
     * page number.
     */
    private Integer pageNum = 1;
    /**
     * page size .
     */
    private Integer pageSize = 10;

    /*** 如果前台不传递requestId，则我们系统自动生成requestId，故定义此前缀值. */
    public static final String AUTO_GENERATED_REQUEST_ID_PREFIX = "@QZMAN_";
    /**
     * data.
     */
    private T data;

    public Integer getPageNum() {
        return pageNum == 0 ? 1 : pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
