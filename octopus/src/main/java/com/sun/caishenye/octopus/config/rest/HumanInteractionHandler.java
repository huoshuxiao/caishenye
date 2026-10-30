package com.sun.caishenye.octopus.config.rest;

import okhttp3.Request;
import okhttp3.Response;

public interface HumanInteractionHandler {
    /**
     * 返回 null 表示无法恢复；
     * 返回新 Request 表示可以继续请求。
     */
    Request handle(Response response, Request originalRequest);
}
