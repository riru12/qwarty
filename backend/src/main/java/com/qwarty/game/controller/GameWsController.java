package com.qwarty.game.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.qwarty.game.service.GameOrchestrator;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class GameWsController {

    private final GameOrchestrator gameOrchestrator;

    @MessageMapping("/game.join/{roomId}")
    public void gameJoin(@DestinationVariable String roomId, Principal principal) {
        gameOrchestrator.handlePlayerJoin(roomId, principal);
    }

    @MessageMapping("/game.ready/{roomId}")
    public void gameReady(@DestinationVariable String roomId, Principal principal) {
        gameOrchestrator.handlePlayerReady(roomId, principal);
    }

}
