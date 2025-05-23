package com.sdet.sdet360.tenant.repository;

import com.sdet.sdet360.tenant.model.ReleaseTestCoverage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReleaseTestCoverageRepository extends JpaRepository<ReleaseTestCoverage, UUID> {
    List<ReleaseTestCoverage> findByVerticalId(UUID verticalId);
}