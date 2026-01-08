package com.qwarty.game.controller;

import com.qwarty.game.service.RoomService;

import lombok.RequiredArgsConstructor;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class RoomWsController {

    private final RoomService roomService;

    @MessageMapping("/room.join/{roomId}")
    public void joinRoom(@DestinationVariable String roomId, StompHeaderAccessor accessor, Principal principal) {
        accessor.getSessionAttributes()
                .put("ROOM_ID", roomId); // upon joining a room, save the roomId into the WS session's attributes
        String username = principal.getName();

        roomService.joinRoom(roomId, username);
    }

    @MessageMapping("/room.leave/{roomId}")
    public void leaveRoom(@DestinationVariable String roomId, StompHeaderAccessor accessor, Principal principal) {
        String username = principal.getName();

        roomService.leaveRoom(roomId, username);
    }
}
