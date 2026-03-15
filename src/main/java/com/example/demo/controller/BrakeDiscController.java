package com.example.demo.controller;

import com.example.demo.entity.BrakeDisc;
import com.example.demo.service.BrakeDiscService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/brake-discs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BrakeDiscController {

    private final BrakeDiscService service;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<BrakeDisc> pageData = service.searchPageable(keyword, page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", pageData.getContent());
        response.put("currentPage", pageData.getNumber());
        response.put("totalItems", pageData.getTotalElements());
        response.put("totalPages", pageData.getTotalPages());
        response.put("size", pageData.getSize());
        
        return ResponseEntity.ok(response);
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

    @GetMapping("/test")
    public ResponseEntity<BrakeDisc> createTestData() {
        BrakeDisc test = new BrakeDisc();
        test.setCode("TEST001");
        test.setCarSeries("测试车系");
        test.setCarModel("测试车型");
        test.setPosition("前");
        test.setType("通风盘");
        test.setWeight(5.5);
        test.setSurfaceTreatment("电泳");
        test.setProcess("铸造");
        test.setOuterDiameter(280.0);
        test.setTotalHeight(45.0);
        test.setBrakeThickness(22.0);
        test.setCenterHole(65.0);
        test.setMountingHole("5x114.3");
        test.setOe1("OE123456");
        test.setEk25("EK25-001");
        test.setEk1("EK1-001");
        test.setShengdiCode("SD001");
        return ResponseEntity.ok(service.save(test));
    }
}
