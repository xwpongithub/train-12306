package com.jiawa.train.member.exception;

import cn.hutool.core.util.StrUtil;
import com.jiawa.train.common.JsonResult;
import com.jiawa.train.common.annotation.IgnoreResponseSerializable;
import com.jiawa.train.common.exception.BusinessException;
import com.jiawa.train.common.toolkits.LogUtil;
import io.seata.core.context.RootContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.validation.BindException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public JsonResult handleSystemException(BusinessException e) {
        LogUtil.error(e);
        return JsonResult.error(e.getMessage())
                .setCode(e.getCode());
    }
//
//    @ExceptionHandler(ScheduleException.class)
//    public void handleScheduleException(ScheduleException e) {
//        LogUtils.error(e);
//    }
//
//    @ExceptionHandler(SaTokenException.class)
//    public JsonResult handleSaTokenException(SaTokenException nle) {
//        String message;
//        if (nle instanceof NotLoginException notLoginException) {
//            if (notLoginException.getType().equals(NotLoginException.NOT_TOKEN)) {
//                // code为11011
//                message = "令牌不存在或已过有效期，无法访问";
//            } else if (notLoginException.getType().equals(NotLoginException.INVALID_TOKEN)) {
//                message = "令牌无效，无法访问";
//            } else if (notLoginException.getType().equals(NotLoginException.TOKEN_TIMEOUT)) {
//                // code为11016
//                message = "令牌已过期,请重新登录";
//            } else if (notLoginException.getType().equals(NotLoginException.BE_REPLACED)) {
//                message = "您已在其他客户端上线";
//            } else if (notLoginException.getType().equals(NotLoginException.KICK_OUT)) {
//                message = "您已被管理员踢下线";
//            } else {
//                message = "未登录无法访问";
//            }
//            return JsonResult.error(message)
//                    .setCode("E"+nle.getCode());
//        }
//        return JsonResult.error(nle.getMessage())
//                .setCode("E"+nle.getCode());
//    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @IgnoreResponseSerializable
    public JsonResult handleException(Exception e) throws Exception {
        LogUtil.warn("【Seata全局事务 - handleException ID:{}】", RootContext.getXID());
        // 如果是在一次全局事务里出异常了，就不要包装返回，将异常抛给调用房，让调用方能够回滚事务
        if (StrUtil.isNotBlank(RootContext.getXID())){
            throw e;
        }
        LogUtil.error(e);
        return JsonResult
                .error("系统异常，请联系管理员")
                .setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

//    @ExceptionHandler(MissingServletRequestParameterException.class)
//    public JsonResult handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
//        return JsonResult.error(e.getMessage())
//                .setCode("E"+HttpStatus.BAD_REQUEST.value());
//    }
//
//    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
//    public JsonResult handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
//        return JsonResult
//                .error(e.getMessage())
//                .setCode("E"+HttpStatus.NOT_ACCEPTABLE.value());
//    }
//
//    /*
//     * Validator 参数校验异常处理
//     */
//    @ExceptionHandler(value = ConstraintViolationException.class)
//    public JsonResult handleMethodArgumentNotValidException(ConstraintViolationException e) {
//        var constraintViolations = e.getConstraintViolations();
//        for (var constraintViolation : constraintViolations) {
//            var pathImpl = (PathImpl) constraintViolation.getPropertyPath();
//            // 读取参数字段，constraintViolation.getMessage() 读取验证注解中的message值
//            var paramName = pathImpl.getLeafNode().getName();
//            var message = "参数{".concat(paramName).concat("}").concat(constraintViolation.getMessage());
//            return JsonResult.error(message)
//                    .set(CommonConstants.TRACE_ID, MDC.get(CommonConstants.TRACE_ID))
//                    .setCode(ErrorResponseEnum.PARAMETER_ERROR.getCode());
//        }
//        return JsonResult.error(
//                ErrorResponseEnum.PARAMETER_ERROR.getMsg()+":"+e.getMessage()
//        ).setCode(ErrorResponseEnum.PARAMETER_ERROR.getCode());
//    }
//
    /*
     * Validator 参数校验异常处理
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public JsonResult handleBindException(BindException e) {
        LogUtil.error(e);
        return JsonResult.error(e.getBindingResult().getAllErrors().get(0).getDefaultMessage()).setCode(4001);
    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public JsonResult handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
//        var errorMsg = new StringBuilder();
//        var re = ex.getBindingResult();
//        for (var error : re.getAllErrors()) {
//            errorMsg.append(error.getDefaultMessage()).append(",");
//        }
//        errorMsg.delete(errorMsg.length() - 1, errorMsg.length());
//        return JsonResult.error(ErrorResponseEnum.PARAMETER_ERROR.getMsg() + " : " + errorMsg)
//                .setCode(ErrorResponseEnum.PARAMETER_ERROR.getCode());
//    }
//
//    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
//    public JsonResult handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
//        return JsonResult
//                .error(e.getMessage())
//                .setCode("E"+HttpStatus.NOT_IMPLEMENTED.value());
//    }
//
//    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
//    public JsonResult handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException e) {
//        return JsonResult
//                .error(e.getMessage())
//                .setCode("E"+HttpStatus.NOT_ACCEPTABLE.value());
//    }
//
//    @ExceptionHandler(RateLimitException.class)
//    public JsonResult handleRateLimitException(RateLimitException e){
//        return JsonResult
//                .error(e.getMessage())
//                .setCode("E"+HttpStatus.NOT_ACCEPTABLE.value());
//    }
//
//    @ExceptionHandler(MultipartException.class)
//    public JsonResult handleMultipartException(MultipartException e) {
//        return JsonResult
//                .error(e.getMessage())
//                .setCode("E"+HttpStatus.NOT_ACCEPTABLE.value());
//    }
//
//    @ExceptionHandler(IllegalArgumentException.class)
//    public JsonResult handleIllegalArgumentException(IllegalArgumentException e){
//        return JsonResult
//                .error(e.getMessage())
//                .setCode("E"+HttpStatus.NOT_ACCEPTABLE.value());
//    }

}
