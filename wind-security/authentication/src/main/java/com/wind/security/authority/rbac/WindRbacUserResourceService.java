package com.wind.security.authority.rbac;

import com.wind.security.core.rbac.RbacResource;
import com.wind.security.core.rbac.RbacResourceSupplier;
import com.wind.security.core.rbac.RbacUserResourceService;
import lombok.AllArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author wuxp
 * @date 2024-08-16 13:38
 **/
@AllArgsConstructor
public class WindRbacUserResourceService implements RbacUserResourceService {

    private final RbacResourceSupplier rbacResourceSupplier;

    @Override
    public Set<String> findOwnerRoleIds(String userId) {
        long currentTimeMillis = System.currentTimeMillis();
        return rbacResourceSupplier.getUserRoles()
                .getOrDefault(userId, Collections.emptySet())
                .stream()
                // 按照授权过期时间过滤数据
                .filter(userRole -> userRole.getExpireTime() == null || userRole.getExpireTime() > currentTimeMillis)
                .map(RbacResource.UserRole::getRoleId)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<RbacResource.Role> getOwnerRoles(String userId) {
        // TODO 待优化
        Set<String> roleIds = findOwnerRoleIds(userId);
        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptySet();
        }
        return rbacResourceSupplier.getRoles().stream().filter(role -> roleIds.contains(role.getId())).collect(Collectors.toSet());
    }
}
