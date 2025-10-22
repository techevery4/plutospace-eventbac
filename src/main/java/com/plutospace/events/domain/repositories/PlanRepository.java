package com.plutospace.events.domain.repositories;

import com.plutospace.events.commons.repositories.BaseRepository;
import com.plutospace.events.domain.entities.Plan;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends BaseRepository<Plan, String> {
}
