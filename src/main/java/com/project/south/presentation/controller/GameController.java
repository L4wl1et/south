package com.project.south.presentation.controller;

import com.project.south.application.dto.GameRequest;
import com.project.south.application.dto.GameResponse;
import com.project.south.application.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping
    public ResponseEntity<GameResponse> create(@Valid @RequestBody GameRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gameService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<GameResponse>> findAll() {
        return ResponseEntity.ok(gameService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(gameService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GameResponse> update(@PathVariable String id,
                                               @Valid @RequestBody GameRequest request) {
        return ResponseEntity.ok(gameService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        gameService.delete(id);
        return ResponseEntity.noContent().build();
    }
}