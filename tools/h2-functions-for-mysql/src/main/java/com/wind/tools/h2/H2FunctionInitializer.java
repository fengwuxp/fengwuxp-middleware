package com.wind.tools.h2;

import com.wind.tools.h2.mysql.H2MysqlFunctions;
import lombok.extern.slf4j.Slf4j;
import org.h2.tools.RunScript;
import org.h2.util.IOUtils;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.sql.Connection;

/**
 * h2 函数初始化器
 * 参见： https://github.com/kubeclub/fastjunit
 *
 * @author wuxp
 * @date 2023-11-20 16:08
 **/
@Slf4j
public final class H2FunctionInitializer {

    private static final String BLANK_SPACE = " ";

    private H2FunctionInitializer() {
        throw new AssertionError();
    }

    /**
     * 初始化h2函数
     *
     * @param dataSource 数据源
     */
    public static void initialize(DataSource dataSource) {
        try {
            StringBuilder sBuilder = new StringBuilder();
            for (H2Function h2Function : H2MysqlFunctions.getFunctions()) {
                sBuilder.append("CREATE")
                        .append(BLANK_SPACE)
                        .append("ALIAS")
                        .append(BLANK_SPACE)
                        .append(h2Function.getName()).append(BLANK_SPACE).append("FOR")
                        .append(BLANK_SPACE)
                        .append("\"").append(h2Function.getFullClassName()).append("\";");
            }
            Connection conn = dataSource.getConnection();
            RunScript.execute(conn, IOUtils.getReader(new ByteArrayInputStream(sBuilder.toString().getBytes())));
            conn.close();
        } catch (Exception exception) {
            log.error("init h2 functions error", exception);
        }
    }
}
