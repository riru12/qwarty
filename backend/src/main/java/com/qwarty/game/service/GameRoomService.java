package com.qwarty.game.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.qwarty.game.dto.RoomInfoDTO;
import com.qwarty.game.dto.RoomIdDTO;
import com.qwarty.game.lov.GameStatus;
import com.qwarty.game.model.GameRoom;
import com.qwarty.game.model.GameState;
import com.qwarty.game.session.GameSession;
import com.qwarty.game.session.GameSessionFactory;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameRoomService {

    private final Map<String, GameRoom> rooms = new ConcurrentHashMap<>();
    private final GameSessionFactory gameSessionFactory;

    /**
     * Returns DTO containing roomId
     * 
     * Called primarily from {@link #GameRoomRestController#create()}
     */
    public RoomIdDTO createRoom() {
        String roomId = UUID.randomUUID().toString();
        GameRoom room = new GameRoom(roomId);
        GameSession session = gameSessionFactory.create(roomId);

        room.setSession(session);
        rooms.put(roomId, room);
        return new RoomIdDTO(roomId);
    }

    /**
     * Returns DTO containing a room's GameState given a roomId
     * 
     * Called primarily from {@link #GameRoomRestController#info(String)}
     */
    public RoomInfoDTO getGameRoomInfo(String roomId) {
        GameRoom room = rooms.get(roomId);

        GameStatus status = room.getSession().getStatus();
        GameState state = room.getSession().getState();

        return new RoomInfoDTO(status, state);
    }
    
    public boolean joinRoom(String roomId, String username) {
        GameRoom room = rooms.get(roomId);
        if (room == null) {
            return false;
        }

        /**
         * 1. Add player into the room
         * 2. Initialize player's progress in the room's GameSession
         */
        if (room.addPlayer(username)) {
            room.getSession().addPlayer(username);
            room.getSession().startGame();
            return true;
        }
        return false;
    }

    public void leaveRoom(String roomId, String username) {
        GameRoom room = rooms.get(roomId);
        if (room != null) {
            room.removePlayer(username);
            if (room.isEmpty()) {
                rooms.remove(roomId);
            }
        }
    }

    public boolean deleteRoom(String roomId) {
        return rooms.remove(roomId) != null;
    }

    // public void handleGameInput(String roomId, String username, WebSocketInputDTO input) {
    //     GameRoom room = rooms.get(roomId);
    //     // room.getSession().handleInput(username, input);
    // }

}
