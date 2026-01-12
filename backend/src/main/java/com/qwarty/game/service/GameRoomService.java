package com.qwarty.game.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.qwarty.game.broadcaster.GameBroadcaster;
import com.qwarty.game.dto.GameStateDTO;
import com.qwarty.game.dto.RoomIdDTO;
import com.qwarty.game.model.GameRoom;
import com.qwarty.game.model.GameState;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameRoomService {

    private final Map<String, GameRoom> rooms = new ConcurrentHashMap<>();
    private final GameBroadcaster gameBroadcaster;

    /**
     * Returns DTO containing roomId
     * 
     * Called primarily from {@link #GameRoomRestController#create()}
     */
    public RoomIdDTO createRoom() {
        String roomId = UUID.randomUUID().toString();
        GameRoom room = new GameRoom(roomId);
        rooms.put(roomId, room);
        return new RoomIdDTO(roomId);
    }

    /**
     * Returns DTO containing a room's GameState given a roomId
     * 
     * Called primarily from {@link #GameRoomRestController#info(String)}
     */
    public GameStateDTO getRoomInfo(String roomId) {
        GameRoom room = rooms.get(roomId);
        GameState state = room.getSession().getState();
        return new GameStateDTO(state);
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

}
