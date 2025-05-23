package com.sdet.sdet360.tenant.service;


import com.sdet.sdet360.tenant.dto.QualityObjectiveStatusDto;
import com.sdet.sdet360.tenant.model.QualityObjectiveStatus;
import com.sdet.sdet360.tenant.model.Vertical;
import com.sdet.sdet360.tenant.repository.QualityObjectiveStatusRepository;
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
public class QualityObjectiveStatusService {
    private final QualityObjectiveStatusRepository repository;
    private final VerticalRepository verticalRepository;

    @Transactional
    public QualityObjectiveStatusDto create(QualityObjectiveStatusDto dto) {
        Vertical vertical = verticalRepository.findById(dto.getVerticalId())
                .orElseThrow(() -> new EntityNotFoundException("Vertical not found"));

        QualityObjectiveStatus entity = new QualityObjectiveStatus();
        mapDtoToEntity(dto, entity, vertical);
        entity = repository.save(entity);
        return mapEntityToDto(entity);
    }

    public List<QualityObjectiveStatusDto> findByVerticalId(UUID verticalId) {
        return repository.findByVerticalId(verticalId).stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public QualityObjectiveStatusDto update(UUID id, QualityObjectiveStatusDto dto) {
        QualityObjectiveStatus entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("QualityObjectiveStatus not found"));

        Vertical vertical = verticalRepository.findById(dto.getVerticalId())
                .orElseThrow(() -> new EntityNotFoundException("Vertical not found"));

        mapDtoToEntity(dto, entity, vertical);
        entity = repository.save(entity);
        return mapEntityToDto(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    private void mapDtoToEntity(QualityObjectiveStatusDto dto, QualityObjectiveStatus entity, Vertical vertical) {
        // Only update keyFeature if it's not null in the DTO
        if (dto.getKeyFeature() != null) {
            entity.setKeyFeature(dto.getKeyFeature());
        }
        entity.setCategory(dto.getCategory());
        entity.setSuccessCriteriaLevel1(dto.getSuccessCriteriaLevel1());
        entity.setSuccessCriteriaLevel2(dto.getSuccessCriteriaLevel2());
        entity.setStatus(dto.getStatus());
        entity.setVertical(vertical);
    }

    private QualityObjectiveStatusDto mapEntityToDto(QualityObjectiveStatus entity) {
        QualityObjectiveStatusDto dto = new QualityObjectiveStatusDto();
        dto.setId(entity.getId());
        dto.setKeyFeature(entity.getKeyFeature());
        dto.setCategory(entity.getCategory());
        dto.setSuccessCriteriaLevel1(entity.getSuccessCriteriaLevel1());
        dto.setSuccessCriteriaLevel2(entity.getSuccessCriteriaLevel2());
        dto.setStatus(entity.getStatus());
        dto.setVerticalId(entity.getVertical().getId());
        return dto;
    }
}