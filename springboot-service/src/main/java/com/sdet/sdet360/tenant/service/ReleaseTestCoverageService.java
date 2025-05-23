package com.sdet.sdet360.tenant.service;

import com.sdet.sdet360.tenant.dto.ReleaseTestCoverageDto;
import com.sdet.sdet360.tenant.model.ReleaseTestCoverage;
import com.sdet.sdet360.tenant.model.Vertical;
import com.sdet.sdet360.tenant.repository.ReleaseTestCoverageRepository;
import com.sdet.sdet360.tenant.repository.VerticalRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReleaseTestCoverageService {
    private final ReleaseTestCoverageRepository repository;
    private final VerticalRepository verticalRepository;

    @Transactional
    public ReleaseTestCoverageDto create(ReleaseTestCoverageDto dto) {
        Vertical vertical = verticalRepository.findById(dto.getVerticalId())
                .orElseThrow(() -> new EntityNotFoundException("Vertical not found"));

        ReleaseTestCoverage entity = new ReleaseTestCoverage();
        mapDtoToEntity(dto, entity, vertical);
        entity = repository.save(entity);
        return mapEntityToDto(entity);
    }

    public List<ReleaseTestCoverageDto> findByVerticalId(UUID verticalId) {
        return repository.findByVerticalId(verticalId).stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReleaseTestCoverageDto update(UUID id, ReleaseTestCoverageDto dto) {
        ReleaseTestCoverage entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ReleaseTestCoverage not found"));

        Vertical vertical = verticalRepository.findById(dto.getVerticalId())
                .orElseThrow(() -> new EntityNotFoundException("Vertical not found"));

        mapDtoToEntity(dto, entity, vertical);
        entity = repository.save(entity);
        return mapEntityToDto(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    private void mapDtoToEntity(ReleaseTestCoverageDto dto, ReleaseTestCoverage entity, Vertical vertical) {
        entity.setEpic(dto.getEpic());
        entity.setSeverity1(dto.getSeverity1());
        entity.setSeverity2(dto.getSeverity2());
        entity.setSeverity3(dto.getSeverity3());
        entity.setSeverity4(dto.getSeverity4());
        entity.setTestCases(dto.getTestCases());
        entity.setVertical(vertical);
    }

    private ReleaseTestCoverageDto mapEntityToDto(ReleaseTestCoverage entity) {
        ReleaseTestCoverageDto dto = new ReleaseTestCoverageDto();
        dto.setId(entity.getId());
        dto.setEpic(entity.getEpic());
        dto.setSeverity1(entity.getSeverity1());
        dto.setSeverity2(entity.getSeverity2());
        dto.setSeverity3(entity.getSeverity3());
        dto.setSeverity4(entity.getSeverity4());
        dto.setTestCases(entity.getTestCases());
        dto.setVerticalId(entity.getVertical().getId());
        return dto;
    }
}