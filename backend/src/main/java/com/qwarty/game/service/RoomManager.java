package com.qwarty.game.service;

import com.qwarty.exception.code.AppExceptionCode;
import com.qwarty.exception.type.AppException;
import com.qwarty.game.lov.GameMode;
import com.qwarty.game.model.Room;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class RoomManager {

    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public Room getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public Room createRoom(GameMode mode, String sessionUid) {
        if (sessionUid == null) {
            throw new AppException(AppExceptionCode.SESSION_UID_NOT_FOUND);
        }

        String roomId = generateUniqueRoomId();
        Room room = new Room(roomId, mode);
        room.addPlayer(sessionUid);
        rooms.put(roomId, room);
        return room;
    }

    public Room joinRoom(String roomId, String sessionUid) {
        if (sessionUid == null) {
            throw new AppException(AppExceptionCode.SESSION_UID_NOT_FOUND);
        }

        Room room = rooms.get(roomId);
        if (room == null) {
            throw new AppException(AppExceptionCode.ROOM_NOT_FOUND);
        }
        if (room.hasPlayer(sessionUid)) {
            return room;
        }
        if (room.isFull()) {
            throw new AppException(AppExceptionCode.ROOM_FULL);
        }

        room.addPlayer(sessionUid);
        return room;
    }

    private String generateUniqueRoomId() {
        String roomId;

        do {
            roomId = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        } while (rooms.containsKey(roomId));

        return roomId;
    }
}
