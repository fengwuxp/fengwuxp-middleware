package com.wind.server.configuration;

import com.wind.server.web.filters.RequestSourceIpFilter;
import com.wind.server.web.filters.WindWebFilterOrdered;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wuxp
 * @date 2023-09-23 07:13
 **/
@Configuration
public class WebFilterConfiguration {


    @Bean
    public FilterRegistrationBean<RequestSourceIpFilter> requestSourceIpFilter() {
        FilterRegistrationBean<RequestSourceIpFilter> result = new FilterRegistrationBean<>();
        result.setFilter(new RequestSourceIpFilter());
        result.setOrder(WindWebFilterOrdered.REQUEST_SOURCE_FILTER.getOrder());
        return result;
    }
}
