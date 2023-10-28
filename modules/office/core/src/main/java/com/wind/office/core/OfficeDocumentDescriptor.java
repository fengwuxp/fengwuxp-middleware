package com.wind.office.core;

/**
 * 办公文档描述符
 *
 * @author wuxp
 * @date 2023-10-27 18:20
 **/
public interface OfficeDocumentDescriptor {

    /**
     * 文档名称
     *
     * @return 名称
     */
    String getName();

    /**
     * 文档类型
     *
     * @return 类型
     */
    OfficeDocumentType getType();
}
