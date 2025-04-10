package com.eb_study.board.free.config;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.lang.reflect.Type;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.eb_study.common.util.PureMethodZip;

import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class FreeBoardBeanConfig {
	private final PureMethodZip pureMethodZip;

	@Bean
	public OperationCustomizer defaultResponseCustomizer() {
		return (operation, handlerMethod) -> {
			Type dataType = pureMethodZip.getGenericClass(handlerMethod);
			ApiResponses responses = operation.getResponses();

			if (responses == null) {
				responses = new ApiResponses();
				operation.setResponses(responses);
			}

			Schema<Object> schema = null;
			if (pureMethodZip.isListType(dataType)) {
				if (pureMethodZip.getGenericClass(dataType) instanceof Class<?> dataGenericClass)
					schema = new ArraySchema().$ref("#/components/schemas/" + dataGenericClass.getSimpleName());
			} else {
				if (dataType instanceof Class<?> dataClass)
					schema = new Schema<>().$ref("#/components/schemas/" + dataClass.getSimpleName());
			}

			responses.addApiResponse("500", new ApiResponse().description("server error"));
			responses.addApiResponse("200", new ApiResponse().description("ok")
					.content(new Content().addMediaType(APPLICATION_JSON_VALUE, new MediaType().schema(schema))));

			return operation;
		};
	}
}
