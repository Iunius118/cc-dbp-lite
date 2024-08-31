package com.github.iunius118.ccdbplite.detabase;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletableFuture;

public class LuaSQLStatement extends LuaSQLStatementBase {
    public LuaSQLStatement(Connection connection, Statement statement, IComputerAccess computer) {
        super(connection, statement, computer);
    }

    /**
     * Executes the given SQL statement.
     *
     * @param sql {@code string} An SQL statement.
     * @return {@code boolean} {@code true} if the first result is a {@code ResultSet} table;
     *                         {@code false} if it is an update count or there are no results.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final boolean execute(String sql) throws LuaException {
        if (sql.isEmpty()) {
            throw new LuaException("SQL error: Empty SQL statement");
        }

        try {
            return statement.execute(sql);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /**
     * Adds the given SQL command to the current list of commands for this statement.
     * The commands in this list can be executed as a batch by calling the function {@code executeBatch}.
     *
     * @param sql {@code string} Typically this is a SQL {@code INSERT} or {@code UPDATE} statement.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final void addBatch(String sql) throws LuaException {
        if (sql.isEmpty()) {
            throw new LuaException("SQL error: Empty SQL statement");
        }

        try {
            statement.addBatch(sql);
        } catch (SQLException e) {
            throw new LuaException(e.getMessage());
        }
    }

    /// Asynchronous Functions /////////////////////////////////////////////////

    /**
     * Asynchronously calls {@code execute(sql)}.
     *
     * <p>This returns immediately.
     * When the execution has completed, a {@code dbstorage_response} event will be queued.
     * The second return value of the event matches the return value of this function.
     * When the execution has completed normally,
     * the third return value of the event is {@code true} and the fourth return value of the event is the return value of {@code execute(sql)}.
     * When the execution has completed exceptionally,
     * the third return value of the event is {@code false} and the fourth return value of the event is the reason.
     *
     * @return {@code number} The ID of the execution.
     *                        When the execution has completed, it will queue a {@code dbstorage_response} event with a matching id.
     */
    @LuaFunction
    public final int executeAsync(String sql) {
        final int eventID = DatabaseStorageResponseEvent.getNewEventID();

        CompletableFuture.runAsync(() -> {
            try {
                boolean result = execute(sql);
                DatabaseStorageResponseEvent.succeed(computer, eventID, result);
            } catch (LuaException e) {
                DatabaseStorageResponseEvent.fail(computer, eventID, e.getMessage());
            }
        });

        return eventID;
    }
}
