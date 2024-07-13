package com.wind.tools.mybatisflex.codegen;

import com.wind.common.exception.BaseException;
import com.wind.common.exception.DefaultExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * 文件操作相关工具
 *
 * @author wuxp
 */
@Slf4j
public final class CodegenFileUtils {

    private CodegenFileUtils() {
        throw new AssertionError();
    }

    /**
     * 递归创建目录
     *
     * @param directoryPath 目录路径
     */
    public static void createDirectoryRecursively(String directoryPath) {
        File file = new File(directoryPath);
        if (!file.exists()) {
            boolean r = file.mkdirs();
            log.debug("创建目录：{}，结果：{}", directoryPath, r ? "成功" : "失败");
        }
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     *
     * @param directoryPath 被删除目录的文件路径
     */
    public static void deleteDirectory(String directoryPath) {
        if (log.isDebugEnabled()) {
            log.debug("删除目录 = {}", directoryPath);
        }
        try {
            FileUtils.deleteDirectory(new File(directoryPath));
        } catch (IOException exception) {
            throw new BaseException(DefaultExceptionCode.COMMON_ERROR, "删除目录异常，directoryPath = " + directoryPath, exception);
        }
    }

    /**
     * 将包名转换为路径
     *
     * @param packageName 包名
     * @return 文件路径 part
     */
    public static String toFilepathPart(String packageName) {
        return packageName.replace(".", File.separator);
    }
}
