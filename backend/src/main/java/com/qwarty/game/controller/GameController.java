package com.qwarty.game.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/game")
public class GameController {

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createGame() {
        return ResponseEntity.ok(Map.of("message", "hello"));
    }
}
