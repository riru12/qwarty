package com.qwarty.game.controller;

import com.qwarty.auth.lov.UserType;
import com.qwarty.game.dto.RoomDetailsDTO;
import com.qwarty.game.lov.GameMode;
import com.qwarty.game.service.RoomManager;
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

    private final RoomManager roomManagerService;

    @PostMapping("/create")
    public ResponseEntity<RoomDetailsDTO> create(@RequestParam String mode, HttpSession session) {
        String sessionUid = (String) session.getAttribute("SESSION_UID");
        String username = (String) session.getAttribute("USERNAME");
        UserType userType = (UserType) session.getAttribute("USER_TYPE");

        RoomDetailsDTO roomDetails = roomManagerService.createRoom(GameMode.from(mode), sessionUid, username, userType);
        return ResponseEntity.ok(roomDetails);
    }

    @PostMapping("/join/{roomId}")
    public ResponseEntity<RoomDetailsDTO> join(@PathVariable String roomId, HttpSession session) {
        String sessionUid = (String) session.getAttribute("SESSION_UID");
        String username = (String) session.getAttribute("USERNAME");
        UserType userType = (UserType) session.getAttribute("USER_TYPE");

        RoomDetailsDTO roomDetails = roomManagerService.joinRoom(roomId, sessionUid, username, userType);
        return ResponseEntity.ok(roomDetails);
    }
}
