package com.wind.server.configuration;

import com.wind.server.trace.TraceFilter;
import com.wind.server.web.filters.RequestSourceIpFilter;
import com.wind.server.web.filters.RestfulErrorHandleFilter;
import com.wind.server.web.filters.WindWebFilterOrdered;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.wind.common.WindConstants.ENABLED_NAME;
import static com.wind.common.WindConstants.RESTFUL_ERROR_FILTER_EXPRESSION;
import static com.wind.common.WindConstants.TRACE_FILTER_EXPRESSION;
import static com.wind.common.WindConstants.TRUE;

/**
 * web filter 相关配置
 *
 * @author wuxp
 * @date 2023-09-23 07:13
 **/
@Configuration
public class WebFilterConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = RESTFUL_ERROR_FILTER_EXPRESSION, name = ENABLED_NAME, havingValue = TRUE, matchIfMissing = true)
    public FilterRegistrationBean<RestfulErrorHandleFilter> restfulErrorHandleFilter() {
        FilterRegistrationBean<RestfulErrorHandleFilter> result = new FilterRegistrationBean<>();
        result.setFilter(new RestfulErrorHandleFilter());
        result.setOrder(WindWebFilterOrdered.RESTFUL_ERROR_FILTER.getOrder());
        return result;
    }

    @Bean
    public FilterRegistrationBean<RequestSourceIpFilter> requestSourceIpFilter() {
        FilterRegistrationBean<RequestSourceIpFilter> result = new FilterRegistrationBean<>();
        result.setFilter(new RequestSourceIpFilter());
        result.setOrder(WindWebFilterOrdered.REQUEST_SOURCE_FILTER.getOrder());
        return result;
    }

    @Bean
    @ConditionalOnProperty(prefix = TRACE_FILTER_EXPRESSION, name = ENABLED_NAME, havingValue = TRUE, matchIfMissing = true)
    public FilterRegistrationBean<TraceFilter> traceFilter() {
        FilterRegistrationBean<TraceFilter> result = new FilterRegistrationBean<>();
        result.setFilter(new TraceFilter());
        result.setOrder(WindWebFilterOrdered.TRACE_FILTER.getOrder());
        return result;
    }

}
