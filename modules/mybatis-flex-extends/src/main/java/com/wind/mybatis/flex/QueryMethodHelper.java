package com.wind.mybatis.flex;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.RawQueryCondition;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

/**
 * MySql 数据库函数支持
 *
 * @author wuxp
 * @date 2023-11-20 12:54
 **/
public final class QueryMethodHelper {

    private QueryMethodHelper() {
        throw new AssertionError();
    }

    /**
     * 参见 https://www.yiibai.com/mysql/find_in_set.html
     * 在 {@param column} 字段中
     *
     * @param column 查询的字段
     * @param text   查找的文本
     * @return QueryCondition
     */
    public static QueryCondition findInSet(QueryColumn column, @Nullable String text) {
        return new RawQueryCondition(String.format("FIND_IN_SET (?, %s)", buildField(column)), text);
    }

    /**
     * 不在 {@param column} 字段中
     *
     * @param column 查询的字段
     * @param text   查找的文本
     * @return QueryCondition
     */
    public static QueryCondition notFindInSet(QueryColumn column, @Nullable String text) {
        return new RawQueryCondition(String.format("NOT FIND_IN_SET (?, %s)", buildField(column)), text);
    }

    private static String buildField(QueryColumn column) {
        return String.format("`%s`.`%s`", column.getTable().getName(), column.getName());
    }

    public static QueryCondition findInSet(QueryColumn column, @Nullable Enum<?> content) {
        if (content == null) {
            return QueryCondition.createEmpty();
        }
        return findInSet(column, content.name());
    }

    public static QueryCondition notFindInSet(QueryColumn column, @Nullable Enum<?> content) {
        if (content == null) {
            return QueryCondition.createEmpty();
        }
        return notFindInSet(column, content.name());
    }

    /**
     * FIND_IN_SET 字段值在集合中
     *
     * @param column 查询的字段
     * @param texts  查找的文本
     *               使用示例:
     *               <p>
     *               <code>
     *               column:
     *                  t_user.identity
     *               texts:
     *                 Set<String> identitys = new HashSet<>();
     *                 identitys.add("OPENAPI");
     *                 identitys.add("MEMBER");
     *               生成的SQL:
     *                  (FIND_IN_SET('OPENAPI', identity) > 0 OR FIND_IN_SET('MEMBER', identity) > 0)
     *               </code>
     *               <p/>
     * @return String sql
     */
    public static String findInSet(QueryColumn column, @NotEmpty Set<String> texts) {
        StringBuilder conditions = new StringBuilder();
        for (String name : texts) {
            if (conditions.length() > 0) {
                conditions.append(" OR ");
            }
            conditions.append(String.format("FIND_IN_SET ('%s', %s) > 0", name, buildField(column)));
        }
        return String.format("( %s )", conditions);
    }
}
