package com.qwarty.game.controller;

import com.qwarty.game.dto.GameRoomDetailsDTO;
import com.qwarty.game.dto.GameRoomIdDTO;
import com.qwarty.game.service.GameRoomService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameRoomRestController {
    
    private final GameRoomService gameRoomService;

    @PostMapping("/create")
    public ResponseEntity<GameRoomIdDTO> create() {
        GameRoomIdDTO roomIdDto= gameRoomService.createRoom();
        return ResponseEntity.ok(roomIdDto);
    }

    @GetMapping("/info/{roomId}")
    public ResponseEntity<GameRoomDetailsDTO> info(@PathVariable String roomId) {
        GameRoomDetailsDTO roomDetails = gameRoomService.retrieveRoomDetails(roomId);
        return ResponseEntity.ok(roomDetails);
    }
}
