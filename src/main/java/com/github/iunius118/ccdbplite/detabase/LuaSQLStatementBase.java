package com.github.iunius118.ccdbplite.detabase;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LuaSQLStatementBase {
    protected final IComputerAccess computer;
    protected final Connection connection;
    protected final Statement statement;

    private boolean isClosed = false;

    public LuaSQLStatementBase(IComputerAccess computer, Connection connection, Statement statement) {
        this.computer = computer;
        this.connection = connection;
        this.statement = statement;
    }

    /**
     * Retrieves the current result as a {@code ResultSet} table containing functions for manipulating the result.
     * The result is read-only, and its cursor may move only forward.
     *
     * @return {@code table} The table containing the result and functions that wraps {@code ResultSet} and {@code ResultSetMetaData} of JDBC.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final LuaResultSet getResultSet() throws LuaException {
        try {
            var resultSet = statement.getResultSet();

            if (resultSet != null) {
                return new LuaResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }

        throw new LuaException("The result is update count or there are no more results");
    }

    /**
     * Retrieves the current result as an update count.
     *
     * @return {@code number} The current result as an update count;
     *                        {@code -1} if the current result is a {@code ResultSet} table or there are no more results.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final int getUpdateCount() throws LuaException {
        try {
            return statement.getUpdateCount();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Moves to the next result.
     * Implicitly closes any current result obtained with the function getResultSet.
     *
     * <p>There are no more results when the following is true:
     *
     * <pre><code>
     *     -- stmt is a Statement table
     *     ((stmt.getMoreResults() == false) and (stmt.getUpdateCount() == -1))
     * </code></pre>
     *
     * @return {@code boolean} {@code true} if the next result is a {@code ResultSet} table;
     *                         {@code false} if it is an update count or there are no more results.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final boolean getMoreResults() throws LuaException {
        try {
            return statement.getMoreResults();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Empties this statement's current list of SQL commands.
     *
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final void clearBatch() throws LuaException {
        try {
            statement.clearBatch();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Submits a batch of commands to the database for execution and if all commands execute successfully, returns an array of update counts.
     *
     * @return {@code table} An array of update counts containing one element for each command in the batch.
     *                       The elements of the array are ordered according to the order in which commands were added to the batch.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final Map<Integer, Integer> executeBatch() throws LuaException {
        int[] updateCounts;

        try {
            updateCounts = statement.executeBatch();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }

        // Create a Map<Integer one_based_array_index, Integer update_count> and return it
        return IntStream.range(0, updateCounts.length).boxed()
                .collect(Collectors.toMap(i -> i + 1, i -> updateCounts[i]));
    }

    /// Connection Functions ///////////////////////////////////////////////////

    /**
     * Retrieves this connection's current transaction isolation level.
     *
     * @return {@code number} The current transaction isolation level.
     *                        {@code 0} if transactions are not supported;
     *                        {@code 1} if it is READ UNCOMMITTED;
     *                        {@code 2} if it is READ COMMITTED;
     *                        {@code 4} if it is REPEATABLE READ;
     *                        {@code 8} if it is SERIALIZABLE.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final int getTransactionIsolation() throws LuaException {
        try {
            return connection.getTransactionIsolation();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Attempts to change the transaction isolation level for this connection to the one given.
     *
     * @param level {@code number} One of the following numbers:
     *                             {@code 1} for READ UNCOMMITTED,
     *                             {@code 2} for READ COMMITTED,
     *                             {@code 4} for REPEATABLE READ,
     *                             {@code 8} for SERIALIZABLE.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final void setTransactionIsolation(int level) throws LuaException {
        try {
            connection.setTransactionIsolation(level);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Retrieves the current auto-commit mode for this connection.
     *
     * @return {@code boolean} The current state of this connection's auto-commit mode.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final boolean getAutoCommit() throws LuaException {
        try {
            return connection.getAutoCommit();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Sets this connection's auto-commit mode to the given state.
     *
     * @param autoCommit {@code boolean} {@code true} to enable auto-commit mode; {@code false} to disable it.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final void setAutoCommit(boolean autoCommit) throws LuaException {
        try {
            connection.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Makes all changes made since the previous commit/rollback permanent and releases any database locks currently held by this connection.
     * This function should be used only when auto-commit mode has been disabled.
     *
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final void commit() throws LuaException {
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Undoes all changes made in the current transaction and releases any database locks currently held by this connection.
     * This function should be used only when auto-commit mode has been disabled.
     *
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final void rollback() throws LuaException {
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Releases the connection to the database and JDBC resources immediately.
     *
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final void close() throws LuaException {
        closeConnection();
    }

    public void closeConnection() {
        if (!isClosed) {
            try {
                statement.close();
            } catch (SQLException ignored) {}

            try {
                connection.close();
            } catch (SQLException ignored) {}

            isClosed = true;
        }
    }

    public boolean isClosed() {
        return isClosed;
    }

    /// Asynchronous Functions /////////////////////////////////////////////////

    /**
     * Asynchronously calls {@code executeBatch()}.
     *
     * <p>This returns immediately.
     * When the execution has completed, a {@code dbstorage_response} event will be queued.
     * The second return value of the event matches the return value of this function.
     * When the execution has completed normally,
     * the third return value of the event is {@code true} and the fourth return value of the event is the return value of {@code executeBatch()}.
     * When the execution has completed exceptionally,
     * the third return value of the event is {@code false} and the fourth return value of the event is the reason.
     *
     * @return {@code number} The ID of the execution.
     *                        When the execution has completed, it will queue a {@code dbstorage_response} event with a matching id.
     */
    @LuaFunction
    public final int executeBatchAsync() {
        final int eventID = DatabaseStorageResponseEvent.getNewEventID();

        CompletableFuture.runAsync(() -> {
            try {
                Map<Integer, Integer> result = executeBatch();
                DatabaseStorageResponseEvent.succeed(computer, eventID, result);
            } catch (LuaException e) {
                DatabaseStorageResponseEvent.fail(computer, eventID, e.getMessage());
            }
        });

        return eventID;
    }

    /**
     * Asynchronously calls {@code commit()}.
     *
     * <p>This returns immediately.
     * When the commit has completed, a {@code dbstorage_response} event will be queued.
     * The second return value of the event matches the return value of this function.
     * When the commit has completed normally, the third return value of the event is {@code true}.
     * When the commit has completed exceptionally,
     * the third return value of the event is {@code false} and the fourth return value of the event is the reason.
     *
     * @return {@code number} The ID of the commit.
     *                        When the commit has completed, it will queue a {@code dbstorage_response} event with a matching id.
     */
    @LuaFunction
    public final int commitAsync() {
        final int eventID = DatabaseStorageResponseEvent.getNewEventID();

        CompletableFuture.runAsync(() -> {
            try {
                commit();
                DatabaseStorageResponseEvent.succeed(computer, eventID, null);
            } catch (LuaException e) {
                DatabaseStorageResponseEvent.fail(computer, eventID, e.getMessage());
            }
        });

        return eventID;
    }
}
