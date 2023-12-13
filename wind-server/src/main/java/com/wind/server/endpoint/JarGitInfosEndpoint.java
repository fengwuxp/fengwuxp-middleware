package com.wind.server.endpoint;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

/**
 * 获取构建的 git info
 *
 * @author wuxp
 * @date 2023-12-13 14:04
 **/
@Component
@Endpoint(id = "windversions")
public class JarGitInfosEndpoint {

    private static final String GIT_INFOS_RESOURCE_LOCATION = "META-INF/git-infos.properties";

    @ReadOperation
    public Properties getGitInfos() throws IOException {
        return PropertiesLoaderUtils.loadAllProperties(GIT_INFOS_RESOURCE_LOCATION, null);
    }
}
