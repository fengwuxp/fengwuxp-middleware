package com.wind.client.retrofit.converter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author wuxp
 * @date 2024-02-27 14:28
 **/
public class DefaultResponseConverterFactory extends Converter.Factory {

    private final ObjectMapper objectMapper;

    public DefaultResponseConverterFactory() {
        this(new ObjectMapper());
    }

    public DefaultResponseConverterFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new ApiResponseBodyConverter<>(type);
    }

    class ApiResponseBodyConverter<T> implements Converter<ResponseBody, T> {

        private final Type type;

        public ApiResponseBodyConverter(Type type) {
            this.type = type;
        }

        @Override
        public T convert(ResponseBody value) throws IOException {
            JavaType javaType = objectMapper.getTypeFactory().constructType(type);
            return objectMapper.readValue(value.bytes(), javaType);
        }

    }
}
