package com.wind.archetype.face;

import com.wind.archetype.AbstractServiceTest;
import com.wind.archetype.biz.ExampleServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author wind
 **/
@ContextConfiguration(classes = WindObjectDigestObjectMaskPrinterExampleServiceImplTests.TestConfig.class)
@Slf4j
 class WindObjectDigestObjectMaskPrinterExampleServiceImplTests extends AbstractServiceTest {

    @Autowired
    private ExampleService exampleService;

    @Test
    void testSayHello(){
        Assertions.assertNotNull(exampleService.sayHello());
    }


    @Import(ExampleServiceImpl.class)
    static class TestConfig{

    }

}
