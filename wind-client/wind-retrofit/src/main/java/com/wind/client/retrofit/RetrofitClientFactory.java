package com.wind.client.retrofit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wind.client.retrofit.converter.DefaultResponseCallAdapterFactory;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author wuxp
 * @date 2024-02-27 11:32
 **/
public class RetrofitClientFactory {

    private final Retrofit retrofit;

    private RetrofitClientFactory(Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    public <T> T create(Class<T> apiClientClass) {
        return retrofit.create(apiClientClass);
    }

    public static RetrofitClientFactory of(Retrofit retrofit) {
        return new RetrofitClientFactory(retrofit);
    }

    public static OkHttpClient.Builder httpClientBuilder() {
        // 设置超时时间，这里以秒为单位
        return new OkHttpClient.Builder()
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);
    }

    public static RetrofitClientFactory defaults(String baseUrl, OkHttpClient.Builder clientBuilder) {
        return defaults(baseUrl, clientBuilder.build());
    }

    public static RetrofitClientFactory defaults(String baseUrl, OkHttpClient client) {
        ObjectMapper objectMapper = new ObjectMapper();
        // 配置忽略未知属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .addCallAdapterFactory(new DefaultResponseCallAdapterFactory(objectMapper))
                .build();
        return of(retrofit);
    }

}
