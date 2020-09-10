package com.cmh.aop.handler;

import com.cmh.aop.aspect.RepeatableCommitException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @Autowired
    HttpServletRequest request;

    /**
     * 异常日志记录
     */
    private void logErrorRequest(Exception e) {
        log.error("报错API URL: {}", request.getRequestURL().toString());
        log.error("异常: {}", e.getMessage());
    }

    /**
     * 返回错误页面
     */
    private String errorPage(Exception e, Model model) {
        model.addAttribute("error_msg", e.getMessage());
        return "test/errorPage";
    }

    /**
     * 参数未通过@Valid验证异常，
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    private String methodArgumentNotValid(MethodArgumentNotValidException e, Model model) {
        logErrorRequest(e);
        return errorPage(e, model);
    }

    /**
     * 参数格式有误
     */
    @ExceptionHandler({MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    private String typeMismatch(Exception e, Model model) {
        logErrorRequest(e);
        return errorPage(e, model);
    }

    /**
     * 缺少参数
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    private String missingServletRequestParameter(MissingServletRequestParameterException e, Model model) {
        logErrorRequest(e);
        return errorPage(e, model);
    }

    /**
     * 表单重复提交，进行重定向
     */
    @ExceptionHandler(RepeatableCommitException.class)
    private String repeatableCommitException(RepeatableCommitException e) {
        logErrorRequest(e);
        return "redirect:" + request.getRequestURI();
    }

    /**
     * 其他异常
     */
    @ExceptionHandler({HttpClientErrorException.class, IOException.class, Exception.class})
    private String commonExceptionHandler(Exception e, Model model) {
        logErrorRequest(e);
        return errorPage(e, model);
    }

    private HttpStatus getStatus() {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }

}