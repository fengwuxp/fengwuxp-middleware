package com.wind.mybatis.encrypt;


import org.apache.ibatis.type.JdbcType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 加解密字符串类型字段
 *
 * @author wuxp
 * @date 2024-07-15 17:47
 **/
public final class StringEncryptTypeHandler extends AbstractEncryptBaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, requireTextEncryptor().encrypt(parameter));
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return requireTextEncryptor().decrypt(rs.getString(columnName));
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return requireTextEncryptor().decrypt(rs.getString(columnIndex));
    }

    @Override
    public String getNullableResult(java.sql.CallableStatement cs, int columnIndex) throws SQLException {
        return requireTextEncryptor().decrypt(cs.getString(columnIndex));
    }

}