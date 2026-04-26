package com.codingshuttle.lovable_clone.Repository;

import com.codingshuttle.lovable_clone.Entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan,Long> {
}
