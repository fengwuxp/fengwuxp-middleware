package com.wind.security.core.rbac;

import com.wind.common.spring.ApplicationContextUtils;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collection;
import java.util.Collections;

/**
 * rbac 资源发生变更
 *
 * @author wuxp
 * @date 2023-09-26 09:57
 **/
@Getter
public class RbacResourceChangeEvent extends ApplicationEvent {

    private static final long serialVersionUID = -4959621536894633454L;

    private final Class<?> resourceType;

    private RbacResourceChangeEvent(Collection<String> ids, Class<?> resourceType) {
        super(ids == null ? Collections.emptyList() : ids);
        this.resourceType = resourceType;
    }

    /**
     * @return rbac 资源 id
     */
    @SuppressWarnings("unchecked")
    public Collection<String> getResourceIds() {
        return (Collection<String>) getSource();
    }

    public static void refreshPermission(Collection<String> ids) {
        publish(new RbacResourceChangeEvent(ids, RbacResource.Permission.class));
    }

    public static void refreshRole(Collection<String> ids) {
        publish(new RbacResourceChangeEvent(ids, RbacResource.Role.class));
    }

    public static void refreshUser(Collection<String> ids) {
        publish(new RbacResourceChangeEvent(ids, RbacResource.User.class));
    }

    private static void publish(RbacResourceChangeEvent event) {
        ApplicationContextUtils.getBean(ApplicationEventPublisher.class).publishEvent(event);
    }
}
