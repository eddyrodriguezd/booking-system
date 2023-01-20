package com.test.booking.commons.exception.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.test.booking.commons.config.mapper.MapperConfig;
import com.test.booking.commons.exception.JsonParsingException;
import lombok.*;

@Getter
@Setter
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
    public static class ResponseBody {
        private String code;
        private String message;

        @Builder
        public ResponseBody(String code, String message) {
            this.code = code;
            this.message = message;
        }

        @Override
        public String toString() {
            try {
                return MapperConfig.getObjectMapper().writeValueAsString(this);
            } catch (JsonProcessingException e) {
                throw new JsonParsingException(this.getClass().getName());
            }
        }
    }
}