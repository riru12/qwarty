package com.qwarty.game.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.qwarty.game.dto.PlayerListEventDTO;
import com.qwarty.game.dto.RoomDetailsDTO;
import com.qwarty.game.lov.MessageType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomManager roomManager;

    public void joinRoom(String roomId, String username) {
        // let RoomManager handle the actual process of joining the room
        RoomDetailsDTO roomDetailsDto = roomManager.joinRoom(roomId, username);

        PlayerListEventDTO event = PlayerListEventDTO.builder()
                .sender(username)
                .roomId(roomDetailsDto.roomId())
                .players(roomDetailsDto.players())
                .messageType(MessageType.JOIN)
                .build();
        messagingTemplate.convertAndSend("/topic/room/" + roomId, event);
    }

    public void leaveRoom(String roomId, String username) {
        RoomDetailsDTO roomDetailsDto = roomManager.leaveRoom(roomId, username);    // returns null if the room ends up empty

        if (roomDetailsDto == null) { // if the room has been closed after the last person left
            return;
        }

        // if there are players left, announce that you have left
        PlayerListEventDTO eventDTO = PlayerListEventDTO.builder()
                .sender(username)
                .roomId(roomDetailsDto.roomId())
                .players(roomDetailsDto.players())
                .messageType(MessageType.LEAVE)
                .build();
        messagingTemplate.convertAndSend("/topic/room/" + roomId, eventDTO);
    }

}
