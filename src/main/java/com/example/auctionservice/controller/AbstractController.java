package com.example.auctionservice.controller;

import com.example.auctionservice.pojo.response.ErrorResponse;
import com.example.auctionservice.pojo.response.Response;

public abstract class AbstractController {
    protected <T> Response<?> getSuccessResponse(T t) {
        return new Response<>(true, t, null);
    }

    protected <T> Response<?> getFailureResponse(T t, ErrorResponse error) {
        return new Response<>(true, t, error);
    }
}
