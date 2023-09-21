package com.wuxp.mybatisplus.processor;

import com.baomidou.mybatisplus.annotation.TableField;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 给实体类生成字段常量类
 *
 * @author wuxp
 * @see MybatisPlusEntityClassProcessor
 */
@SupportedAnnotationTypes(value = {"com.baomidou.mybatisplus.annotation.TableName"})
public class MybatisPlusEntityClassProcessor extends AbstractProcessor {

    public static final String CLASS_NAME_PREFIX = "E_";

    private static final String LINE_CHAR = "_";

    private static final Pattern TO_LINE_REGEXP = Pattern.compile("([A-Z]+)");

    @Override
    @SuppressWarnings("unchecked")
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        for (String typeName : this.getSupportedAnnotationTypes()) {
            Class<? extends Annotation> entityAnnotationType = null;
            try {
                entityAnnotationType = (Class<? extends Annotation>) Class.forName(typeName);
            } catch (ClassNotFoundException e) {
                this.processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format("%s  can't found class %s", getClass().getSimpleName(), typeName));
            }
            if (entityAnnotationType == null) {
                continue;
            }
            processAnnotations(roundEnv.getElementsAnnotatedWith(entityAnnotationType));
        }

        return false;
    }

    private void processAnnotations(Set<? extends Element> elementList) {
        Elements elementUtils = this.processingEnv.getElementUtils();

        for (Element element : elementList) {
            // 只支持对类，接口，注解的处理，对字段不做处理
            if (!element.getKind().isClass() && !element.getKind().isInterface()) {
                continue;
            }
            TypeElement typeElement = (TypeElement) element;
            final String packageName = elementUtils.getPackageOf(typeElement).getQualifiedName().toString();
            JavaFile.Builder javaFileBuilder = JavaFile.builder(packageName, this.buildTypeSpec(typeElement));
            try {
                javaFileBuilder.build().writeTo(this.processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private TypeSpec buildTypeSpec(TypeElement element) {
        final String newSimpleClassName = getNewSimpleClassName(element);
        TypeSpec.Builder builder = TypeSpec.classBuilder(newSimpleClassName)
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC);
        String fullClassName = element.getQualifiedName().toString();
        TypeMirror superTypeMirror = element.getSuperclass();
        int i = 5;
        while (i-- > 0) {
            if (superTypeMirror instanceof NoType) {
                break;
            }
            Element supperElement = processingEnv.getTypeUtils().asElement(superTypeMirror);
            if (supperElement == null) {
                break;
            }
            TypeElement superTypeElement = (TypeElement) supperElement;
            String superClassName = superTypeElement.getQualifiedName().toString();
            if (Object.class.getName().equals(superClassName)) {
                break;
            }

            // 遍历超类的属性
            this.addFields(superTypeElement, fullClassName, builder);
        }
        this.addFields(element, fullClassName, builder);
        return builder.build();
    }


    private void addFields(TypeElement element, String fullClassName, TypeSpec.Builder builder) {

        element.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.FIELD)
                .filter(e -> !e.getModifiers().contains(Modifier.STATIC))
                .map(field -> {
                    FieldSpec fieldSpec = getFieldSpec(fullClassName, field);
                    return builder.fieldSpecs.contains(fieldSpec) ? null : fieldSpec;
                })
                .filter(Objects::nonNull)
                .forEach(builder::addField);
    }

    private FieldSpec getFieldSpec(String fullClassName, Element filed) {
        TableField annotation = filed.getAnnotation(TableField.class);
        String filedName = filed.getSimpleName().toString();
        String filedValue = annotation == null ? humpToLine(filedName) : annotation.value();
        return FieldSpec.builder(String.class, filedName, Modifier.FINAL, Modifier.PUBLIC, Modifier.STATIC)
                .initializer("$S", filedValue)
                .addJavadoc(String.format("%s#%s", fullClassName, filedName))
                .build();
    }


    private String getNewSimpleClassName(TypeElement element) {
        return CLASS_NAME_PREFIX + element.getSimpleName().toString();
    }


    /**
     * 驼峰格式的字符串转下划线
     *
     * @param str 驼峰格式内容
     * @return 驼峰格式的字符串转下划线的字符串内容
     * @link http://ifeve.com/google-guava/ CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, str);
     */
    public static String humpToLine(String str) {
        if (str == null || str.trim().isEmpty()) {
            return str;
        }

        StringBuilder text = new StringBuilder(str);
        Matcher matcher = TO_LINE_REGEXP.matcher(text);
        while (matcher.find()) {
            String replaceText = MessageFormat.format("{0}{1}", LINE_CHAR, text.substring(matcher.start(0), matcher.end(0)));
            text.replace(matcher.start(0), matcher.end(0), replaceText.toLowerCase());
            matcher = TO_LINE_REGEXP.matcher(text);
        }
        String result = text.toString();
        if (result.startsWith(LINE_CHAR)) {
            return result.substring(1);
        }
        return result;
    }

}
