package com.wind.archetype.web.controller;

import com.wind.archetype.core.AppConstants;
import com.wind.archetype.face.ExampleService;
import com.wind.server.web.restful.RestfulApiRespFactory;
import com.wind.server.web.supports.ApiResp;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wind
 **/
@RestController
@AllArgsConstructor
@RequestMapping(AppConstants.API_V1_PREFIX + "/examples")
@Tag(name = "Example")
@Slf4j
public class ExampleController {

    private final ExampleService exampleService;

    @GetMapping
    public ApiResp<String> sayHello() {
        exampleService.sayHello();
        return RestfulApiRespFactory.ok("Hello Word");
    }


}
