package com.example.demo.service;

import com.example.demo.entity.BrakeDisc;
import com.example.demo.repository.BrakeDiscRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BrakeDiscService {

    private final BrakeDiscRepository repository;

    public List<BrakeDisc> findAll() {
        return repository.findAll();
    }

    public Page<BrakeDisc> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        return repository.findAll(pageable);
    }

    public Optional<BrakeDisc> findById(Long id) {
        return repository.findById(id);
    }

    public List<BrakeDisc> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        return repository.search(keyword);
    }

    public Page<BrakeDisc> searchPageable(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll(pageable);
        }
        return repository.searchPageable(keyword, pageable);
    }

    @Transactional
    public BrakeDisc save(BrakeDisc brakeDisc) {
        return repository.save(brakeDisc);
    }

    @Transactional
    public BrakeDisc update(Long id, BrakeDisc brakeDisc) {
        return repository.findById(id).map(existing -> {
            existing.setCode(brakeDisc.getCode());
            existing.setCarSeries(brakeDisc.getCarSeries());
            existing.setCarModel(brakeDisc.getCarModel());
            existing.setPosition(brakeDisc.getPosition());
            existing.setType(brakeDisc.getType());
            existing.setWeight(brakeDisc.getWeight());
            existing.setSurfaceTreatment(brakeDisc.getSurfaceTreatment());
            existing.setProcess(brakeDisc.getProcess());
            existing.setOuterDiameter(brakeDisc.getOuterDiameter());
            existing.setTotalHeight(brakeDisc.getTotalHeight());
            existing.setBrakeThickness(brakeDisc.getBrakeThickness());
            existing.setCenterHole(brakeDisc.getCenterHole());
            existing.setMountingHole(brakeDisc.getMountingHole());
            existing.setOe1(brakeDisc.getOe1());
            existing.setOe2(brakeDisc.getOe2());
            existing.setOe3(brakeDisc.getOe3());
            existing.setOe4(brakeDisc.getOe4());
            existing.setOe5(brakeDisc.getOe5());
            existing.setOe6(brakeDisc.getOe6());
            existing.setOe7(brakeDisc.getOe7());
            existing.setOe8(brakeDisc.getOe8());
            existing.setOe9(brakeDisc.getOe9());
            existing.setOe10(brakeDisc.getOe10());
            existing.setOe11(brakeDisc.getOe11());
            existing.setEk25(brakeDisc.getEk25());
            existing.setEk1(brakeDisc.getEk1());
            existing.setShengdiCode(brakeDisc.getShengdiCode());
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Record not found"));
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public List<BrakeDisc> importFromExcel(MultipartFile file) throws IOException {
        List<BrakeDisc> discs = new ArrayList<>();
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String code = getCellValueAsString(row.getCell(0));
                if (code == null || code.trim().isEmpty()) {
                    continue;
                }

                // 检查编号是否已存在
                List<BrakeDisc> existing = repository.findByCodeContaining(code);
                BrakeDisc disc;
                if (!existing.isEmpty()) {
                    // 更新已有记录
                    disc = existing.get(0);
                } else {
                    // 创建新记录
                    disc = new BrakeDisc();
                    disc.setCode(code);
                }

                disc.setCarSeries(getCellValueAsString(row.getCell(1)));
                disc.setCarModel(getCellValueAsString(row.getCell(2)));
                disc.setPosition(getCellValueAsString(row.getCell(3)));
                disc.setType(getCellValueAsString(row.getCell(4)));
                disc.setWeight(getCellValueAsDouble(row.getCell(5)));
                disc.setSurfaceTreatment(getCellValueAsString(row.getCell(6)));
                disc.setProcess(getCellValueAsString(row.getCell(7)));
                disc.setOuterDiameter(getCellValueAsDouble(row.getCell(8)));
                disc.setTotalHeight(getCellValueAsDouble(row.getCell(9)));
                disc.setBrakeThickness(getCellValueAsDouble(row.getCell(10)));
                disc.setCenterHole(getCellValueAsDouble(row.getCell(11)));
                disc.setMountingHole(getCellValueAsString(row.getCell(12)));
                disc.setOe1(getCellValueAsString(row.getCell(13)));
                disc.setOe2(getCellValueAsString(row.getCell(14)));
                disc.setOe3(getCellValueAsString(row.getCell(15)));
                disc.setOe4(getCellValueAsString(row.getCell(16)));
                disc.setOe5(getCellValueAsString(row.getCell(17)));
                disc.setOe6(getCellValueAsString(row.getCell(18)));
                disc.setOe7(getCellValueAsString(row.getCell(19)));
                disc.setOe8(getCellValueAsString(row.getCell(20)));
                disc.setOe9(getCellValueAsString(row.getCell(21)));
                disc.setOe10(getCellValueAsString(row.getCell(22)));
                disc.setOe11(getCellValueAsString(row.getCell(23)));
                disc.setEk25(getCellValueAsString(row.getCell(24)));
                disc.setEk1(getCellValueAsString(row.getCell(25)));
                disc.setShengdiCode(getCellValueAsString(row.getCell(26)));

                discs.add(repository.save(disc));
            }
        }
        return discs;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    private Double getCellValueAsDouble(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return null;
                }
            default:
                return null;
        }
    }
}
