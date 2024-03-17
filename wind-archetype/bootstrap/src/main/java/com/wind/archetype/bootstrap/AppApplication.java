package com.wind.archetype.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 应用启动类
 *
 * @author wind
 */
@SpringBootApplication(scanBasePackages = {"com.wind.archetype.**", "com.wind.**"})
public class AppApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

}