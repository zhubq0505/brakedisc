package com.example.demo.controller;

import com.example.demo.entity.BrakeDisc;
import com.example.demo.service.BrakeDiscService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/brake-discs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BrakeDiscController {

    private final BrakeDiscService service;

    @GetMapping
    public ResponseEntity<List<BrakeDisc>> getAll(@RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(service.search(keyword));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrakeDisc> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BrakeDisc> create(@RequestBody BrakeDisc brakeDisc) {
        return ResponseEntity.ok(service.save(brakeDisc));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrakeDisc> update(@PathVariable Long id, @RequestBody BrakeDisc brakeDisc) {
        try {
            return ResponseEntity.ok(service.update(id, brakeDisc));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import")
    public ResponseEntity<List<BrakeDisc>> importExcel(@RequestParam("file") MultipartFile file) {
        try {
            List<BrakeDisc> imported = service.importFromExcel(file);
            return ResponseEntity.ok(imported);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
