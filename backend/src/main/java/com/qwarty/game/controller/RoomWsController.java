package com.qwarty.game.controller;

import com.qwarty.game.dto.PlayerListEventDTO;
import com.qwarty.game.dto.RoomDetailsDTO;
import com.qwarty.game.lov.RoomMessageType;
import com.qwarty.game.service.RoomManager;
import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.time.Instant;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class RoomWsController {

    private final RoomManager roomManager;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/room.join/{roomId}")
    public void joinRoom(@DestinationVariable String roomId, StompHeaderAccessor accessor, Principal principal) {
        accessor.getSessionAttributes()
                .put("ROOM_ID", roomId); // upon joining a room, save the roomId into the WS session's attributes
        String username = principal.getName();

        // let RoomManager handle the joining
        RoomDetailsDTO roomDetailsDto = roomManager.joinRoom(roomId, username);

        PlayerListEventDTO event = PlayerListEventDTO.builder()
                .roomId(roomDetailsDto.roomId())
                .players(roomDetailsDto.players())
                .gameMode(roomDetailsDto.gameMode())
                .messageType(RoomMessageType.JOIN)
                .timestamp(Instant.now())
                .build();
        messagingTemplate.convertAndSend("/topic/room/" + roomId, event);
    }

    @MessageMapping("/room.leave/{roomId}")
    public void leaveRoom(@DestinationVariable String roomId, Principal principal) {
        String username = principal.getName();

        // let RoomManager handle leaving the room
        RoomDetailsDTO roomDetailsDto = roomManager.leaveRoom(roomId, username);

        if (roomDetailsDto == null) { // if the room has been closed after the last person left
            return;
        }

        PlayerListEventDTO eventDTO = PlayerListEventDTO.builder()
                .roomId(roomDetailsDto.roomId())
                .players(roomDetailsDto.players())
                .gameMode(roomDetailsDto.gameMode())
                .messageType(RoomMessageType.LEAVE)
                .timestamp(Instant.now())
                .build();
        messagingTemplate.convertAndSend("/topic/room/" + roomId, eventDTO);
    }
}
