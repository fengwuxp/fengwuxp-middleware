package com.wind.client.retrofit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wind.client.retrofit.converter.DefaultResponseCallAdapterFactory;
import com.wind.client.retrofit.converter.DefaultResponseConverterFactory;
import com.wind.common.exception.ApiClientException;
import com.wind.common.exception.AssertUtils;
import com.wind.api.core.ApiResponse;
import com.wind.api.core.ImmutableApiResponse;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import retrofit2.Retrofit;

import java.util.concurrent.TimeUnit;

/**
 * RetrofitClient 服务工厂
 *
 * <p>For example,
 *
 * <pre><code>
 *   OkHttpClient.Builder clientBuilder = RetrofitClientFactory.httpClientBuilder()
 *           .addInterceptor(new XxxInterceptor());
 *    RetrofitClientFactory factory = RetrofitClientFactory.restful("you api base address", clientBuilder);
 *    ExampleRetrofitClient exampleRetrofitClient = factory.create(ExampleRetrofitClient.class);
 * </code></pre>
 *
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

    /**
     * 创建默认风格 API RetrofitClientFactory
     *
     * @param baseUrl       请求接口根路径
     * @param clientBuilder okhttp 客户端 builder
     * @return 工厂实例
     */
    public static RetrofitClientFactory defaults(String baseUrl, OkHttpClient.Builder clientBuilder) {
        return defaults(baseUrl, clientBuilder.build());
    }

    /**
     * 创建默认风格 API RetrofitClientFactory
     *
     * @param baseUrl 请求接口根路径
     * @param client  okhttp 客户端
     * @return 工厂实例
     */
    public static RetrofitClientFactory defaults(String baseUrl, OkHttpClient client) {
        ObjectMapper objectMapper = buildObjectMapper();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addCallAdapterFactory(new DefaultResponseCallAdapterFactory(objectMapper))
                .addConverterFactory(new DefaultResponseConverterFactory(objectMapper))
                .build();
        return of(retrofit);
    }

    /**
     * 创建 restful 风格 API RetrofitClientFactory
     *
     * @param baseUrl       请求接口根路径
     * @param clientBuilder okhttp 客户端 builder
     * @return 工厂实例
     */
    public static RetrofitClientFactory restful(String baseUrl, OkHttpClient.Builder clientBuilder) {
        return restful(baseUrl, clientBuilder.build());
    }


    /**
     * 创建 restful 风格 API RetrofitClientFactory
     *
     * @param baseUrl 请求接口根路径
     * @param client  okhttp 客户端
     * @return 工厂实例
     */
    public static RetrofitClientFactory restful(String baseUrl, OkHttpClient client) {
        ObjectMapper objectMapper = buildObjectMapper();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addCallAdapterFactory(new DefaultResponseCallAdapterFactory(objectMapper, ImmutableApiResponse.class, RetrofitClientFactory::defaultResponseExtractor))
                .addConverterFactory(new DefaultResponseConverterFactory(objectMapper, ImmutableApiResponse.class, RetrofitClientFactory::defaultResponseExtractor))
                .build();
        return of(retrofit);
    }

    @NotNull
    private static ObjectMapper buildObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 配置忽略未知属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @NotNull
    private static Object defaultResponseExtractor(Object data) {
        if (data instanceof ApiResponse) {
            ApiResponse<?> response = (ApiResponse<?>) data;
            AssertUtils.state(response.isSuccess(), () -> new ApiClientException(response, response.getErrorMessage()));
            return response.getData();
        }
        return data;
    }

}
