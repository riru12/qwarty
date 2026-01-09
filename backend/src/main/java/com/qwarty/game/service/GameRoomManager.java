package com.qwarty.game.service;

import com.qwarty.exception.code.AppExceptionCode;
import com.qwarty.exception.type.AppException;
import com.qwarty.game.dto.GameRoomDetailsDTO;
import com.qwarty.game.dto.GameRoomIdDTO;
import com.qwarty.game.model.GameRoom;
import com.qwarty.game.session.StackerGameRunner;
import com.qwarty.game.session.StackerGameSession;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameRoomManager {

    private final Map<String, GameRoom> rooms = new ConcurrentHashMap<>();

    /**
     * Creates a room and stores it in memory by hash map, returns its details in a DTO
     */
    public GameRoomIdDTO createRoom() {
        String roomId = generateUniqueRoomId();
        GameRoom room = new GameRoom(roomId);

        rooms.put(roomId, room);
        return new GameRoomIdDTO(roomId);
    }

    /**
     * Retrieve room details, which includes the given roomId and player(s) inside the room
     */
    public GameRoomDetailsDTO retrieveRoomDetails(String roomId) {
        GameRoom room = rooms.get(roomId);
        if (room == null) {
            throw new AppException(AppExceptionCode.ROOM_NOT_FOUND);
        }
        List<String> playerList = room.getPlayers();
        return new GameRoomDetailsDTO(roomId, playerList);
    }

    /**
     * Joins a room by creating a connection to a room via hashmap, returns updated room details in a DTO
     */
    public GameRoomDetailsDTO joinRoom(String roomId, String username) {
        GameRoom room = rooms.get(roomId);
        if (room == null) {
            throw new AppException(AppExceptionCode.ROOM_NOT_FOUND);
        }
        if (room.isFull()) {
            throw new AppException(AppExceptionCode.ROOM_FULL);
        }
        if (!room.addConnection(username)) {
            throw new AppException(AppExceptionCode.ROOM_FULL);
        };

        return retrieveRoomDetails(roomId);
    }

    /**
     * Leaves a room by removing a connection to the room, returns updated room details in a DTO
     */
    public GameRoomDetailsDTO leaveRoom(String roomId, String username) {
        GameRoom room = rooms.get(roomId);
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

    private String generateUniqueRoomId() {
        String roomId;

        do {
            roomId = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        } while (rooms.containsKey(roomId));

        return roomId;
    }

    /**
     * Checks if the room's game can be started and initialized
     */
    public boolean canStartGame(String roomId) {
        GameRoom room = rooms.get(roomId);
        if (room.getPlayerConnections().size() < room.getSLOTS()) {
            return false;
        }
        return true;
    }

    /**
     * Initialize a game session for the room
     */
    public void initializeGame(String roomId, SimpMessagingTemplate messagingTemplate) {
        GameRoom room = rooms.get(roomId);
        StackerGameSession session = new StackerGameSession(room.getPlayers());
        StackerGameRunner runner = new StackerGameRunner(roomId, session, messagingTemplate);

        room.setStackerGameSession(session);
        room.setStackerGameRunner(runner);

        runner.start();
    }
}
