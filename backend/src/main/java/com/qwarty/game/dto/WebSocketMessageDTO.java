package com.qwarty.game.dto;

import java.time.Instant;

import com.qwarty.game.lov.MessageType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessageDTO<T> {
    private MessageType messageType;
    private T payload;
    private final Instant timestamp = Instant.now();

    public static <T> WebSocketMessageDTO<T> of(MessageType type, T payload) {
        return WebSocketMessageDTO.<T>builder()
                .messageType(type)
                .payload(payload)
                .build();
    }
}
