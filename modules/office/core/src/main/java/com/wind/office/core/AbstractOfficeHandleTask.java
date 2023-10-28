package com.wind.office.core;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;

/**
 * @author wuxp
 * @date 2023-10-27 19:55
 **/
@Getter
@Setter
public abstract class AbstractOfficeHandleTask implements OfficeHandleTask {

    private final String id = RandomStringUtils.randomAlphanumeric(20);

    private final String name;

    private OfficeTaskState state = OfficeTaskState.WAIT;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    protected AbstractOfficeHandleTask(String name) {
        this.name = name;
    }
}
