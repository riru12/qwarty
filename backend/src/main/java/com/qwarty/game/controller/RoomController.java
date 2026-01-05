package com.qwarty.game.controller;

import com.qwarty.game.lov.GameMode;
import com.qwarty.game.model.Room;
import com.qwarty.game.service.RoomManagerService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomManagerService roomManagerService;

    @PostMapping("/create")
    public ResponseEntity<Room> create(@RequestParam String mode, HttpSession session) {
        String username = (String) session.getAttribute("USERNAME");

        Room room = roomManagerService.createRoom(GameMode.from(mode), username);
        return ResponseEntity.ok(room);
    }

    @PostMapping("/join/{roomId}")
    public ResponseEntity<Room> join(@PathVariable String roomId, HttpSession session) {
        String username = (String) session.getAttribute("USERNAME");

        Room room = roomManagerService.joinRoom(roomId, username);
        return ResponseEntity.ok(room);
    }
}
