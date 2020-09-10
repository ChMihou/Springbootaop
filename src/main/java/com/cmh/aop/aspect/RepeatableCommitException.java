package com.cmh.aop.aspect;

/**
 * @author ankoye@qq.com
 */
public class RepeatableCommitException extends RuntimeException {
    private Integer code;

    private String message;

    public RepeatableCommitException(String message) {
        this(1, message);
    }

    public RepeatableCommitException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

}
