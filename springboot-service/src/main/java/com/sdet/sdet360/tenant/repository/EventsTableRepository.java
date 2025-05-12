package com.sdet.sdet360.tenant.repository;

import com.sdet.sdet360.tenant.model.EventsTable;
import com.sdet.sdet360.tenant.model.InteractionTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventsTableRepository extends JpaRepository<EventsTable, UUID> {
    List<EventsTable> findByInteraction(InteractionTable interaction);
    
    @Query("SELECT e FROM EventsTable e WHERE e.interaction.testcaseId = :testcaseId")
    List<EventsTable> findByTestcaseId(UUID testcaseId);
}
