package com.wind.security.core.rbac;

import com.wind.common.spring.ApplicationContextUtils;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

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
        super(ids == null ? Collections.emptyList() : Collections.unmodifiableCollection(ids));
        this.resourceType = resourceType;
    }

    /**
     * @return rbac 资源 id
     */
    @SuppressWarnings("unchecked")
    public Collection<String> getResourceIds() {
        return (Collection<String>) getSource();
    }

    public static void refreshPermission(Collection<Long> ids) {
        refreshPermissionTextIds(idAsText(ids));
    }

    public static void refreshRole(Collection<Long> ids) {
        refreshRoleTextIds(idAsText(ids));
    }

    public static void refreshUser(Collection<Long> ids) {
        refreshUserTextIds(idAsText(ids));
    }

    public static void refreshPermissionTextIds(Collection<String> ids) {
        publish(new RbacResourceChangeEvent(ids, RbacResource.Permission.class));
    }

    public static void refreshRoleTextIds(Collection<String> ids) {
        publish(new RbacResourceChangeEvent(ids, RbacResource.Role.class));
    }

    public static void refreshUserTextIds(Collection<String> ids) {
        publish(new RbacResourceChangeEvent(ids, RbacResource.User.class));
    }

    private static void publish(RbacResourceChangeEvent event) {
        ApplicationContextUtils.getBean(ApplicationEventPublisher.class).publishEvent(event);
    }

    private static Collection<String> idAsText(Collection<Long> ids) {
        return ids.stream().map(String::valueOf).collect(Collectors.toSet());
    }
}
