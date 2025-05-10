package com.sdet.sdet360.tenant.repository;

import com.sdet.sdet360.tenant.model.EventsTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface EventsTableRepository extends JpaRepository<EventsTable, UUID> {
}
