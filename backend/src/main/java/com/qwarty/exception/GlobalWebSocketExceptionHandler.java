package com.qwarty.exception;

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
    public void handleAllExceptions(Exception exception, StompHeaderAccessor accessor) {
        logger.warn(
                "Exception occurred in WebSocket: {} - {}",
                exception.getClass().getSimpleName(),
                exception.getMessage());

        String sessionUid = (String) accessor.getSessionAttributes().get("SESSION_UID");

        if (sessionUid == null) {
            return;
        }

        Map<String, Object> error = Map.of(
                "type", "ERROR",
                "code", "INTERNAL_ERROR",
                "message", "An unexpected error occurred");

        messagingTemplate.convertAndSendToUser(sessionUid, "/queue/errors", error);
    }
}
