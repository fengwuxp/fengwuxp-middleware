package com.wind.security.core.rbac;

import com.google.common.collect.ImmutableSet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Set;

/**
 * @author wuxp
 * @date 2023-09-26 07:41
 **/
public interface RbacResource {

    String getId();

    interface Permission extends RbacResource {

        /**
         * @return 权限类型
         */
        String getType();

        /**
         * @return 权限内容
         */
        Set<String> getAttributes();

        static Permission immutable(String id, String type, String value) {
            Set<String> attributes = StringUtils.hasLength(value) ? Collections.emptySet() : ImmutableSet.copyOf(value.split(","));
            return immutable(id, type, attributes);
        }

        static Permission immutable(String id, String type, Set<String> attributes) {
            return new ImmutablePermission(id, type, attributes);
        }
    }

    interface Role extends RbacResource {

        /**
         * 角色关联的权限
         *
         * @return 权限列表
         */
        Set<String> getPermissions();

        static Role immutable(String id, Set<String> permissions) {
            return new ImmutableRole(id, permissions);
        }
    }

    interface User extends RbacResource {

    }

    @AllArgsConstructor
    @Getter
    class ImmutablePermission implements RbacResource.Permission {

        /**
         * id
         */
        private final String id;

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
    class ImmutableRole implements RbacResource.Role {

        /**
         * id
         */
        private final String id;

        /**
         * 权限内容
         */
        private final Set<String> permissions;

    }

}
