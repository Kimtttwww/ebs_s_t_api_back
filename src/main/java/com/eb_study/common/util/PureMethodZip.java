package com.eb_study.common.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

@Component
public class PureMethodZip {
	public Type getGenericClass(HandlerMethod handlerMethod) {
		return getGenericClass(handlerMethod.getMethod().getGenericReturnType());
	}

	public Type getGenericClass(Type type) {
		if (type instanceof ParameterizedType parameterizedType) {
			return parameterizedType.getActualTypeArguments()[0];
		}
		return null;
	}

	public boolean isListType(Type type) {
		if (type instanceof ParameterizedType parameterizedType) {
			return parameterizedType.getRawType() == List.class;
		}
		return false;
	}
}
