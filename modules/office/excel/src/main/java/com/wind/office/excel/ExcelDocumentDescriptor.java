package com.wind.office.excel;

import com.wind.office.core.OfficeDocumentDescriptor;
import com.wind.office.core.OfficeDocumentType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.format.Formatter;

import java.util.List;
import java.util.Map;

/**
 * excel 文档导出描述符
 *
 * @author wuxp
 * @date 2023-10-27 18:31
 **/
@Getter
@Setter
@Accessors(chain = true)
public class ExcelDocumentDescriptor implements OfficeDocumentDescriptor {

    /**
     * 一个工作表导出的最大行数
     */
    private int sheetMaxRows = 65535;

    /**
     * 一个excel文件支持的最大sheet个数
     */
    private int maxSheetNum = 10;

    /**
     * 一次抓取数据的大小
     */
    private int fetchSize = 500;

    /**
     * 文档名称
     */
    private String name;

    /**
     * 导出列定义
     */
    private List<Cell> headers;

    /**
     * 文档类型
     */
    private OfficeDocumentType type;

    /**
     * 获取处理的总条数
     */
    private int total;

    /**
     * 获取处理成功的条数
     */
    private int successTotal;

    /**
     * 获取处理失败的条数
     */
    private int failureTotal;

    /**
     * 获取 sheet 的总数
     */
    private int sheetTotal;

    /**
     * 获取当前处理的 sheet index
     */
    private int currentSheetIndex;

    /**
     * 获取当前sheet 的总条数
     */
    private int currentSheetTotal;


    @Data
    public static class Cell {

        /**
         * 取值表达式
         * 默认使用 spring expression 表达式
         * {@link org.springframework.expression.spel.standard.SpelExpressionParser}
         */
        private String expression;

        /**
         * 当前列的标题
         */
        private String title;

        /**
         * 列宽
         */
        private Integer width;

        /**
         * 格式化数值的达式，仅在当前列对应的数据类型为数值类型是使用
         *
         * @see org.springframework.format.number.NumberStyleFormatter
         */
        private String numStylePattern;

        /**
         * map装换数据源，在该对象不为空时，将自动启用map转换器
         *
         * @key 属性名称
         * @value 需要转换的值
         */
        private Map<String, String> mapFormatterSource;

        /**
         * 列转换器
         */
        private Formatter<?> formatter;

    }

}
