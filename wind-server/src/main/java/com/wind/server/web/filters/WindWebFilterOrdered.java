package com.wind.server.web.filters;

import com.wind.common.enums.DescriptiveEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.Ordered;

/**
 * @author wuxp
 * @date 2023-09-23 07:19
 **/
@AllArgsConstructor
@Getter
public enum WindWebFilterOrdered implements Ordered, DescriptiveEnum {


    RESTFUL_ERROR_FILTER(Ordered.HIGHEST_PRECEDENCE + 30, "RestfulErrorHandleFilter"),


    INDEX_HTML_FILTER(Ordered.HIGHEST_PRECEDENCE + 100, "RestfulErrorHandleFilter"),

    REQUEST_SOURCE_FILTER(Ordered.HIGHEST_PRECEDENCE + 1000, "RequestSourceIpFilter"),

    TRACE_FILTER(Ordered.HIGHEST_PRECEDENCE + 1010, "TraceFilter"),

    REQUEST_SIGN_FILTER(Ordered.HIGHEST_PRECEDENCE + 1030, "RequestSignFilter"),

    ;


    private final int order;

    private final String desc;


}
