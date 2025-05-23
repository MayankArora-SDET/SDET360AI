
package com.sdet.sdet360.tenant.controller;

import com.sdet.sdet360.config.annotation.SafeUUID;
import com.sdet.sdet360.tenant.dto.ReleaseTestCoverageDto;
import com.sdet.sdet360.tenant.service.ReleaseTestCoverageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/release/{verticalId}/release-test-coverage")
@RequiredArgsConstructor
public class ReleaseTestCoverageController {
    private final ReleaseTestCoverageService service;

    @PostMapping
    public ResponseEntity<ReleaseTestCoverageDto> create(
            @PathVariable @SafeUUID UUID verticalId,
            @Valid @RequestBody ReleaseTestCoverageDto dto) {
        dto.setVerticalId(verticalId);
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<ReleaseTestCoverageDto>> getAllByVerticalId(@PathVariable @SafeUUID UUID verticalId) {
        return ResponseEntity.ok(service.findByVerticalId(verticalId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReleaseTestCoverageDto> update(
            @PathVariable @SafeUUID UUID verticalId,
            @PathVariable UUID id,
            @Valid @RequestBody ReleaseTestCoverageDto dto) {
        dto.setId(id);
        dto.setVerticalId(verticalId);
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
