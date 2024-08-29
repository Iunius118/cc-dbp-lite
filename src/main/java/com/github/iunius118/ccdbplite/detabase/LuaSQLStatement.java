package com.github.iunius118.ccdbplite.detabase;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LuaSQLStatement {
    private final IComputerAccess computer;
    private final String databaseURL;
    private List<String> batch = new ArrayList<>();

    public LuaSQLStatement(IComputerAccess computer, String databaseURL) {
        this.computer = computer;
        this.databaseURL = databaseURL;
    }

    /**
     * Executes the given SQL statement.
     *
     * @param sql {@code string} An SQL statement.
     * @return {@code table} {@code isResultSet == true} if the result is a result set;
     *                       {@code updateCount ~= -1} if it is an update count;
     *                       none of the above if there is no result.
     * @throws LuaException Thrown when SQL driver returns a warning or error.
     */
    @LuaFunction
    public final MethodResult execute(String sql) throws LuaException {
        if (sql.isEmpty()) {
            return MethodResult.of(false, -1);
        }

        try (   Connection connection = DriverManager.getConnection(databaseURL);
                Statement statement = connection.createStatement()
        ) {
            // Execute SQL statement
            if (statement.execute(sql)) {
                // Has result set
                return MethodResult.of(LuaSQLResult.resultSetOf(statement));
            } else {
                // Has update count
                return MethodResult.of(LuaSQLResult.updateCountOf(statement));
            }
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
        if (!sql.isEmpty()) {
            batch.add(sql);
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
    public final MethodResult executeBatch() throws LuaException {
        try (   Connection connection = DriverManager.getConnection(databaseURL);
                Statement statement = connection.createStatement()
        ) {
            List<String> sqls = batch;
            batch = new ArrayList<>();

            for (String sql : sqls) {
                statement.addBatch(sql);
            }

            int[] updateCounts = statement.executeBatch();
            List<Integer> updateCountList = Arrays.stream(updateCounts).boxed().toList();
            return MethodResult.of(updateCountList);
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
     * @param sql {@code string} An SQL statement.
     * @return {@code number} The ID of the execution.
     *                        When the execution has completed, it will queue a {@code dbstorage_response} event with a matching id.
     */
    @LuaFunction
    public final int executeAsync(String sql) {
        final int eventID = DatabaseStorageResponseEvent.getNewEventID();

        CompletableFuture.runAsync(() -> {
            try {
                MethodResult result = execute(sql);
                DatabaseStorageResponseEvent.succeed(computer, eventID, result.getResult());
            } catch (LuaException e) {
                DatabaseStorageResponseEvent.fail(computer, eventID, e.getMessage());
            }
        });

        return eventID;
    }

    /**
     * Asynchronously calls {@code executeBatch()}.
     *
     * <p>This returns immediately.
     * When the execution has completed, a {@code dbstorage_response} event will be queued.
     * @return {@code number} The ID of the execution.
     *                        When the execution has completed, it will queue a {@code dbstorage_response} event with a matching id.
     */
    @LuaFunction
    public final int executeBatchAsync() {
        final int eventID = DatabaseStorageResponseEvent.getNewEventID();

        CompletableFuture.runAsync(() -> {
            try {
                MethodResult result = executeBatch();
                DatabaseStorageResponseEvent.succeed(computer, eventID, result.getResult());
            } catch (LuaException e) {
                DatabaseStorageResponseEvent.fail(computer, eventID, e.getMessage());
            }
        });

        return eventID;
    }
}
