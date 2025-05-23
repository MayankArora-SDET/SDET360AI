package com.sdet.sdet360.tenant.repository;


import com.sdet.sdet360.tenant.model.QualityObjectiveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface QualityObjectiveStatusRepository extends JpaRepository<QualityObjectiveStatus, UUID> {
    List<QualityObjectiveStatus> findByVerticalId(UUID verticalId);
}
