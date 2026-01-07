package com.qwarty.game.controller;

import com.qwarty.game.dto.RoomDetailsDTO;
import com.qwarty.game.lov.GameMode;
import com.qwarty.game.service.RoomManager;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomRestController {

    private final RoomManager roomManagerService;

    @PostMapping("/create")
    public ResponseEntity<RoomDetailsDTO> create(@RequestParam String mode, HttpSession session) {
        RoomDetailsDTO roomDetails = roomManagerService.createRoom(GameMode.from(mode));
        return ResponseEntity.ok(roomDetails);
    }

    @GetMapping("/info/{roomId}")
    public ResponseEntity<RoomDetailsDTO> info(@PathVariable String roomId) {
        RoomDetailsDTO roomDetails = roomManagerService.retrieveRoomDetails(roomId);
        return ResponseEntity.ok(roomDetails);
    }
}
