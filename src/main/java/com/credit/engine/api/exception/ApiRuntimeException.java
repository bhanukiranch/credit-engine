package com.credit.engine.api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.springframework.http.HttpStatus;

public class ApiRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private HttpStatus httpStatus;
    private ApiRuntimeException.ErrorResponse errorResponse;

    public ApiRuntimeException(HttpStatus httpStatus) {
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        this.httpStatus = httpStatus;
        this.errorResponse = new ApiRuntimeException.ErrorResponse();
    }

    public ApiRuntimeException(HttpStatus httpStatus, String errorCode, String errorMessage) {
        super(errorMessage);
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        this.httpStatus = httpStatus;
        this.errorResponse = new ApiRuntimeException.ErrorResponse(errorCode, errorMessage);
    }

    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    public ApiRuntimeException.ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }

    @JsonInclude(Include.NON_NULL)
    public class ErrorResponse {
        private String errorCode;
        private String errorMessage;

        public ErrorResponse() {
        }

        public ErrorResponse(String errorCode, String errorMessage) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }

        public String getErrorCode() {
            return this.errorCode;
        }

        public String getErrorMessage() {
            return this.errorMessage;
        }
    }
}
