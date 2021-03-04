package com.sendi.telecom.results;

import java.util.List;

/**
 * @param <T>
 * @author hqx
 */
public class PageResult<T> {
    /**
     * total.
     */
    private long total;
    /**
     * data.
     */
    private List<T> list;

    /**
     * Construct .
     *
     * @param total
     * @param list
     */
    public PageResult(long total, List<T> list) {
        this.total = total;
        this.list = list;
    }
}
