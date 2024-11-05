package com.nighthawk.spring_portfolio.mvc.teamAssignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeamAssignmentService {

    @Autowired
    private TeamAssignmentRepository repository;

    public boolean assignTeam(String teamName, String username) {
        // Check if the user is already assigned to the team
        if (repository.existsByTeamNameAndUsername(teamName, username)) {
            return false; // Assignment already exists
        }

        // Create and save the new assignment
        TeamAssignment assignment = new TeamAssignment();
        assignment.setTeamName(teamName);
        assignment.setUsername(username);
        repository.save(assignment);
        
        return true; // Assignment successful
    }
}
