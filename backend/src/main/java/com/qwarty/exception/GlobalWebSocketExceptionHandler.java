package com.qwarty.exception;

import java.security.Principal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalWebSocketExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalRestExceptionHandler.class);
    private final SimpMessagingTemplate messagingTemplate;

    @MessageExceptionHandler(Exception.class)
    public void handleAllExceptions(Exception exception, StompHeaderAccessor accessor, Principal principal) {
        logger.warn(
                "Exception occurred: {} - {}",
                exception.getClass().getSimpleName(),
                exception.getMessage());

        String roomId = (String) accessor.getSessionAttributes().get("ROOM_ID");

        Map<String, Object> error = Map.of(
                "type", "ERROR",
                "code", "INTERNAL_ERROR",
                "message", "An unexpected error occurred");
        
        messagingTemplate.convertAndSendToUser(principal.getName(),  "/queue/errors/" + roomId, error);
    }
}
