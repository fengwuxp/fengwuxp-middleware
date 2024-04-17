package com.wind.security.core.rbac;

import com.wind.common.util.StringJoinSplitUtils;
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
    interface Permission extends RbacResource<String> {

        /**
         * @return 权限内容
         */
        Set<String> getAttributes();

        /**
         * 创建一个不可变的权限对象
         *
         * @param id    权限 id
         * @param name  权限名称
         * @param value 权限值，使用逗号分隔
         * @return 权限
         */
        static Permission immutable(String id, String name, String value) {
            Set<String> attributes = StringUtils.hasLength(value) ? StringJoinSplitUtils.split(value) : Collections.emptySet();
            return immutable(id, name, attributes);
        }

        static Permission immutable(String id, String name, Set<String> attributes) {
            return new ImmutablePermission(id, name, attributes);
        }
    }

    /**
     * rbac 角色资源
     */
    interface Role extends RbacResource<String> {


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
        static Role immutable(String id, String name, Set<String> permissions) {
            return new ImmutableRole(id, name, permissions);
        }
    }

    /**
     * rbac 用户资源
     */
    interface User extends RbacResource<String> {

    }

    @AllArgsConstructor
    @Getter
    class ImmutablePermission implements RbacResource.Permission {

        private static final long serialVersionUID = 6255678473411919964L;

        /**
         * id
         */
        private final String id;

        /**
         * 权限名称
         */
        private final String name;

        /**
         * 权限内容
         */
        private final Set<String> attributes;

        /**
         * 为了给序列化框架使用，提供一个空构造
         */
        ImmutablePermission() {
            this("", "", Collections.emptySet());
        }

    }

    @AllArgsConstructor
    @Getter
    class ImmutableRole implements RbacResource.Role {

        private static final long serialVersionUID = -6791142921724321619L;

        /**
         * id
         */
        private final String id;

        /**
         * 角色名称
         */
        private final String name;

        /**
         * 权限内容
         */
        private final Set<String> permissions;

        /**
         * 为了给序列化框架使用，提供一个空构造
         */
        ImmutableRole() {
            this("", "", Collections.emptySet());
        }
    }

}
