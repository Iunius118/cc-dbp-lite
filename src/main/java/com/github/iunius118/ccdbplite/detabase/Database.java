package com.github.iunius118.ccdbplite.detabase;

import dan200.computercraft.api.lua.LuaException;
import org.sqlite.JDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Database {
    static {
        try {
            // Load SQLite JDBC Driver
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void executeSQL(String databaseURL, String sql) throws LuaException {
        // TODO: Execute SQL and get result

        // Sample code from SQLite JDBC Driver
        try(
                // create a database connection
                Connection connection = DriverManager.getConnection(JDBC.PREFIX + databaseURL);
                Statement statement = connection.createStatement();
        ){
            statement.setQueryTimeout(5);

            statement.execute("drop table if exists person");
            statement.execute("create table person (id integer, name string)");
            statement.execute("insert into person values(1, 'leo')");
            statement.execute("insert into person values(2, 'yui')");
            statement.execute("select * from person");
            ResultSet rs = statement.getResultSet();

            while(rs.next()) {
                // read the result set
                System.out.println("name = " + rs.getString("name"));
                System.out.println("id = " + rs.getInt("id"));
            }
        } catch(Exception e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            e.printStackTrace(System.out);
        }
    }
}
