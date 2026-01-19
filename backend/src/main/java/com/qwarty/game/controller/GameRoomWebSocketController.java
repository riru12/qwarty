package com.qwarty.game.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.qwarty.game.dto.GameInputDTO;
import com.qwarty.game.service.GameRoomService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class GameRoomWebSocketController {

    private final GameRoomService gameRoomService;

    @MessageMapping("/game.join/{roomId}")
    public void joinRoom(@DestinationVariable String roomId, SimpMessageHeaderAccessor headerAccessor, Principal principal) {
        headerAccessor.getSessionAttributes().put("ROOM_ID", roomId);
        gameRoomService.joinRoom(roomId, principal.getName());
    }

    @MessageMapping("/game.leave/{roomId}")
    public void leaveRoom(@DestinationVariable String roomId, Principal principal) {
        gameRoomService.leaveRoom(roomId, principal.getName());
    }
    
    @MessageMapping("/game.input/{roomId}")
     public void gameInput(@DestinationVariable String roomId, Principal principal, GameInputDTO input) {
        gameRoomService.handleGameInput(roomId, principal.getName(), input);
    }

}
