package com.sdet.sdet360.tenant.repository;

import com.sdet.sdet360.tenant.model.TestCaseWithText;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TestCaseWithTextRepository extends JpaRepository<TestCaseWithText, Long> {
    
    /**
     * Find test cases by feature ID
     * @param featureId The ID of the feature
     * @return List of test cases for the feature
     */
    @Query("SELECT t FROM TestCaseWithText t WHERE t.feature.id = :featureId")
    List<TestCaseWithText> findByFeatureId(@Param("featureId") UUID featureId);
    
    /**
     * Find test cases by feature ID with pagination
     * @param featureId The ID of the feature
     * @param pageable Pagination information
     * @return Page of test cases for the feature
     */
    @Query("SELECT t FROM TestCaseWithText t WHERE t.feature.id = :featureId")
    Page<TestCaseWithText> findByFeatureId(@Param("featureId") UUID featureId, Pageable pageable);
    
    /**
     * Find a test case by ID and feature ID
     * @param id The ID of the test case
     * @param featureId The ID of the feature
     * @return Optional containing the test case if found
     */
    @Query("SELECT t FROM TestCaseWithText t WHERE t.id = :id AND t.feature.id = :featureId")
    Optional<TestCaseWithText> findByIdAndFeatureId(
            @Param("id") Long id, 
            @Param("featureId") UUID featureId);
    
    /**
     * Find test cases by vertical ID (through feature relationship)
     * @param verticalId The ID of the vertical
     * @return List of test cases for the vertical
     */
    @Query("SELECT t FROM TestCaseWithText t WHERE t.feature.vertical.id = :verticalId")
    List<TestCaseWithText> findByVerticalId(@Param("verticalId") UUID verticalId);
    
    /**
     * Find test cases by vertical ID with pagination
     * @param verticalId The ID of the vertical
     * @param pageable Pagination information
     * @return Page of test cases for the vertical
     */
    @Query("SELECT t FROM TestCaseWithText t WHERE t.feature.vertical.id = :verticalId")
    Page<TestCaseWithText> findByVerticalId(
            @Param("verticalId") UUID verticalId, 
            Pageable pageable);
}
