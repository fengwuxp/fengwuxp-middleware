package com.wind.sentinel.util;

import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.wind.sentinel.DefaultSentinelResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

/**
 * @author wuxp
 * @date 2024-06-19 10:29
 **/
class SentinelFlowLimitUtilsTests {

    @Test
    void testFlowControl() throws Exception {
        DefaultSentinelResource resource = new DefaultSentinelResource();
        resource.setName("test");
        resource.setContextName("test");
        resource.setResourceType(0);
        resource.setEntryType(EntryType.IN);
        Consumer<Exception> consumer = SentinelFlowLimitUtils.flowControl(resource);
        Assertions.assertEquals(resource.getName(), ContextUtil.getContext().getName());
        consumer.accept(null);
        Assertions.assertNull(ContextUtil.getContext());
    }
}
