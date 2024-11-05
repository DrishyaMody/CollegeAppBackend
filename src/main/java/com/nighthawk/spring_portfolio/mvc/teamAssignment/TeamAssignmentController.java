package com.nighthawk.spring_portfolio.mvc.teamAssignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams")
public class TeamAssignmentController {

    @Autowired
    private TeamAssignmentService service;

    @PostMapping("/assign")
    public boolean assignTeam(@RequestParam String teamName, @RequestParam String username) {
        return service.assignTeam(teamName, username);
    }
}
