package com.sendi.telecom.execption;

import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.sendi.telecom.results.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hqx
 */
@RestControllerAdvice
@Slf4j
public class GzsendiExceptionHandler {

	private final Pattern duplicateEntryPattern = Pattern.compile("Duplicate entry '(\\S*)' .*");
	/**
	 * 开启钉钉机器人通知功能.
	 */
	private final boolean isActiveDev;
	/**
	 * 钉钉机器人url.  触发条件是 内容中包含 git.
	 */
	private final String robotUrl = "https://oapi.dingtalk.com/robot/send?access_token=c1f4e9cc9351949fcc38d3b7147187fab7cb92e1d872968906b7c55441d5b366";

	public GzsendiExceptionHandler(Environment environment) {
//		this.isActiveDev = "dev".equals(environment.getProperty("spring.profiles.active"));
		isActiveDev = true;
	}

	/**
	 * 处理自定义异常
	 */
	@ExceptionHandler(GzsendiException.class)
	public Result<?> handleRRException(GzsendiException e){
		log.error(e.getMessage(), e);
		return Result.error(e.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public Result<?> handleException(Exception e, HttpServletRequest request) {
		log.error(e.getMessage(), e);

		String errorMsg;

		if (e instanceof HttpRequestMethodNotSupportedException) {
			errorMsg = e.getMessage();
		}

		//请求信息读取及绑定到对象时的异常，此时不会进入WebLogAspect
        else if (e instanceof HttpMessageNotReadableException) {
			HttpMessageNotReadableException hmnre = (HttpMessageNotReadableException) e;
			errorMsg = "请求入参解析失败，请参考解析异常检查您的请求再重试 : " + hmnre.getMessage();
		}

		//请求参数绑定到对象成功后，但校验失败的时异常，此时不会进入WebLogAspect
		else if (e instanceof MethodArgumentNotValidException) {
			MethodArgumentNotValidException argNotValidEx = (MethodArgumentNotValidException) e;
			//Method method = argNotValidEx.getParameter().getMethod();
			//参数校验失败时，可直接获取各种校验注解上的提示信息并可直接拿到入参target从而得到requestId
			BindingResult bindingResult = argNotValidEx.getBindingResult();
			errorMsg = handleArgNotValidException(bindingResult);
		}

		//非json入参时参数绑定失败，会进入WebLogAspect的情况
		else if (e instanceof ConstraintViolationException) {
			Set<ConstraintViolation<?>> violations = ((ConstraintViolationException) e).getConstraintViolations();
			errorMsg = violations.iterator().next().getMessage();
		}

		//非json入参时参数绑定失败，不会进入WebLogAspect的情况
		else if (e instanceof BindException) {
			BindingResult bindingResult = ((BindException) e).getBindingResult();
			errorMsg = handleArgNotValidException(bindingResult);
		} else if (e instanceof IllegalArgumentException || e instanceof IllegalStateException) {
			errorMsg = e.getMessage();
		} else if (e.getCause() instanceof GzsendiException) {
			errorMsg = e.getCause().getMessage();
		} else {
			errorMsg = "操作失败, 请稍后再尝试";
			//数据库操作异常（sql有误等）
			if (e instanceof DataAccessException) {
				Throwable rootCause = ((DataAccessException) e).getRootCause();
				if (rootCause instanceof SQLIntegrityConstraintViolationException) {
					String message = rootCause.getMessage();
					Matcher matcher = duplicateEntryPattern.matcher(message);
					if (matcher.find()) {
						String duplicateValue = matcher.group(1);
						errorMsg = "参数值重复，请检查修改后再操作: " + duplicateValue;
					} else {
						errorMsg = "数据处理异常, 请稍后再操作";
					}
				} else {
					errorMsg = "数据处理异常, 请稍后再操作";
				}
			}
			//未知异常时发送钉钉消息
//			if (isActiveDev
//					&& !"127.0.0.1".equalsIgnoreCase(request.getServerName())
//					&& !"localhost".equalsIgnoreCase(request.getServerName())) {

			if(isActiveDev){
				try {
					@SuppressWarnings("unchecked")
					Map<String, String> requestMap = (Map<String, String>) request.getAttribute("requestMap");
					if (requestMap == null) {
						requestMap = new LinkedHashMap<>();
						String url = request.getRequestURL().toString();
						String remoteAddr = request.getRemoteAddr();
						String httpMethod = request.getMethod();
						requestMap.put("url", url);
						requestMap.put("remoteAddr", remoteAddr);
						requestMap.put("httpMethod", httpMethod);
					}
					//待把url定义成属性,
					HttpRequest hr = HttpUtil.createPost(robotUrl);
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					PrintWriter writer = new PrintWriter(out);
					e.printStackTrace(writer);
					writer.close();
					String exStackTrace = out.toString();
					Map<String, Object> textMap = new LinkedHashMap<>();
					String content = JSONUtil.toJsonStr(requestMap) + "\nEXCEPTION: \n" +
							String.join("\n", Arrays.copyOf(exStackTrace.split("\n"), 20));
					textMap.put("content", "git:来自服务器"+request.getServerName()+"的报错\r\n"+content); //触发关键字git
					Map<String, Object> robotMsgMap = new LinkedHashMap<>();
					robotMsgMap.put("msgtype", "text");
					robotMsgMap.put("text", textMap);
					hr.body(JSONUtil.toJsonStr(robotMsgMap), ContentType.JSON.toString());
					hr.execute();
				} catch (Exception ex) {
					//ignore
				}
			}
		}

		return Result.error(errorMsg);
	}

	/**
	 * 处理参数校验失败的异常
	 *
	 * @param bindingResult 数据绑定结果，如有异常，可从中获取绑定失败的说明（如参数校验注解上声明的message信息）
	 */
	private String handleArgNotValidException(BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			ObjectError objectError = bindingResult.getAllErrors().get(0);
			if (objectError != null) {
				return objectError.getDefaultMessage();
			}
		}
		return "未知校验异常";
	}

}
