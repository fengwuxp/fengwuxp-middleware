package com.wind.sequence;

import com.wind.common.WindConstants;

/**
 * 序列存储器
 *
 * @author wuxp
 * @date 2024-03-31 09:41
 **/
public interface SequenceRepository {

    default SequenceGenerator getSequence(String sequenceName) {
        return getSequence(sequenceName, WindConstants.DEFAULT_TEXT);
    }

    /**
     * 获取一个序列生成器
     *
     * @param sequenceName 序列名称
     * @param groupName    分组名称
     * @return 序列生成器
     */
    SequenceGenerator getSequence(String sequenceName, String groupName);
}
