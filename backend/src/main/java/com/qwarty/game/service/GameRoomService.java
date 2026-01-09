package com.qwarty.game.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.qwarty.game.dto.PlayerListEventDTO;
import com.qwarty.game.dto.GameRoomIdDTO;
import com.qwarty.game.dto.GameRoomDetailsDTO;
import com.qwarty.game.dto.GameRoomEventDTO;
import com.qwarty.game.lov.MessageType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameRoomService {

    private final SimpMessagingTemplate messagingTemplate;
    private final GameRoomManager roomManager;

    private final String server = "SERVER";

    public GameRoomIdDTO createRoom() {
        GameRoomIdDTO roomIdDto = roomManager.createRoom();
        return roomIdDto;
    }

    public GameRoomDetailsDTO retrieveRoomDetails(String roomId) {
        GameRoomDetailsDTO roomDetails = roomManager.retrieveRoomDetails(roomId);
        return roomDetails;
    }

    /**
     * Adds a user to a room, if condition is met this can also trigger start of the game
     */
    public void joinRoom(String roomId, String username) {
        // let RoomManager handle the actual process of joining the room
        GameRoomDetailsDTO roomDetailsDto = roomManager.joinRoom(roomId, username);

        PlayerListEventDTO event = PlayerListEventDTO.builder()
                .sender(username)
                .roomId(roomDetailsDto.roomId())
                .players(roomDetailsDto.players())
                .messageType(MessageType.JOIN)
                .build();
        messagingTemplate.convertAndSend("/topic/room/" + roomId, event);

        if (roomManager.canStartGame(roomId)) {
            this.announceStart(roomId);
        }
    }

    public void leaveRoom(String roomId, String username) {
        GameRoomDetailsDTO roomDetailsDto = roomManager.leaveRoom(roomId, username);    // returns null if the room ends up empty

        if (roomDetailsDto == null) { // if the room has been closed after the last person left
            return;
        }

        // if there are players left, announce that you have left
        PlayerListEventDTO event = PlayerListEventDTO.builder()
                .sender(username)
                .roomId(roomDetailsDto.roomId())
                .players(roomDetailsDto.players())
                .messageType(MessageType.LEAVE)
                .build();
        messagingTemplate.convertAndSend("/topic/room/" + roomId, event);
    }

    /**
     * Broadcast start of game to the room
     */
    public void announceStart(String roomId) {
        roomManager.initializeGame(roomId, messagingTemplate);

        GameRoomEventDTO event = GameRoomEventDTO.builder()
            .sender(server)
            .roomId(roomId)
            .messageType(MessageType.GAME_START)
            .build();
        messagingTemplate.convertAndSend("/topic/room/" + roomId, event);
    }

}
