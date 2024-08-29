package com.github.iunius118.ccdbplite.detabase;

import dan200.computercraft.api.peripheral.IComputerAccess;

public final class LuaPreparedSQLStatement {
    private final IComputerAccess computer;
    private final String databaseURL;
    private final String sql;

    public LuaPreparedSQLStatement(IComputerAccess computer, String databaseURL, String sql) {
        this.computer = computer;
        this.databaseURL = databaseURL;
        this.sql = sql;
    }

}
