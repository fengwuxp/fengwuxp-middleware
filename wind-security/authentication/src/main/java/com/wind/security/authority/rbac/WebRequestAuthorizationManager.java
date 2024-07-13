package com.wind.security.authority.rbac;

import com.wind.security.core.SecurityAccessOperations;
import com.wind.security.core.rbac.RbacResourceService;
import com.wind.security.web.util.RequestMatcherUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import static com.wind.security.WebSecurityConstants.REQUEST_REQUIRED_PERMISSIONS_ATTRIBUTE_NAME;

/**
 * 基于请求的 rbac 权限控制
 *
 * @author wuxp
 * @date 2023-10-23 08:52
 **/
@AllArgsConstructor
@Slf4j
public class WebRequestAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private static final AuthorizationDecision ACCESS_PASSED = new AuthorizationDecision(true);

    private static final AuthorizationDecision ACCESS_DENIED = new AuthorizationDecision(false);

    private final RbacResourceService rbacResourceService;

    private final SecurityAccessOperations securityAccessOperations;

    /**
     * 请求权限匹配时是否匹配所有权限
     * {@link #matchesRequestPermissions}
     */
    private final boolean matchesRequestAllPermission;

    @Nullable
    @Override
    public AuthorizationDecision check(Supplier<Authentication> supplier, RequestAuthorizationContext context) {
        if (!supplier.get().isAuthenticated()) {
            // 未登录
            return ACCESS_PASSED;
        }
        if (securityAccessOperations.isSupperAdmin()) {
            // 超级管理员
            log.debug("supper admin, allow access");
            return ACCESS_PASSED;
        }
        Set<String> permissions = matchesRequestPermissions(context.getRequest());
        if (ObjectUtils.isEmpty(permissions)) {
            log.debug("no permission required, allow access");
            // 请求路径没有配置权限，表明该请求接口可以任意访问
            return ACCESS_PASSED;
        }
        if (log.isDebugEnabled()) {
            log.debug("request resource ={} {}, required permissions = {}", context.getRequest().getMethod(), context.getRequest().getRequestURI(), permissions);
        }
        context.getRequest().setAttribute(REQUEST_REQUIRED_PERMISSIONS_ATTRIBUTE_NAME, permissions);
        return securityAccessOperations.hasAnyAuthority(permissions.toArray(new String[0])) ? ACCESS_PASSED : ACCESS_DENIED;
    }

    /**
     * 匹配当前请请你需要的权限
     *
     * @param request 请求
     * @return 当前请求需要的角色权限
     */
    private Set<String> matchesRequestPermissions(HttpServletRequest request) {
        Set<String> result = new HashSet<>();
        for (Map.Entry<String, Set<RequestMatcher>> entry : getRequestPermissionMatchers().entrySet()) {
            if (RequestMatcherUtils.matches(entry.getValue(), request)) {
                // 权限匹配
                result.add(entry.getKey());
                if (!matchesRequestAllPermission) {
                    //非匹配所有权限模式， 匹配到了则返回
                    break;
                }
            }
        }
        return result;
    }

    private Map<String, Set<RequestMatcher>> getRequestPermissionMatchers() {
        Map<String, Set<RequestMatcher>> result = new HashMap<>();
        rbacResourceService.getPermissions().stream()
                .filter(Objects::nonNull)
                .forEach(permission -> result.put(permission.getId(), RequestMatcherUtils.convertMatchers(permission.getAttributes())));
        return result;
    }
}
