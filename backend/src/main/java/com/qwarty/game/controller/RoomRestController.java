package com.qwarty.game.controller;

import com.qwarty.game.dto.RoomDetailsDTO;
import com.qwarty.game.dto.RoomIdDTO;
import com.qwarty.game.service.RoomManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomRestController {

    private final RoomManager roomManagerService;

    @PostMapping("/create")
    public ResponseEntity<RoomIdDTO> create() {
        RoomIdDTO roomIdDto= roomManagerService.createRoom();
        return ResponseEntity.ok(roomIdDto);
    }

    @GetMapping("/info/{roomId}")
    public ResponseEntity<RoomDetailsDTO> info(@PathVariable String roomId) {
        RoomDetailsDTO roomDetails = roomManagerService.retrieveRoomDetails(roomId);
        return ResponseEntity.ok(roomDetails);
    }
}
