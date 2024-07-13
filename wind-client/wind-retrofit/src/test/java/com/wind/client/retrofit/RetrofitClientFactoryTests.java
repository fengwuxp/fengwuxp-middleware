package com.wind.client.retrofit;

import com.wind.api.core.signature.ApiSecretAccount;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author wuxp
 * @date 2024-05-07 09:53
 **/
 class RetrofitClientFactoryTests {

     @Test
     void testBuild(){
         RetrofitClientFactory factory = RetrofitClientFactory.builder()
                 .baseUrl("https://wind.example.com")
                 .authenticationHeaderPrefix("Wind")
                 .account(ApiSecretAccount.sha256WithRsa("example", "test"))
                 .restful();
         Assertions.assertNotNull(factory);
     }
}
