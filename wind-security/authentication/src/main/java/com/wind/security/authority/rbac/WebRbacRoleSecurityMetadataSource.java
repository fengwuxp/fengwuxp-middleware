package com.wind.security.authority.rbac;

import com.wind.security.core.rbac.RbacResource;
import com.wind.security.core.rbac.RbacResourceService;
import com.wind.security.web.utils.RequestMatcherUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.wind.security.WebSecurityConstants.REQUEST_REQUIRED_ROLES_ATTRIBUTE_NAME;

/**
 * 1：通过请求 method、requestUri 获取需要的权限
 * 1.1：如果为配置权限则表示不需要控制权限
 * 2：通过权限交换到拥有该权限的角色列表
 *
 * @author wuxp
 * @date 2023-09-25 15:26
 * @see org.springframework.security.access.vote.RoleVoter
 * @see org.springframework.security.access.AccessDecisionManager
 * @see org.springframework.security.web.access.intercept.FilterSecurityInterceptor
 **/
@AllArgsConstructor
public class WebRbacRoleSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private final RbacResourceService rbacResourceService;

    /**
     * 默认的角色值前缀
     *
     * @see org.springframework.security.access.vote.RoleVoter#getRolePrefix
     */
    private final String rolePrefix;

    /**
     * 请求权限匹配是否匹配所有权限
     */
    private final boolean matchesRequestAllPermission;

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        // 获取当前的请求
        FilterInvocation filterInvocation = (FilterInvocation) object;
        String[] permissions = matchesRequestPermissions(filterInvocation.getRequest());
        if (ObjectUtils.isEmpty(permissions)) {
            // 请求路径没有配置权限，表明该请求接口可以任意访问
            return Collections.emptyList();
        }
        Set<String> roleIds = getRolesByPermissionIds(permissions);
        filterInvocation.getRequest().setAttribute(REQUEST_REQUIRED_ROLES_ATTRIBUTE_NAME, roleIds);
        String[] authorities = roleIds
                .stream()
                .map(role -> rolePrefix + role)
                .distinct()
                .toArray(String[]::new);
        return SecurityConfig.createList(authorities);
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        String[] authorities = new String[0];
        return SecurityConfig.createList(authorities);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

    /**
     * 匹配当前请请你需要的权限
     *
     * @param request 请求
     * @return 当前请求需要的角色权限
     */
    private String[] matchesRequestPermissions(HttpServletRequest request) {
        Set<String> permissionIds = new HashSet<>();
        for (Map.Entry<String, Set<RequestMatcher>> entry : getRequestPermissionMatchers().entrySet()) {
            if (RequestMatcherUtils.matches(entry.getValue(), request)) {
                // 权限匹配
                permissionIds.add(entry.getKey());
                if (matchesRequestAllPermission) {
                    //非匹配所有权限模式， 匹配到了则返回
                    break;
                }
            }
        }
        return permissionIds.toArray(new String[0]);
    }

    /**
     * 通过权限标识查找权限
     *
     * @param permissionIds 权限标识
     * @return 有 {@param permissionIds} 权限的角色
     */
    private Set<String> getRolesByPermissionIds(String... permissionIds) {
        List<RbacResource.Role> roles = rbacResourceService.getAllRoles();
        Set<String> result = Arrays.stream(permissionIds)
                .map(id -> findRoleByPermissionId(id, roles))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        return Collections.unmodifiableSet(result);
    }

    private String findRoleByPermissionId(String permissionId, List<RbacResource.Role> roles) {
        for (RbacResource.Role role : roles) {
            if (role.getPermissions().contains(permissionId)) {
                return role.getId();
            }
        }
        return null;
    }

    private Map<String, Set<RequestMatcher>> getRequestPermissionMatchers() {
        Map<String, Set<RequestMatcher>> result = new HashMap<>();
        rbacResourceService.getAllPermissions().forEach(permission -> result.put(permission.getId(), RequestMatcherUtils.convertMatchers(permission.getAttributes())));
        return result;
    }

}
