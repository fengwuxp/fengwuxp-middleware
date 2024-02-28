package com.wind.client.retrofit.converter;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.wind.common.exception.BaseException;
import com.wind.common.exception.DefaultExceptionCode;
import com.wind.core.api.ImmutableApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author wuxp
 * @date 2024-02-27 14:43
 **/
@AllArgsConstructor
@Slf4j
public class DefaultResponseCallAdapterFactory extends CallAdapter.Factory {

    private final ObjectMapper objectMapper;

    private final boolean restful;

    public DefaultResponseCallAdapterFactory(ObjectMapper objectMapper) {
        this(objectMapper, false);
    }

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        return new RespCallAdapter<>(returnType);
    }

    class RespCallAdapter<R> implements CallAdapter<R, Object> {

        private final Type responseType;

        public RespCallAdapter(Type responseType) {
            this.responseType = responseType;
        }

        @Override
        public Type responseType() {
            return responseType;
        }

        @Override
        public Object adapt(Call<R> call) {
            try {
                Response<R> response = call.execute();
                return response.isSuccessful() ? response.body() : parseErrorResp(response.errorBody());
            } catch (IOException exception) {
                throw new BaseException(DefaultExceptionCode.COMMON_ERROR, "convert resp body error", exception);
            }
        }

        private Object parseErrorResp(ResponseBody errorBody) throws IOException {
            JavaType javaType = objectMapper.getTypeFactory().constructType(ImmutableApiResponse.class);
            ObjectReader reader = objectMapper.readerFor(javaType);
            try {
                return reader.readValue(errorBody.charStream());
            } finally {
                errorBody.close();
            }
        }
    }


}
