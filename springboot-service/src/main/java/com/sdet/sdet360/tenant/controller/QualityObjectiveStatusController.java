
package com.sdet.sdet360.tenant.controller;

import com.sdet.sdet360.tenant.dto.QualityObjectiveStatusDto;
import com.sdet.sdet360.tenant.service.QualityObjectiveStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/quality-status/{verticalId}/quality-objective-status")
@RequiredArgsConstructor
public class QualityObjectiveStatusController {

    private final QualityObjectiveStatusService service;

    @PostMapping
    public ResponseEntity<QualityObjectiveStatusDto> create(
            @PathVariable UUID verticalId,
            @Valid @RequestBody QualityObjectiveStatusDto dto) {
        dto.setVerticalId(verticalId);
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<QualityObjectiveStatusDto>> findByVerticalId(
            @PathVariable UUID verticalId) {
        return ResponseEntity.ok(service.findByVerticalId(verticalId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QualityObjectiveStatusDto> update(
            @PathVariable UUID verticalId,
            @PathVariable UUID id,
            @Valid @RequestBody QualityObjectiveStatusDto dto) {
        dto.setId(id);
        dto.setVerticalId(verticalId);
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID verticalId,
            @PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
