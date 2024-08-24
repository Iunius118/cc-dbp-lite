package com.github.iunius118.ccdbplite.detabase;

import dan200.computercraft.api.peripheral.IComputerAccess;

public final class DatabaseStorageResponseEvent {
    private static final String EVENT = "dbstorage_response";

    private static int id = 0;

    private DatabaseStorageResponseEvent() {}

    public static int getNewEventID() {
        return id++;
    }

    public static void succeed(IComputerAccess computer, int eventID, Object result) {
        computer.queueEvent(EVENT, eventID, true, result);
    }

    public static void fail(IComputerAccess computer, int eventID, String message) {
        computer.queueEvent(EVENT, eventID, false, message);
    }
}
