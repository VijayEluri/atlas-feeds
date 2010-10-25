package org.atlasapi.beans;

import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.ImmutableMap;

public class AtlasErrorSummary {
	
	private static class AtlasExceptionBuilder {
		private final String friendly;
		private final int httpStatus;

		public AtlasExceptionBuilder(String friendlyCode, int httpStatusCode) {
			this.friendly = friendlyCode;
			this.httpStatus = httpStatusCode;
		}

		public String friendly() {
			return friendly;
		}

		public int httpStatus() {
			return httpStatus;
		}
		
		public AtlasErrorSummary build(Exception exception) {
			return new AtlasErrorSummary(exception).withErrorCode(friendly()).withStatusCode(httpStatus());
		}
	}
	
	private static class ExceptionExposingAtlasExceptionBuilder extends AtlasExceptionBuilder{

		public ExceptionExposingAtlasExceptionBuilder(String friendlyCode, int httpStatusCode) {
			super(friendlyCode, httpStatusCode);
		}
		
		public AtlasErrorSummary build(Exception exception) {
			return new AtlasErrorSummary(exception).withErrorCode(friendly()).withStatusCode(httpStatus()).withMessage(exception.getMessage());
		}
	}
	
	private static Map<Class<? extends Exception>, AtlasExceptionBuilder> exceptionCodes = exceptionMap();
	
	public static AtlasErrorSummary forException(Exception exception) {
		AtlasExceptionBuilder builder = exceptionCodes.get(exception.getClass());
		if (builder != null) {
			return builder.build(exception);
		} else {
			return new AtlasErrorSummary(exception);
		}
	}
	
	private static Map<Class<? extends Exception>, AtlasExceptionBuilder> exceptionMap() {
		return ImmutableMap.<Class<? extends Exception>, AtlasExceptionBuilder>of(
			IllegalArgumentException.class, new ExceptionExposingAtlasExceptionBuilder("BAD_QUERY_ATTRIBUTE", HttpServletResponse.SC_BAD_REQUEST),
			ProjectionException.class, new ExceptionExposingAtlasExceptionBuilder("BAD_PROJECTION", HttpServletResponse.SC_BAD_REQUEST)
		);
	}

	private String id;
	private Exception exception;
	private String errorCode = "INTERNAL_ERROR";
	private int statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
	private String message = "An internal server error occurred";
	
	public AtlasErrorSummary(Exception exception) {
		this.exception = exception;
		this.id = UUID.randomUUID().toString();
	}
	
	public AtlasErrorSummary() { /* for JSON and XML tools */ }

	public String id() {
		return id;
	}
	
	public Exception exception() {
		return exception;
	}

	public AtlasErrorSummary withStatusCode(int statusCode) {
		this.statusCode = statusCode;
		return this;
	}

	public int statusCode() {
		return statusCode;
	}
	
	public AtlasErrorSummary withErrorCode(String errorCode) {
		this.errorCode = errorCode;
		return this;
	}
	public String errorCode() {
		return errorCode;
	}
	
	public AtlasErrorSummary withMessage(String message) {
		this.message = message;
		return this;
	}
	public String message() {
		return this.message;
	}
}