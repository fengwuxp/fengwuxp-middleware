package com.wind.server.flow;

import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.wind.sentinel.DefaultSentinelResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author wuxp
 * @date 2024-03-11 16:05
 **/
class SentinelWebInterceptorTests {

    SentinelWebInterceptor interceptor;

    @BeforeEach
    void setup() {
        FlowRuleManager.loadRules(Arrays.asList(
                mockFlowRule("GET /example/5qps", 5),
                mockFlowRule("GET /example/10qps", 10)
        ));
        DegradeRuleManager.loadRules(Arrays.asList(
                mockDegradeRule("GET /example/5qps", 5),
                mockDegradeRule("GET /example/10qps", 10)
        ));
        interceptor = new SentinelWebInterceptor(request -> createResource(String.format("%s %s", request.getMethod().toUpperCase(), request.getRequestURI())));
    }

    @Test
    void testFlow() throws Exception {
        concurrentQps("/example/5qps", 50);
    }

    private void concurrentQps(String uri, int concurrent) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(concurrent);
        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < concurrent; i++) {
            futures.add(executorService.submit(() -> {
                try {
                    assertQps(uri);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }));
        }
        for (Future<?> f : futures) {
            f.get();
        }
    }

    private void assertQps(String uri) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", uri);
        MockHttpServletResponse response = new MockHttpServletResponse();
        boolean b = interceptor.preHandle(request, response, new Object());
        if (b) {
            Assertions.assertNotNull(request.getAttribute(SentinelWebInterceptor.SENTINEL_ENTRY_ATTRIBUTE_NAME));
            interceptor.afterCompletion(request, response, new Object(), null);
            Assertions.assertNull(request.getAttribute(SentinelWebInterceptor.SENTINEL_ENTRY_ATTRIBUTE_NAME));
        }else {
            Assertions.assertNull(request.getAttribute(SentinelWebInterceptor.SENTINEL_ENTRY_ATTRIBUTE_NAME));
        }
    }

    private DefaultSentinelResource createResource(String name) {
        DefaultSentinelResource result = new DefaultSentinelResource();
        result.setName(name);
        result.setContextName("wind");
        result.setEntryType(EntryType.IN);
        result.setOrigin("wind");
        result.setResourceType(0);
        return result;
    }

    private static FlowRule mockFlowRule(String resource, int count) {
        FlowRule result = new FlowRule();
        result.setResource(resource);
        result.setCount(count);
        return result;
    }

    private static DegradeRule mockDegradeRule(String resource, int count) {
        DegradeRule result = new DegradeRule();
        result.setResource(resource);
        result.setCount(count);
        result.setTimeWindow(1000);
        return result;
    }
}
