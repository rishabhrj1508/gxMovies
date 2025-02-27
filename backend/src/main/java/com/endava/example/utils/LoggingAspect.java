package com.endava.example.utils;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * LoggingAspect class provides AOP-based logging for controllers and service
 * implementation classes. It logs method entry, method exit with and without
 * result, and exceptions. Logging is applied to: - All methods inside
 * `com.endava.example.controller` package and its sub-packages - All methods
 * inside `com.endava.example.service.impl` package and its sub-packages
 */

@Aspect
@Component
public class LoggingAspect {

	/**
	 * Logger instance for logging method details.
	 */
	private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

	/**
	 * PointCut that matches all methods in controller and service implementation
	 * classes.
	 */
	@Pointcut("execution(* com.endava.example.controller..*(..)) || execution(* com.endava.example.service.impl..*(..))")
	public void applicationPointcut() {
	}

	/**
	 * Logs method entry details - name and arguments before execution.
	 *
	 * @param joinPoint Contains details of the current intercepted method.
	 */
	@Before(value = "applicationPointcut()")
	public void logBeforeMethod(JoinPoint joinPoint) {
		logger.info("Entering method: {} with arguments: {}", joinPoint.getSignature(),
				Arrays.toString(joinPoint.getArgs()));
	}

	/**
	 * Logs method exit details after execution completes , regardless of whether it
	 * returns successfully or throws an exception
	 *
	 * @param joinPoint Contains details of the current intercepted method.
	 */

	@After(value = "applicationPointcut()")
	public void logAfterMethodCompletes(JoinPoint joinPoint) {
		logger.info("Exiting method : {} ", joinPoint.getSignature());
	}

	/**
	 * Logs method exit details after successful execution.
	 *
	 * @param joinPoint Contains details of the intercepted method.
	 * @param result    The returned object from the method.
	 */
	@AfterReturning(value = "applicationPointcut()", returning = "result")
	public void logAfterMethodReturns(JoinPoint joinPoint, Object result) {
		logger.info("Method returned : {} with result: {}", joinPoint.getSignature(), result);
	}

	/**
	 * Logs exception details if a method throws an exception.
	 *
	 * @param joinPoint Contains details of the intercepted method.
	 * @param exception The thrown exception.
	 */
	@AfterThrowing(value = "applicationPointcut()", throwing = "exception")
	public void logException(JoinPoint joinPoint, Exception exception) {
		logger.error("Exception in method: {} with cause: {}", joinPoint.getSignature(), exception.getMessage(),
				exception);
	}

	/**
	 * Logs method execution with timestamp and calculates execution time.
	 * 
	 * @param joinPoint           Contains details of the intercepted method.
	 * @param proceedingJoinPoint The actual method being called.
	 * @return The result of the method execution.
	 * @throws Throwable If the method execution throws an exception.
	 */
	@Around(value = "applicationPointcut()")
	public Object logAroundMethod(JoinPoint joinPoint, ProceedingJoinPoint proceedingJoinPoint)
			throws Throwable {
		long startTime = System.currentTimeMillis(); // Capture start time
		logger.info("Method [{}] started at: {}", joinPoint.getSignature(), startTime);

		Object result = null;
		try {
			// Proceed with the actual method execution
			result = proceedingJoinPoint.proceed();
			long endTime = System.currentTimeMillis(); // Capture end time
			logger.info("Method [{}] completed at: {}. Execution time: {} ms", joinPoint.getSignature(), endTime,
					(endTime - startTime));
		} catch (Exception e) {
			long endTime = System.currentTimeMillis();
			logger.error("Method [{}] failed at: {}. Execution time: {} ms", joinPoint.getSignature(), endTime,
					(endTime - startTime));

			// Log the stack trace to identify the exact origin of the exception
			for (StackTraceElement element : e.getStackTrace()) {
				// Log the full stack trace for better insight
				logger.error("Exception originated from: at {} in {} (line {})", element.getMethodName(),
						element.getClassName(), element.getLineNumber());
			}

			throw e; // Re-throw exception after logging
		}

		return result;
	}

}
