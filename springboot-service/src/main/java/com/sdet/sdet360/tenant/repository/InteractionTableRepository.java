package com.sdet.sdet360.tenant.repository;
import com.sdet.sdet360.tenant.model.InteractionTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InteractionTableRepository extends JpaRepository<InteractionTable, UUID> {

    @Query("SELECT i FROM InteractionTable i WHERE i.deletedAt IS NULL")
    List<InteractionTable> findAllActive();

    @Query("SELECT i FROM InteractionTable i WHERE LOWER(i.category) = LOWER(:category) AND i.deletedAt IS NULL")
    List<InteractionTable> findByCategoryAndNotDeleted(@Param("category") String category);

    Optional<InteractionTable> findByTestcaseId(UUID testcaseId);
}
