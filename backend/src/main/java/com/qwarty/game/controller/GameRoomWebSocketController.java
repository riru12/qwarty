package com.qwarty.game.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.qwarty.game.service.GameRoomService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class GameRoomWebSocketController {

    private final GameRoomService gameRoomService;

    @MessageMapping("/game.join/{roomId}")
    public void joinRoom(@DestinationVariable String roomId, Principal principal) {
        gameRoomService.joinRoom(roomId, principal.getName());
    }

    @MessageMapping("/game.leave/{roomId}")
    public void leaveRoom(@DestinationVariable String roomId, Principal principal) {
        gameRoomService.leaveRoom(roomId, principal.getName());
    }
    

}
