# CCDatabasePeripheralLite

Version 0.1.2

A Minecraft mod to add a peripheral for [CC: Tweaked](https://tweaked.cc/) to manipulate databases using [SQLite JDBC Driver](https://github.com/xerial/sqlite-jdbc).

## Requirements

- Minecraft 1.20.1
- Minecraft Forge 47.3.0+
- CC: Tweaked 1.20.1-forge-1.112.0+

## Block

### Database Storage

A database storage is a block of database storage peripheral that can be connected from ComputerCraft's computers.

Crafting (shaped recipe):

    Database Storage
    6 Stones (#), Disk Drive (D), Block of Redstone (R), Iron Ingot (i)
    | # | D | # |
    | # | L | # |
    | # | i | # |

## Peripheral

### Database Storage Peripheral

A database storage peripheral is a peripheral (type: `dbstorage`) accessible from computers connected to a database storage block.
Players can manipulate the database through this peripheral.

Each database storage peripheral is identified by storage ID, and each storage has only one database that can be accessed from computers.
The database file is saved in `(world_dir)/computercraft/dbstorage/(storage_id)/database.db` as a SQLite3 database.

#### Database Storage Functions

- getID()
  - Retrieves an integer to identify this database storage peripheral
  - This identifier will be assigned to this peripheral when the database in this peripheral is first connected
  - Returns - [number] The identifier; -1 If it has not yet been assigned
- createStatement()
  - Connects to the database and returns a Statement table containing functions for manipulating the database
  - Returns - [table] The Statement table containing functions that wraps Connection and Statement of Java Database Connectivity
- prepareStatement(sql)
  - Connects to the database with parameterized SQL statement and returns a Prepared Statement table containing functions for manipulating the database
  - sql - [string] An SQL statement that may contain one or more '?' parameter placeholders
  - Returns - [table] The Prepared Statement table containing functions that wraps Connection, PreparedStatement, and ParameterMetaData of Java Database Connectivity
- closeAll()
  - Releases all connections and resources to the database from this computer immediately

#### Statement Functions

- execute(sql)
  - Executes the given SQL statement
  - sql - [string] An SQL statement
  - Returns - [boolean] true if the first result is a Result Set table; false if it is an update count or there are no results
- executeAsync(String sql)
  - Asynchronously calls execute()
  - This returns immediately. When the execution has completed, a dbstorage_response event will be queued
  - Returns - [number] The ID of the execution. When the execution has completed, it will queue a dbstorage_response event with a matching id
- getResultSet()
  - Retrieves the current result as a Result Set table containing functions for manipulating the result
  - The result is read-only, and its cursor may move only forward
  - Returns - [table] The Result Set table containing the result and functions that wraps ResultSet and ResultSetMetaData of Java Database Connectivity
- getUpdateCount()
  - Retrieves the current result as an update count
  - Returns - [number] The current result as an update count; -1 if the current result is a Result Set table or there are no more results
- getMoreResults()
  - Moves to the next result
  - Implicitly closes any current result obtained with getResultSet()
  - Returns - [boolean] true if the next result is a ResultSet table; false if it is an update count or there are no more results
- addBatch(sql)
  - Adds the given SQL command to the current list of commands for this statement
  - The commands in this list can be executed as a batch by calling executeBatch()
  - sql - [string] Typically this is a SQL INSERT or UPDATE statement
- clearBatch()
  - Empties this statement's current list of SQL commands
- executeBatch()
  - Submits a batch of commands to the database for execution and if all commands execute successfully, returns an array of update counts
  - Returns - [table] An array of update counts containing one element for each command in the batch
- executeBatchAsync()
  - Asynchronously calls executeBatch()
  - This returns immediately. When the execution has completed, a dbstorage_response event will be queued
  - Returns - [number] The ID of the execution. When the execution has completed, it will queue a dbstorage_response event with a matching id
- getTransactionIsolation()
  - Retrieves this connection's current transaction isolation level
  - Return - [number] 0 if transactions are not supported; 1 if it is READ UNCOMMITTED; 2 if it is READ COMMITTED; 4 if it is REPEATABLE READ; 8 if it is SERIALIZABLE
- setTransactionIsolation(level)
  - Attempts to change the transaction isolation level for this connection to the one given
  - level - [number] 1 for READ UNCOMMITTED, 2 for READ COMMITTED, 4 for REPEATABLE READ, 8 for SERIALIZABLE
- getAutoCommit()
  - Retrieves the current auto-commit mode for this connection
  - Returns - [boolean] The current state of this connection's auto-commit mode
- setAutoCommit(autoCommit)
  - Sets this connection's auto-commit mode to the given state.
  - autoCommit - [boolean] true to enable auto-commit mode; false to disable it
- commit()
  - Makes all changes made since the previous commit/rollback permanent and releases any database locks currently held by this connection
  - This function should be used only when auto-commit mode has been disabled
- commitAsync()
  - Asynchronously calls commit()
  - This returns immediately. When the commit has completed, a dbstorage_response event will be queued
  - Returns - [number] The ID of the execution. When the commit has completed, it will queue a dbstorage_response event with a matching id
- rollback()
  - Undoes all changes made in the current transaction and releases any database locks currently held by this connection
  - This function should be used only when auto-commit mode has been disabled
- close()
  - Releases the connection to the database and JDBC resources immediately

#### Prepared Statement Functions

- setBoolean(parameterIndex, x)
  - Sets the designated parameter to the given boolean value
  - parameterIndex - [number] The parameter index one-based
  - x - [boolean] The parameter value
- setInt(parameterIndex, x)
  - Sets the designated parameter to the given number value
  - The driver converts this to an SQL INTEGER value when it sends it to the database
  - parameterIndex - [number] The parameter index one-based
  - x - [number] The parameter value
- setDouble(parameterIndex, x)
  - Sets the designated parameter to the given number value
  - The driver converts this to an SQL DOUBLE value when it sends it to the database
  - parameterIndex - [number] The parameter index one-based
  - x - [number] The parameter value
- setString(parameterIndex, x)
  - Sets the designated parameter to the given string value
  - parameterIndex - [number] The parameter index one-based
  - x - [string] The parameter value
- setNull(parameterIndex)
  - Sets the designated parameter to SQL NULL
  - parameterIndex - [number] The parameter index one-based
- clearParameters()
  - Clears the current parameter values immediately
  - The parameter values are cleared immediately instead of waiting for them to be automatically cleared
- getParameterCount()
  - Retrieves the number of parameters in this prepared statement
  - Returns - [number] The number of parameters
- getParameterTypeName(parameterIndex)
  - Retrieves the designated parameter's database-specific type name
  - parameterIndex - [number] The parameter index one-based
  - Returns - [string] The type name used by the database
- getParameterMode(parameterIndex)
  - Retrieves the designated parameter's mode
  - parameterIndex - [number] The parameter index one-based
  - Returns - [number]  0 if the mode of the parameter is unknown; 1 if it is IN; 2 if it is INOUT; 4 if it is OUT
- isNullable(parameterIndex)
  - Retrieves whether null values are allowed in the designated parameter
  - parameterIndex - [number] The parameter index one-based
  - Returns - [number] 0 if the column will not allow NULL values; 1 if the column will allow NULL values; 2 if the nullability of the column's values is unknown
- execute()
  - Executes the SQL statement in this prepared statement
  - Returns - [boolean] true if the first result is a ResultSet table; false if it is an update count or there are no results
- executeAsync()
  - Asynchronously calls execute()
  - This returns immediately. When the execution has completed, a dbstorage_response event will be queued
  - Returns - [number] The ID of the execution. When the execution has completed, it will queue a dbstorage_response event with a matching id
- getResultSet()
  - Retrieves the current result as a Result Set table containing functions for manipulating the result
  - The result is read-only, and its cursor may move only forward
  - Returns - [table] The Result Set table containing the result and functions that wraps ResultSet and ResultSetMetaData of Java Database Connectivity
- getUpdateCount()
  - Retrieves the current result as an update count
  - Returns - [number] The current result as an update count; -1 if the current result is a Result Set table or there are no more results
- getMoreResults()
  - Moves to the next result
  - Implicitly closes any current result obtained with getResultSet()
  - Returns - [boolean] true if the next result is a ResultSet table; false if it is an update count or there are no more results
- addBatch()
  - Adds a set of parameters to this prepared statement's batch of commands
  - The commands in this list can be executed as a batch by calling executeBatch()
- clearBatch()
  - Empties this statement's current list of SQL commands
- executeBatch()
  - Submits a batch of commands to the database for execution and if all commands execute successfully, returns an array of update counts
  - Returns - [table] An array of update counts containing one element for each command in the batch
- executeBatchAsync()
  - Asynchronously calls executeBatch()
  - This returns immediately. When the execution has completed, a dbstorage_response event will be queued
  - Returns - [number] The ID of the execution. When the execution has completed, it will queue a dbstorage_response event with a matching id
- getTransactionIsolation()
  - Retrieves this connection's current transaction isolation level
  - Return - [number] 0 if transactions are not supported; 1 if it is READ UNCOMMITTED; 2 if it is READ COMMITTED; 4 if it is REPEATABLE READ; 8 if it is SERIALIZABLE
- setTransactionIsolation(level)
  - Attempts to change the transaction isolation level for this connection to the one given
  - level - [number] 1 for READ UNCOMMITTED, 2 for READ COMMITTED, 4 for REPEATABLE READ, 8 for SERIALIZABLE
- getAutoCommit()
  - Retrieves the current auto-commit mode for this connection
  - Returns - [boolean] The current state of this connection's auto-commit mode
- setAutoCommit(autoCommit)
  - Sets this connection's auto-commit mode to the given state.
  - autoCommit - [boolean] true to enable auto-commit mode; false to disable it
- commit()
  - Makes all changes made since the previous commit/rollback permanent and releases any database locks currently held by this connection
  - This function should be used only when auto-commit mode has been disabled
- commitAsync()
  - Asynchronously calls commit()
  - This returns immediately. When the commit has completed, a dbstorage_response event will be queued
  - Returns - [number] The ID of the execution. When the commit has completed, it will queue a dbstorage_response event with a matching id
- rollback()
  - Undoes all changes made in the current transaction and releases any database locks currently held by this connection
  - This function should be used only when auto-commit mode has been disabled
- close()
  - Releases the connection to the database and JDBC resources immediately

#### Result Set Functions

- next()
  - Moves the cursor forward one row from its current position
  - A result set cursor is initially positioned before the first row; the first call to the method next makes the first row the current row; the second call makes the second row the current row, and so on
  - Returns - [boolean] true if the new current row is valid; false if there are no more rows
- findColumn(columnLabel)
  - Maps the given result column label to its column index
  - columnLabel - [string] The label for the column
  - Returns - [number] The column index of the given column name
- getBoolean(column)
  - Retrieves the value of the designated column in the current row of this result as a boolean
  - column - [number | string] The column index or column label. The column index is one-based
  - Returns - [boolean] The column value; if the value is SQL NULL, the value returned is false
- getNumber(column)
  - Retrieves the value of the designated column in the current row of this result as a number
  - Note that converting SQLite 8-byte INTEGER or SQL BIGINT to Lua 5.2 number may result in a loss of accuracy
  - column - [number | string] The column index or column label. The column index is one-based
  - Returns - [number] The column value; if the value is SQL NULL, the value returned is 0
- getString(column)
  - Retrieves the value of the designated column in the current row of this result as a string
  - column - [number | string] The column index or column label. The column index is one-based
  - Returns - [string] The column value; if the value is SQL NULL, the value returned is nil
- wasNull()
  - Reports whether the last column read had a value of SQL NULL
  - Returns - [boolean] true if the last column value read was SQL NULL and false otherwise
- isAfterLast()
  - Retrieves whether the cursor is after the last row in this result
  - Returns - [boolean] true if the cursor is after the last row; false if the cursor is at any other position or the result set contains no rows
- isBeforeFirst()
  - Retrieves whether the cursor is before the first row in this result
  - Returns - [boolean] true if the cursor is before the first row; false if the cursor is at any other position or the result set contains no rows
- isFirst()
  - Retrieves whether the cursor is on the first row of this result
  - Returns - [boolean] true if the cursor is on the first row; false otherwise
- getRow()
  - Retrieves the current row number. The row number is one-based
  - Returns - [number] The current row number; 0 if there is no current row
- getCatalogName(columnIndex)
  - Retrieves the designated column's table's catalog name
  - columnIndex - [number] The column index one-based
  - Returns - [string] The catalog name for the table in which the given column appears or "" if not applicable
- getTableName(columnIndex)
  - Retrieves the designated column's table name
  - columnIndex - [number] The column index one-based
  - Returns - [string] The table name in which the given column appears or "" if not applicable
- getColumnCount()
  - Retrieves the number of columns in this result
  - Returns - [number] The number of columns
- getColumnLabel(columnIndex)
  - Retrieves the designated column's suggested title for use in printouts and display
  - columnIndex - [number] The column index one-based
  - Returns - [string] The suggested column title
- getColumnName(columnIndex)
  - Retrieves the designated column's name
  - columnIndex - [number] The column index one-based
  - Returns - [string] The column name
- getPrecision(columnIndex)
  - Retrieves the designated column's specified column size
  - columnIndex - [number] The column index one-based
  - Returns - [number] The precision; 0 is returned for data types where the column size is not applicable
- getColumnTypeName(columnIndex)
  - Retrieves the designated column's database-specific type name
  - columnIndex - [number] The column index one-based
  - Returns - [string] The type name
- isNullable(columnIndex)
  - Retrieves whether the designated column allows NULL values
  - columnIndex - [number] The column index one-based
  - Returns - [number] 0 if the column does not allow NULL values; 1 if the column allows NULL values; 2 if the nullability of the column's values is unknown
- isAutoIncrement(columnIndex)
  - Retrieves whether the designated column is automatically numbered
  - columnIndex - [number] The column index one-based
  - Returns - [boolean] true if so; false otherwise
- isSigned(columnIndex)
  - Retrieves whether values in the designated column are signed numbers
  - columnIndex - [number] The column index one-based
  - Returns - [boolean] true if so; false otherwise
- close()
  - Releases the result and JDBC resources immediately

#### Event Queued by Database Storage

- dbstorage_response
  - This is an event that will be queued when a request by an asynchronous function is completed
  - Returns - eventName, id, isCompletedNormally, result
    1. eventName - [string] The event name "dbstorage_response"
    2. id - [number] The ID of the request that completed
    3. isCompletedNormally - [boolean] true if the request has completed normally; false if it has completed exceptionally
    4. result - [any | string] If isCompletedNormally is true, the return value of the request. Otherwise, the reason for the exception
