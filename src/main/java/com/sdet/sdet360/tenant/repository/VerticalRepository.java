package com.sdet.sdet360.tenant.repository;

import com.sdet.sdet360.tenant.model.Vertical;
import com.sdet.sdet360.tenant.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VerticalRepository extends JpaRepository<Vertical, UUID> {
    List<Vertical> findByUser(User user);
    List<Vertical> findByUserId(UUID userId);
    Optional<Vertical> findByIdAndUserId(UUID id, UUID userId);
    
    @Query("SELECT v FROM Vertical v WHERE v.deletedAt IS NULL")
    List<Vertical> findAllActive();
    
    @Query("SELECT v FROM Vertical v WHERE v.id = :id AND v.deletedAt IS NULL")
    Optional<Vertical> findActiveById(@Param("id") UUID id);
    
    @Query("SELECT v FROM Vertical v WHERE v.user.id = :userId AND v.deletedAt IS NULL")
    List<Vertical> findActiveByUserId(@Param("userId") UUID userId);
}
