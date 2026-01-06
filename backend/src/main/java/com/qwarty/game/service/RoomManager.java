package com.qwarty.game.service;

import com.qwarty.exception.code.AppExceptionCode;
import com.qwarty.exception.type.AppException;
import com.qwarty.game.dto.RoomDetailsDTO;
import com.qwarty.game.lov.GameMode;
import com.qwarty.game.model.Room;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomManager {

    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public Room getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    public void removeRoom(String roomId) {
        rooms.remove(roomId);
    }

    public RoomDetailsDTO createRoom(GameMode mode, String sessionUid) {
        if (sessionUid == null) {
            throw new AppException(AppExceptionCode.SESSION_UID_NOT_FOUND);
        }

        String roomId = generateUniqueRoomId();
        Room room = new Room(roomId, mode);

        room.initPlayerConnection(sessionUid);

        rooms.put(roomId, room);
        return retrieveRoomDetails(roomId);
    }

    public RoomDetailsDTO joinRoom(String roomId, String sessionUid) {
        if (sessionUid == null) {
            throw new AppException(AppExceptionCode.SESSION_UID_NOT_FOUND);
        }

        Room room = getRoom(roomId);
        if (room == null) {
            throw new AppException(AppExceptionCode.ROOM_NOT_FOUND);
        }
        if (room.hasPlayer(sessionUid)) {
            return retrieveRoomDetails(roomId);
        }
        if (room.isFull()) {
            throw new AppException(AppExceptionCode.ROOM_FULL);
        }

        room.initPlayerConnection(sessionUid);
        return retrieveRoomDetails(roomId);
    }

    private RoomDetailsDTO retrieveRoomDetails(String roomId) {
        Room room = getRoom(roomId);
        if (room == null) {
            throw new AppException(AppExceptionCode.ROOM_NOT_FOUND);
        }

        GameMode gameMode = room.getGameMode();
        List<String> playerList = room.getPlayers();

        return new RoomDetailsDTO(roomId, playerList, gameMode);
    }

    private String generateUniqueRoomId() {
        String roomId;

        do {
            roomId = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        } while (rooms.containsKey(roomId));

        return roomId;
    }
}
