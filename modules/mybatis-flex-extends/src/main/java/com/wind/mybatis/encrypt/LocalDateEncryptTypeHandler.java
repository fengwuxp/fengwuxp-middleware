package com.wind.mybatis.encrypt;

import com.wind.common.WindDateFormater;
import org.apache.ibatis.type.JdbcType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * 将 java {@link LocalDate} 类型的数据保存到数据库 varchar 字段中，并加密
 *
 * @author wuxp
 * @date 2024-07-15 17:47
 **/
public final class LocalDateEncryptTypeHandler extends AbstractEncryptBaseTypeHandler<LocalDate> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, LocalDate parameter, JdbcType jdbcType) throws SQLException {
        String format = parameter.format(WindDateFormater.YYYY_MM_DD.getFormatter());
        ps.setString(i, requireTextEncryptor().encrypt(format));
    }

    @Override
    public LocalDate getNullableResult(ResultSet rs, String columnName) throws SQLException {

        return parseLocalDate(rs.getString(columnName));
    }

    @Override
    public LocalDate getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseLocalDate(rs.getString(columnIndex));
    }

    @Override
    public LocalDate getNullableResult(java.sql.CallableStatement cs, int columnIndex) throws SQLException {
        return parseLocalDate(cs.getString(columnIndex));
    }

    private LocalDate parseLocalDate(String text) throws SQLException {
        String date = requireTextEncryptor().decrypt(text);
        return date != null ? LocalDate.parse(date, WindDateFormater.YYYY_MM_DD.getFormatter()) : null;
    }


}