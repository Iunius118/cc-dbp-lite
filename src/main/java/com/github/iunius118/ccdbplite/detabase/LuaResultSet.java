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

    /**
     * Moves the cursor forward one row from its current position.
     * A {@code ResultSet} cursor is initially positioned before the first row;
     * the first call to the method next makes the first row the current row;
     * the second call makes the second row the current row, and so on.
     * @return {@code boolean} {@code true} if the new current row is valid;
     *                         {@code false} if there are no more rows.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final boolean next() throws LuaException {
        try {
            return resultSet.next();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Releases the result object and JDBC resources immediately.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final void close() throws LuaException {
        try {
            resultSet.close();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Reports whether the last column read had a value of SQL {@code NULL}.
     * @return {@code boolean} {@code true} if the last column value read was SQL {@code NULL} and {@code false} otherwise.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final boolean wasNull() throws LuaException {
        try {
            return resultSet.wasNull();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Maps the given result column label to its column index.
     * @param columnLabel {@code string} The label for the column.
     * @return {@code number} The column index of the given column name.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final int findColumn(String columnLabel) throws LuaException {
        try {
            return resultSet.findColumn(columnLabel);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Retrieves the value of the designated column in the current row of this result as a {@code boolean}.
     * @param column {@code number | string} The column index or column label. The column index is one-based.
     * @return {@code boolean} the column value; if the value is SQL {@code NULL}, the value returned is {@code false}.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final boolean getBoolean(Object column) throws LuaException {
        try {
            return resultSet.getBoolean(checkColumnDescriptor(column));
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Retrieves the value of the designated column in the current row of this result as a {@code number}.
     *
     * <p>Note that converting SQLite 8-byte {@code INTEGER} or SQL {@code BIGINT} to Lua 5.2 {@code number} may result in a loss of accuracy.
     * @param column {@code number | string} The column index or column label. The column index is one-based.
     * @return {@code number} the column value; if the value is SQL {@code NULL}, the value returned is {@code 0}.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final double getNumber(Object column) throws LuaException {
        try {
            // Return double for lua number
            return resultSet.getDouble(checkColumnDescriptor(column));
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Retrieves the value of the designated column in the current row of this result as a {@code string}.
     * @param column {@code number | string} The column index or column label. The column index is one-based.
     * @return {@code string} the column value; if the value is SQL {@code NULL}, the value returned is {@code nil}.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final String getString(Object column) throws LuaException {
        try {
            return resultSet.getString(checkColumnDescriptor(column));
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Retrieves whether the cursor is after the last row in this result.
     * @return {@code boolean} {@code true} if the cursor is after the last row;
     *                         {@code false} if the cursor is at any other position or the result set contains no rows.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final boolean isAfterLast() throws LuaException {
        try {
            return resultSet.isAfterLast();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Retrieves whether the cursor is before the first row in this result.
     * @return {@code boolean} {@code true} if the cursor is before the first row;
     *                         {@code false} if the cursor is at any other position or the result set contains no rows.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final boolean isBeforeFirst() throws LuaException {
        try {
            return resultSet.isBeforeFirst();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Retrieves whether the cursor is on the first row of this result.
     * @return {@code boolean} {@code true} if the cursor is on the first row; {@code false} otherwise.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final boolean isFirst() throws LuaException {
        try {
            return resultSet.isFirst();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Retrieves the current row number. The row number is one-based.
     * @return {@code number} The current row number; {@code 0} if there is no current row.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final int getRow() throws LuaException {
        try {
            return resultSet.getRow();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    private int checkColumnDescriptor(Object value) throws LuaException {
        // Get the column index from a number or string argument
        try {
            if (value instanceof Number numCol) return numCol.intValue();
            if (value instanceof String strCol) return resultSet.findColumn(strCol);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }

        throw LuaValues.badArgument(0, "number or string", LuaValues.getType(value));
    }

    /// ResultSetMetaData Functions ////////////////////////////////////////////

    /**
     * Retrieves the designated column's table's catalog name.
     * @param columnIndex {@code number} The column index one-based.
     * @return {@code string} The catalog name for the table in which the given column appears or "" if not applicable.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final String getCatalogName(int columnIndex) throws LuaException {
        try {
            String catalogName = metaData.getCatalogName(columnIndex);
            return catalogName != null ? catalogName : "";
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Retrieves the designated column's table name.
     * @param columnIndex {@code number} The column index one-based.
     * @return {@code string} The table name in which the given column appears or "" if not applicable.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final String getTableName(int columnIndex) throws LuaException {
        try {
            return metaData.getTableName(columnIndex);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Retrieves the number of columns in this result.
     * @return {@code number} The number of columns.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final int getColumnCount() throws LuaException {
        try {
            return metaData.getColumnCount();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Retrieves the designated column's suggested title for use in printouts and displays.
     * @param columnIndex {@code number} The column index one-based.
     * @return {@code string} The suggested column title.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final String getColumnLabel(int columnIndex) throws LuaException {
        try {
            return metaData.getColumnLabel(columnIndex);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Retrieves the designated column's name.
     * @param columnIndex {@code number} The column index one-based.
     * @return {@code string} The column name.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final String getColumnName(int columnIndex) throws LuaException {
        try {
            return metaData.getColumnName(columnIndex);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Retrieves the designated column's specified column size.
     * @param columnIndex {@code number} The column index one-based.
     * @return {@code number} The precision; {@code 0} is returned for data types where the column size is not applicable.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final int getPrecision(int columnIndex) throws LuaException {
        try {
            return metaData.getPrecision(columnIndex);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Retrieves the designated column's database-specific type name.
     * @param columnIndex {@code number} The column index one-based.
     * @return {@code string} The type name.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final String getColumnTypeName(int columnIndex) throws LuaException {
        try {
            return metaData.getColumnTypeName(columnIndex);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Retrieves whether the designated column allows {@code NULL} values.
     * @param columnIndex {@code number} The column index one-based.
     * @return {@code number} {@code 0} if the column does not allow {@code NULL} values;
     *                        {@code 1} if the column allows {@code NULL} values;
     *                        {@code 2} if the nullability of the column's values is unknown.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final int isNullable(int columnIndex) throws LuaException {
        try {
            return metaData.isNullable(columnIndex);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Retrieves whether the designated column is automatically numbered.
     * @param columnIndex {@code number} The column index one-based.
     * @return {@code boolean} {@code true} if so; {@code false} otherwise.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final boolean isAutoIncrement(int columnIndex) throws LuaException {
        try {
            return metaData.isAutoIncrement(columnIndex);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Retrieves whether values in the designated column are signed numbers.
     * @param columnIndex {@code number} The column index one-based.
     * @return {@code boolean} {@code true} if so; {@code false} otherwise.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final boolean isSigned(int columnIndex) throws LuaException {
        try {
            return metaData.isSigned(columnIndex);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }
}
