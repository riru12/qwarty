package com.qwarty.game.service;

import com.qwarty.exception.code.AppExceptionCode;
import com.qwarty.exception.type.AppException;
import com.qwarty.game.dto.RoomDetailsDTO;
import com.qwarty.game.lov.GameMode;
import com.qwarty.game.model.Room;
import com.qwarty.game.session.GameSession;
import com.qwarty.game.session.GameSessionFactory;

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
    private final GameSessionFactory gameSessionFactory;

    /**
     * Creates a room and stores it in memory by hash map, returns its details in a DTO
     */
    public RoomDetailsDTO createRoom(GameMode mode) {
        String roomId = generateUniqueRoomId();
        Room room = new Room(roomId, mode);

        GameSession gameSession = gameSessionFactory.createSession(mode, room);
        room.setGameSession(gameSession);
        
        rooms.put(roomId, room);
        return retrieveRoomDetails(roomId);
    }

    /**
     * Joins a room by creating a connection to a room via hashmap, returns updated room details in a DTO
     */
    public RoomDetailsDTO joinRoom(String roomId, String username) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new AppException(AppExceptionCode.ROOM_NOT_FOUND);
        }
        if (room.isFull()) {
            throw new AppException(AppExceptionCode.ROOM_FULL);
        }
        if (!room.addConnection(username)) {
            throw new AppException(AppExceptionCode.ROOM_FULL);
        };
        System.out.println("ROOM JOIN:" + username);
        return retrieveRoomDetails(roomId);
    }

    /**
     * Leaves a room by removing a connection to the room, returns updated room details in a DTO
     */
    public RoomDetailsDTO leaveRoom(String roomId, String username) {
        Room room = rooms.get(roomId);
        if (room == null) {
            return null;
        }
        if (room.hasPlayer(username)) {
            room.removeConnection(username);
        }
        if (room.isEmpty()) {
            rooms.remove(roomId);
            return null;
        }
        return retrieveRoomDetails(roomId);
    }

    public RoomDetailsDTO retrieveRoomDetails(String roomId) {
        Room room = rooms.get(roomId);
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

    /**
     * Returns a given room's GameSession
     * 
     * Primarily used by {@link #GameOrchestrator}
     */
    public GameSession getRoomGameSesssion(String roomId) {
        Room room = rooms.get(roomId);
        return room.getGameSession();
    }

    /**
     * Checks if a given username has successfully joined the room
     */
    public boolean isPlayerInRoom(String username, String roomId) {
        Room room = rooms.get(roomId);

        if (room == null) {
            return false;
        }
        
        return room.hasPlayer(username);
    }
}
