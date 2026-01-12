package com.qwarty.game.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qwarty.game.dto.GameStateDTO;
import com.qwarty.game.dto.RoomIdDTO;
import com.qwarty.game.service.GameRoomService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class GameRoomRestController {

    private final GameRoomService gameRoomService;

    /**
     * Creates a room and returns a DTO containing the room's ID
     */
    @PostMapping
    public ResponseEntity<RoomIdDTO> create() {
        RoomIdDTO roomIdDto = gameRoomService.createRoom();
        return ResponseEntity.ok(roomIdDto);
    }

    /**
     * Gets the current GameState of a given room and returns it in a DTO
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<GameStateDTO> state(@PathVariable String roomId) {
        GameStateDTO roomDetailsDto = gameRoomService.getGameRoomState(roomId);
        return ResponseEntity.ok(roomDetailsDto);
    }

}
