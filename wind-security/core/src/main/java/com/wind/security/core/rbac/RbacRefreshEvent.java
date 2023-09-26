package com.wind.security.core.rbac;

import com.wind.common.spring.ApplicationContextUtils;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collection;

/**
 * @author wuxp
 * @date 2023-09-26 09:57
 **/
@Getter
public class RbacRefreshEvent extends ApplicationEvent {

    private static final long serialVersionUID = -4959621536894633454L;

    private final Class<?> resourceType;

    private RbacRefreshEvent(Collection<String> ids, Class<?> resourceType) {
        super(ids);
        this.resourceType = resourceType;
    }

    /**
     * 获取 rbac 资源 id
     * @return
     */
    public Collection<String> getResourceIds() {
        return (Collection<String>) getSource();
    }

    public static void permission(Collection<String> ids) {
        RbacRefreshEvent event = new RbacRefreshEvent(ids, RbacResource.Permission.class);
        ApplicationContextUtils.getBean(ApplicationEventPublisher.class).publishEvent(event);

    }

    public static void role(Collection<String> ids) {
        RbacRefreshEvent event = new RbacRefreshEvent(ids, RbacResource.Role.class);
        ApplicationContextUtils.getBean(ApplicationEventPublisher.class).publishEvent(event);
    }
}
