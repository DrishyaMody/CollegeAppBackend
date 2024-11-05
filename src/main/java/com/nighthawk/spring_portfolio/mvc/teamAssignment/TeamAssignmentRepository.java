package com.nighthawk.spring_portfolio.mvc.teamAssignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamAssignmentRepository extends JpaRepository<TeamAssignment, Long> {
    boolean existsByTeamNameAndUsername(String teamName, String username);
}
