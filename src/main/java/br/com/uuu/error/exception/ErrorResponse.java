package br.com.uuu.error.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ErrorResponse {

	private Integer status;

    private Object message;

    private LocalDateTime timestamp;

    public ErrorResponse(Integer status) {
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }
    
    public static ErrorResponse status(HttpStatusCode statusCode) {
    	return new ErrorResponse(statusCode.value());
    }

    public static ErrorResponse badRequest() {
    	return new ErrorResponse(HttpStatus.BAD_REQUEST.value());
    }
    
    public static ErrorResponse internalServerError() {
    	return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
    
    public ErrorResponse setMessage(Object message) {
    	this.message = message;
    	return this;
    }

}
