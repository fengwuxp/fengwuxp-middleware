package com.wind.tools.mybatisflex.codegen.model;

import com.wind.common.WindConstants;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 代码生成描述对象
 *
 * @author wuxp
 * @date 2023-10-06 16:37
 **/
@Data
@Accessors(chain = true)
public class GenCodeInfo {


    /**
     * 生成的类名称
     */
    private String javaClassName;

    /**
     * 输出类型
     */
    private CodegenOutPutType codegenOutPutType;

    /**
     * 类注释、描述
     */
    private String comment;

    /**
     * 实体名称
     */
    private String name;

    /**
     * 字段
     */
    private List<FieldInfo> fields;

    /**
     * 有乐观锁字段
     */
    private boolean existsVersionField;

    /**
     * 额外属性
     */
    private Map<String, Object> extraProps;

    public String getFirstLowName() {
        String[] chars = name.split(WindConstants.EMPTY);
        chars[0] = chars[0].toLowerCase();
        return String.join(WindConstants.EMPTY, chars);
    }

    public Set<String> getDependencies() {
        return getFields().stream()
                .map(FieldInfo::getDependencies)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    public GenCodeInfo reJavaClassName(String pattern) {
        return duplicate()
                .setJavaClassName(String.format(pattern, getName()));
    }

    public GenCodeInfo duplicate() {
        return new GenCodeInfo()
                .setJavaClassName(getJavaClassName())
                .setCodegenOutPutType(getCodegenOutPutType())
                .setName(this.getName())
                .setComment(this.getComment())
                .setFields(new ArrayList<>(this.getFields()))
                .setExtraProps(getExtraProps());
    }

    /**
     * @return 是否使用了乐观锁
     */
    public boolean isExistsVersionField() {
        return fields.stream().anyMatch(field -> Objects.equals("version", field.getName()) && field.getClassType() == Integer.class);
    }

    /**
     * @return 是否使用了逻辑删除
     */
    public boolean isUseLogicDeleted() {
        return getFields().stream().anyMatch(field -> Objects.equals(field.name, "deleted") && field.getClassType() == Boolean.class);
    }

    /**
     * 字段信息
     */
    @Data
    @Accessors(chain = true)
    public static class FieldInfo {

        /**
         * 注释
         */
        private String comment;

        /**
         * 字段名称
         */
        private String name;

        /**
         * 字段类型
         */
        private String type;

        private Class<?> classType;

        private Set<String> dependencies = new LinkedHashSet<>();

        private List<String> annotations = new ArrayList<>();

        public String getFirstUpCaseName() {
            String[] chars = name.split(WindConstants.EMPTY);
            chars[0] = chars[0].toUpperCase();
            return String.join(WindConstants.EMPTY, chars);
        }

        public FieldInfo rename(String name, String comment) {
            return new FieldInfo()
                    .setName(name)
                    .setType(getType())
                    .setClassType(getClassType())
                    .setAnnotations(getAnnotations())
                    .setDependencies(getDependencies())
                    .setComment(comment);
        }

    }
}
