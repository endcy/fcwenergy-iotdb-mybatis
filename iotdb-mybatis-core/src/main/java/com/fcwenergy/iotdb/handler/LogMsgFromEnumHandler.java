package com.fcwenergy.iotdb.handler;

import com.fcwenergy.common.enums.LogMsgFromEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LogMsgFromEnumHandler extends BaseTypeHandler<LogMsgFromEnum> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, LogMsgFromEnum logMsgFromEnum, JdbcType jdbcType) throws SQLException {
        preparedStatement.setInt(i, logMsgFromEnum.getCode());
    }

    @Override
    public LogMsgFromEnum getNullableResult(ResultSet resultSet, String colName) throws SQLException {
        int code = resultSet.getInt(colName);
        return code <= 0 ? null : LogMsgFromEnum.create(code);
    }

    @Override
    public LogMsgFromEnum getNullableResult(ResultSet resultSet, int i) throws SQLException {
        int code = resultSet.getInt(i);
        return code <= 0 ? null : LogMsgFromEnum.create(code);
    }

    @Override
    public LogMsgFromEnum getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        int code = callableStatement.getInt(i);
        return code <= 0 ? null : LogMsgFromEnum.create(code);
    }
}
