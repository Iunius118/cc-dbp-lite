package com.github.iunius118.ccdbplite.detabase;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.LuaValues;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class LuaResultSet {
    private final ResultSet resultSet;
    private final ResultSetMetaData metaData;

    public LuaResultSet(ResultSet resultSet) throws SQLException {
        this.resultSet = resultSet;
        this.metaData = resultSet.getMetaData();
    }

    @LuaFunction
    public final boolean next() throws LuaException {
        try {
            return resultSet.next();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    @LuaFunction
    public final void close() throws LuaException {
        try {
            resultSet.close();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    @LuaFunction
    public final boolean wasNull() throws LuaException {
        try {
            return resultSet.wasNull();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    @LuaFunction
    public final int findColumn(String col) throws LuaException {
        try {
            return resultSet.findColumn(col);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    @LuaFunction
    public final boolean getBoolean(Object col) throws LuaException {
        try {
            return resultSet.getBoolean(checkColumnDescriptor(col));
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    @LuaFunction
    public final double getNumber(Object col) throws LuaException {
        try {
            // Return double for lua number
            return resultSet.getDouble(checkColumnDescriptor(col));
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    @LuaFunction
    public final String getString(Object col) throws LuaException {
        try {
            return resultSet.getString(checkColumnDescriptor(col));
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    @LuaFunction
    public final boolean isAfterLast() throws LuaException {
        try {
            return resultSet.isAfterLast();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    @LuaFunction
    public final boolean isBeforeFirst() throws LuaException {
        try {
            return resultSet.isBeforeFirst();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    @LuaFunction
    public final boolean isFirst() throws LuaException {
        try {
            return resultSet.isFirst();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    @LuaFunction
    public final int getRow() throws LuaException {
        try {
            return resultSet.getRow();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    private int checkColumnDescriptor(Object value) throws LuaException {
        try {
            if (value instanceof Number numCol) return numCol.intValue();
            if (value instanceof String strCol) return resultSet.findColumn(strCol);
            throw LuaValues.badArgument(0, "number or string", LuaValues.getType(value));
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /// ResultSetMetaData Functions ////////////////////////////////////////////

    @LuaFunction
    public final String getTableName(int col) throws LuaException {
        try {
            return metaData.getTableName(col);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    @LuaFunction
    public final int getColumnCount() throws LuaException {
        try {
            return metaData.getColumnCount();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    @LuaFunction
    public final String getColumnName(int col) throws LuaException {
        try {
            return metaData.getColumnName(col);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    @LuaFunction
    public final String getColumnTypeName(int col) throws LuaException {
        try {
            return metaData.getColumnTypeName(col);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    @LuaFunction
    public final int isNullable(int col) throws LuaException {
        try {
            return metaData.isNullable(col);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    @LuaFunction
    public final boolean isAutoIncrement(int col) throws LuaException {
        try {
            return metaData.isAutoIncrement(col);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    @LuaFunction
    public final boolean isSigned(int col) throws LuaException {
        try {
            return metaData.isSigned(col);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }
}
