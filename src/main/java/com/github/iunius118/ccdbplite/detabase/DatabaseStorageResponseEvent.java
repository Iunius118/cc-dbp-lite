package com.github.iunius118.ccdbplite.detabase;

import dan200.computercraft.api.peripheral.IComputerAccess;

import java.util.stream.Stream;

public final class DatabaseStorageResponseEvent {
    private static final String EVENT = "dbstorage_response";

    private static int id = 0;

    private DatabaseStorageResponseEvent() {}

    public static int getNewEventID() {
        return id++;
    }

    public static void succeed(IComputerAccess computer, int eventID, Object... result) {
        Object[] objects = Stream.concat(Stream.of(eventID, true), Stream.of(result)).toArray();
        computer.queueEvent(EVENT, objects);
    }

    public static void fail(IComputerAccess computer, int eventID, String message) {
        computer.queueEvent(EVENT, eventID, false, message);
    }
}
