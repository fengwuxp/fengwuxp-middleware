package com.wuxp.mybatisplus.processor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

class MybatisPlusEntityClassProcessorTests extends AbstractProcessorTest {

    private static final String PACKAGE_PATH = "src/test/java/com/wuxp/mybatisplus/processor/entity/";

    private static final List<String> CLASSES = getFiles(PACKAGE_PATH);

    @Test
    @Disabled
    void testProcess() throws Exception {
        process(MybatisPlusEntityClassProcessor.class, CLASSES);
        Assertions.assertNotNull(PACKAGE_PATH);
    }
}