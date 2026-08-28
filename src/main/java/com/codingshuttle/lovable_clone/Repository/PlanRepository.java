package com.codingshuttle.lovable_clone.Repository;

import com.codingshuttle.lovable_clone.Entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan,Long> {
    Optional<Plan> findByStripePriceId(String id);
}
