package com.wind.client.retrofit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.wind.api.core.ApiResponse;
import com.wind.api.core.ImmutableApiResponse;
import com.wind.client.retrofit.converter.JacksonConverterFactory;
import com.wind.client.retrofit.converter.JacksonResponseCallAdapterFactory;
import com.wind.common.exception.ApiClientException;
import com.wind.common.exception.AssertUtils;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import retrofit2.Retrofit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import static com.wind.common.WindDateFormatPatterns.HH_MM_SS;
import static com.wind.common.WindDateFormatPatterns.YYYY_MM_DD;
import static com.wind.common.WindDateFormatPatterns.YYYY_MM_DD_HH_MM_SS;

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
     * @param baseUrl 请求接口根路径
     * @param builder okhttp 客户端 builder
     * @return 工厂实例
     */
    public static RetrofitClientFactory defaults(String baseUrl, OkHttpClient.Builder builder) {
        return defaults(baseUrl, builder.build(), buildObjectMapper());
    }

    /**
     * 创建默认风格 API RetrofitClientFactory
     *
     * @param baseUrl 请求接口根路径
     * @param client  okhttp 客户端
     * @param objectMapper jackson objectMapper
     * @return 工厂实例
     */
    private static RetrofitClientFactory defaults(String baseUrl, OkHttpClient client, ObjectMapper objectMapper) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addCallAdapterFactory(new JacksonResponseCallAdapterFactory(objectMapper))
                .addConverterFactory(new JacksonConverterFactory(objectMapper))
                .build();
        return of(retrofit);
    }

    /**
     * 创建 restful 风格 API RetrofitClientFactory
     *
     * @param baseUrl 请求接口根路径
     * @param builder okhttp 客户端 builder
     * @return 工厂实例
     */
    public static RetrofitClientFactory restful(String baseUrl, OkHttpClient.Builder builder) {
        return restful(baseUrl, builder.build());
    }

    /**
     * 创建 restful 风格 API RetrofitClientFactory
     *
     * @param baseUrl      请求接口根路径
     * @param builder      okhttp 客户端 builder
     * @param objectMapper jackson objectMapper
     * @return 工厂实例
     */
    public static RetrofitClientFactory restful(String baseUrl, OkHttpClient.Builder builder, ObjectMapper objectMapper) {
        return restful(baseUrl, builder.build(), objectMapper);
    }

    /**
     * 创建 restful 风格 API RetrofitClientFactory
     *
     * @param baseUrl 请求接口根路径
     * @param client  okhttp 客户端
     * @return 工厂实例
     */
    private static RetrofitClientFactory restful(String baseUrl, OkHttpClient client) {
        return restful(baseUrl, client, buildObjectMapper());
    }

    /**
     * 创建 restful 风格 API RetrofitClientFactory
     *
     * @param baseUrl      请求接口根路径
     * @param client       okhttp 客户端
     * @param objectMapper jackson objectMapper
     * @return 工厂实例
     */
    private static RetrofitClientFactory restful(String baseUrl, OkHttpClient client, ObjectMapper objectMapper) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addCallAdapterFactory(new JacksonResponseCallAdapterFactory(objectMapper, ImmutableApiResponse.class, RetrofitClientFactory::defaultResponseExtractor))
                .addConverterFactory(new JacksonConverterFactory(objectMapper, ImmutableApiResponse.class, RetrofitClientFactory::defaultResponseExtractor))
                .build();
        return of(retrofit);
    }

    @NotNull
    private static ObjectMapper buildObjectMapper() {
        ObjectMapper result = new ObjectMapper();
        // 配置忽略未知属性
        result.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        result.registerModule(buildJavaTimeModule());
        return result;
    }

    @NotNull
    private static JavaTimeModule buildJavaTimeModule() {
        JavaTimeModule result = new JavaTimeModule();
        result.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS)));
        result.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(YYYY_MM_DD)));
        result.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(HH_MM_SS)));
        result.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS)));
        result.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(YYYY_MM_DD)));
        result.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(HH_MM_SS)));
        return result;
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
