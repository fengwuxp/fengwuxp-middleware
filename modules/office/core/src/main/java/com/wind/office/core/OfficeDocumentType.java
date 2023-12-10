package com.wind.office.core;

import com.wind.common.enums.DescriptiveEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 办公文档类型
 *
 * @author wuxp
 * @date 2023-10-27 18:21
 **/
@AllArgsConstructor
@Getter
public enum OfficeDocumentType implements DescriptiveEnum {

    WORD("word 文档"),

    EXCEL("excel 文档"),

    PPT("ppt 文档"),

    PDF("pdf 文档");

    private final String desc;
}
