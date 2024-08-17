package com.github.iunius118.ccdbplite.detabase;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;

import java.sql.*;

public class LuaPreparedSQLStatement extends LuaSQLStatementBase {
    private final PreparedStatement preparedStatement;
    private final ParameterMetaData parameterMetaData;

    public LuaPreparedSQLStatement(Connection connection, PreparedStatement preparedStatement) throws SQLException {
        super(connection, preparedStatement);
        this.preparedStatement = preparedStatement;
        this.parameterMetaData = preparedStatement.getParameterMetaData();
    }

    /**
     * Sets the designated parameter to the given Lua {@code boolean} value.
     * @param parameterIndex {@code number} The parameter index one-based.
     * @param x {@code boolean} The parameter value.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final void setBoolean(int parameterIndex, boolean x) throws LuaException {
        try {
            preparedStatement.setBoolean(parameterIndex, x);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Sets the designated parameter to the given Lua {@code number} value.
     * The driver converts this to an SQL {@code INTEGER} value when it sends it to the database.
     * @param parameterIndex {@code number} The parameter index one-based.
     * @param x {@code number} The parameter value.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final void setInt(int parameterIndex, int x) throws LuaException {
        try {
            preparedStatement.setInt(parameterIndex, x);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Sets the designated parameter to the given Lua {@code number} value.
     * The driver converts this to an SQL {@code DOUBLE} value when it sends it to the database.
     * @param parameterIndex {@code number} The parameter index one-based.
     * @param x {@code number} The parameter value.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final void setDouble(int parameterIndex, double x) throws LuaException {
        try {
            preparedStatement.setDouble(parameterIndex, x);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Sets the designated parameter to the given Lua {@code string} value.
     * @param parameterIndex {@code number} The parameter index one-based.
     * @param x {@code string} The parameter value.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final void setString(int parameterIndex, String x) throws LuaException {
        try {
            preparedStatement.setString(parameterIndex, x);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Sets the designated parameter to SQL {@code NULL}.
     * @param parameterIndex {@code number} The parameter index one-based.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final void setNull(int parameterIndex) throws LuaException {
        try {
            preparedStatement.setNull(parameterIndex, Types.NULL);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Clears the current parameter values immediately.
     * The parameter values are cleared immediately without waiting for automatic clearing.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final void clearParameters() throws LuaException {
        try {
            preparedStatement.clearParameters();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Executes the SQL statement in this prepared statement.
     * @return {@code boolean} {@code true} if the first result is a {@code ResultSet} table;
     *                         {@code false} if it is an update count or there are no results.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final boolean execute() throws LuaException {
        try {
            return preparedStatement.execute();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Adds a set of parameters to this prepared statement's batch of commands.
     * The commands in this list can be executed as a batch by calling the function {@code executeBatch}.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final void addBatch() throws LuaException {
        try {
            preparedStatement.addBatch();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /// ParameterMetaData Functions ////////////////////////////////////////////

    /**
     * Retrieves the number of parameters in this prepared statement.
     * @return {@code number} The number of parameters.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final int getParameterCount() throws LuaException {
        try {
            return parameterMetaData.getParameterCount();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Retrieves the designated parameter's database-specific type name.
     * @param parameterIndex {@code number} The parameter index one-based.
     * @return {@code string} The type name used by the database.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final String getParameterTypeName(int parameterIndex) throws LuaException {
        try {
            return parameterMetaData.getParameterTypeName(parameterIndex);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Retrieves the designated parameter's mode.
     * @param parameterIndex {@code number} The parameter index one-based.
     * @return {@code number} The mode of the parameter.
     *                        {@code 0} if the mode of the parameter is unknown;
     *                        {@code 1} if the parameter's mode is IN;
     *                        {@code 2} if the parameter's mode is INOUT;
     *                        {@code 4} if the parameter's mode is OUT.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final int getParameterMode(int parameterIndex) throws LuaException {
        try {
            return parameterMetaData.getParameterMode(parameterIndex);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Retrieves whether null values are allowed in the designated parameter.
     * @param parameterIndex {@code number} The parameter index one-based.
     * @return {@code number} {@code 0} if the column will not allow {@code NULL} values;
     *                        {@code 1} if the column will allow {@code NULL} values;
     *                        {@code 2} if the nullability of the column's values is unknown.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final int isNullable(int parameterIndex) throws LuaException {
        try {
            return parameterMetaData.isNullable(parameterIndex);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }
}
