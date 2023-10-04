package com.wind.security.core.rbac;

import com.google.common.collect.ImmutableSet;
import com.wind.common.WindConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

/**
 * rbac 资源定义
 *
 * @param <I> id 类型
 * @author wuxp
 * @date 2023-09-26 07:41
 **/
public interface RbacResource<I extends Serializable> extends Serializable {

    /**
     * @return 唯一标识
     */
    I getId();

    /**
     * @return 用于展示的名称
     */
    String getName();

    /**
     * rbac 权限资源
     */
    interface Permission<I extends Serializable> extends RbacResource<I> {

        /**
         * @return 权限类型，例如：菜单、接口、数据
         */
        String getType();

        /**
         * @return 权限内容
         */
        Set<String> getAttributes();

        /**
         * 创建一个不可变的权限对象
         *
         * @param id    权限 id
         * @param name  权限名称
         * @param type  权限类型
         * @param value 权限值，使用逗号分隔
         * @return 权限
         */
        static <I extends Serializable> Permission<I> immutable(I id, String name, String type, String value) {
            Set<String> attributes = StringUtils.hasLength(value) ? Collections.emptySet() : ImmutableSet.copyOf(value.split(WindConstants.COMMA));
            return immutable(id, name, type, attributes);
        }

        static <I extends Serializable> Permission<I> immutable(I id, String name, String type, Set<String> attributes) {
            return new ImmutablePermission<>(id, name, type, attributes);
        }
    }

    /**
     * rbac 角色资源
     */
    interface Role<I extends Serializable> extends RbacResource<I> {

        /**
         * 角色关联的权限
         *
         * @return 权限列表
         */
        Set<String> getPermissions();

        /**
         * 创建一个不可变的角色对象
         *
         * @param id          权限 ID
         * @param name        权限名称
         * @param permissions 权限值
         * @return 角色
         */
        static <I extends Serializable> Role<I> immutable(I id, String name, Set<String> permissions) {
            return new ImmutableRole<>(id, name, permissions);
        }
    }

    /**
     * rbac 用户资源
     */
    interface User<I extends Serializable> extends RbacResource<I> {

    }

    @AllArgsConstructor
    @Getter
    class ImmutablePermission<I extends Serializable> implements RbacResource.Permission<I> {

        private static final long serialVersionUID = 6255678473411919964L;

        /**
         * id
         */
        private final I id;

        /**
         * 权限名称
         */
        private final String name;

        /**
         * 权限类型
         */
        private final String type;

        /**
         * 权限内容
         */
        private final Set<String> attributes;

    }

    @AllArgsConstructor
    @Getter
    class ImmutableRole<I extends Serializable> implements RbacResource.Role<I> {

        private static final long serialVersionUID = -6791142921724321619L;

        /**
         * id
         */
        private final I id;

        /**
         * 角色名称
         */
        private final String name;

        /**
         * 权限内容
         */
        private final Set<String> permissions;

    }

}
