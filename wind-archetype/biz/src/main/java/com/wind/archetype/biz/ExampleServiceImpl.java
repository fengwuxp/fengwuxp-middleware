package com.wind.archetype.biz;

import com.wind.archetype.face.ExampleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author wind
 **/
@Service
@Slf4j
@AllArgsConstructor
public class ExampleServiceImpl implements ExampleService {

    @Override
    public String sayHello() {
        String result = "Hello Word!!";
        log.info(result);
        return result;
    }
}
