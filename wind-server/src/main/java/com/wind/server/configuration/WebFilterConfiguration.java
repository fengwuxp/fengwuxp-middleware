package com.wind.server.configuration;

import com.wind.server.web.filters.RequestSourceIpFilter;
import com.wind.server.web.filters.RestfulErrorHandleFilter;
import com.wind.server.web.filters.WindWebFilterOrdered;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * web filter 相关配置
 *
 * @author wuxp
 * @date 2023-09-23 07:13
 **/
@Configuration
public class WebFilterConfiguration {

    @Bean
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
}
