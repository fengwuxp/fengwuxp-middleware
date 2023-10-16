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

    REQUEST_SOURCE_FILTER(Ordered.HIGHEST_PRECEDENCE +1000, "RequestSourceIpFilter");


    private final int order;

    private final String desc;


}
