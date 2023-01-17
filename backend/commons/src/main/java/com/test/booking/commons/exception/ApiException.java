package com.test.booking.commons.exception;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RegisterForReflection
public class ApiException extends RuntimeException {
    private ResponseBody body;
    private int status;

    public ApiException(String code, int status, String message) {
        this.status = status;
        this.body = ResponseBody.builder().code(code).message(message).build();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
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
            return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
        }
    }
}