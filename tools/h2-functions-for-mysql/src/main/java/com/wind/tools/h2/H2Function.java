package com.wind.tools.h2;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author wuxp
 * @date 2023-11-20 16:10
 **/
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class H2Function {

    /**
     * Mysql function alias，etc：version
     */
    private final String name;

    /**
     * The full class name for this function implementation
     */
    private final String fullClassName;
}
