package com.github.iunius118.ccdbplite.detabase;

import dan200.computercraft.api.lua.ILuaFunction;
import dan200.computercraft.api.lua.MethodResult;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LuaSQLResult {
    private LuaSQLResult() {}

    public static Map<String, Object> resultSetOf(Statement statement) throws SQLException {
        final var resultSet = statement.getResultSet();
        final var metaData = resultSet.getMetaData();
        final int columnCount = metaData.getColumnCount();

        Map<String, Object> columns = new HashMap<>();
        columns.put("columnCount", columnCount);
        columns.put("isAutoIncrement", getColumnMetaData(metaData::isAutoIncrement, columnCount));
        columns.put("isNullable", getColumnMetaData(i -> metaData.isNullable(i) != ResultSetMetaData.columnNoNulls, columnCount));
        columns.put("columnNames", getColumnMetaData(metaData::getColumnName, columnCount));
        columns.put("columnTypes", getColumnMetaData(metaData::getColumnTypeName, columnCount));
        columns.put("tableNames", getColumnMetaData(metaData::getTableName, columnCount));

        Map<String, Object> result = new HashMap<>();
        result.put("isResultSet", true);
        result.put("updateCount", -1);
        result.put("getMetaData", createLuaFunction(columns));
        result.put("getRows", createLuaFunction(getRows(resultSet, columnCount)));
        return result;
    }

    public static Map<String, Object> updateCountOf(Statement statement) throws SQLException {
        Map<String, Object> result = new HashMap<>();
        result.put("isResultSet", false);
        result.put("updateCount", statement.getUpdateCount());
        return result;
    }

    private static List<Object> getColumnMetaData(ThrowableSQLFunction<Integer, Object> dataGetter, int columnCount) throws SQLException {
        List<Object> data = new ArrayList<>();

        for (int i = 1; i <= columnCount; i++) {
            data.add(dataGetter.apply(i));
        }

        return data;
    }

    private static List<Object> getRows(ResultSet resultSet, int columnCount) throws SQLException {
        List<Object> rows = new ArrayList<>();

        while(resultSet.next()) {
            List<Object> rowData = new ArrayList<>();

            for (int i = 1; i <= columnCount; i++) {
                rowData.add(resultSet.getObject(i));
            }

            rows.add(rowData);
        }

        return rows;
    }

    private static ILuaFunction createLuaFunction(Object object) {
        return arguments -> MethodResult.of(object);
    }

    @FunctionalInterface
    private interface ThrowableSQLFunction<T, R> {
        R apply(T t) throws SQLException;
    }
}
