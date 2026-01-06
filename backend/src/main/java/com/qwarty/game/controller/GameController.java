package com.qwarty.game.controller;

import com.qwarty.game.model.GameEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class GameController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/room/{roomId}")
    public void sendMessage(
            @Payload GameEvent event, @DestinationVariable String roomId, StompHeaderAccessor accessor) {

        String username = (String) accessor.getSessionAttributes().get("USERNAME");
        event.setSender(username);
        event.setRoomId(roomId);

        messagingTemplate.convertAndSend("/topic/room/" + event.getRoomId(), event);
    }
}
