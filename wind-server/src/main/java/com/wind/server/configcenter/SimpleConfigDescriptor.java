package com.wind.server.configcenter;

import com.wind.common.enums.ConfigFileType;
import com.wind.configcenter.core.ConfigRepository;
import lombok.Data;

/**
 * @author wuxp
 * @date 2023-10-15 13:27
 **/
@Data
public class SimpleConfigDescriptor implements ConfigRepository.ConfigDescriptor {

    private String name;

    private String label;

    private String group;

    private ConfigFileType fileType;

    /**
     * 是否允许刷新
     */
    private boolean refreshable;

    public static SimpleConfigDescriptor of(String name, String group) {
        SimpleConfigDescriptor result = new SimpleConfigDescriptor();
        result.setGroup(group);
        ConfigFileType configFileType = ConfigFileType.parse(name);
        if (configFileType == null) {
            result.setName(name);
            result.setFileType(ConfigFileType.PROPERTIES);
        } else {
            result.setName(name.substring(0, name.indexOf(configFileType.getFileExtension()) - 1));
            result.setFileType(configFileType);
        }
        return result;
    }

}
