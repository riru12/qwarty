package com.qwarty.game.service;

import java.security.Principal;

import org.springframework.stereotype.Service;

import com.qwarty.exception.code.AppExceptionCode;
import com.qwarty.exception.type.AppException;
import com.qwarty.game.lov.GameStatus;
import com.qwarty.game.session.GameSession;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameOrchestrator {

    private final RoomManager roomManager;

    public void handlePlayerJoin(String roomId, Principal principal) {
        GameSession gameSession = roomManager.getRoomGameSesssion(roomId);

        // make sure that a game session exists in the room
        if (gameSession == null) {
            return;
        }
        System.out.println("GAME JOIN: " + principal.getName());

        if (!roomManager.isPlayerInRoom(roomId, principal.getName())) {
            throw new AppException(AppExceptionCode.USER_NOT_IN_ROOM);
        }

        if (gameSession.getStatus() == GameStatus.WAITING) {
            gameSession.addPlayer(principal.getName());
        }
    }

    public void handlePlayerReady(String roomId, Principal principal) {
        GameSession gameSession = roomManager.getRoomGameSesssion(roomId);

        // make sure that a game session exists in the room
        if (gameSession == null) {
            return;
        }

        if (gameSession.getStatus() == GameStatus.COUNTDOWN) {  // if the player joined while in COUNTDOWN, player can still participate
            gameSession.broadcastUserOngoingCountdown(principal);
        }
        if (gameSession.canStart()) {   // if the game hasn't started yet and now meets start condition, begin countdown
            gameSession.broadcastAllAndStartCountdown();
        }
    }

}
