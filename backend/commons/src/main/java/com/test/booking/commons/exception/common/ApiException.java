package com.test.booking.commons.exception.common;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiException extends RuntimeException {
    private ResponseBody body;
    private int status;

    public ApiException(String code, int status, String message) {
        this.status = status;
        this.body = ResponseBody.builder().code(code).message(message).build();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ResponseBody {
        private String code;
        private String message;

        @Override
        public String toString() {
           return "{\"code\":\"" + code + "\",\"message\":\"" + message + "\"}";
        }
    }
}