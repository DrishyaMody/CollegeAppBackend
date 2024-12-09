// package com.nighthawk.spring_portfolio.mvc.person;

// import java.util.Comparator;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.stream.Collectors;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// @RestController
// @RequestMapping("/api/rankings")
// public class RankingsController {

//     @Autowired
//     private PersonJpaRepository repository;

//     /**
//      * Get top 10 users by balance and teams ranked by total balance.
//      * @return Rankings of users and teams.
//      */
//     @GetMapping("/leaderboard")
//     public ResponseEntity<Map<String, Object>> getRankings() {
//         // Fetch all people from the database
//         List<Person> people = repository.findAll();

//         // Sort users by balance (descending) and take the top 10
//         List<Map<String, Object>> topUsers = people.stream()
//                 .sorted(Comparator.comparingDouble(Person::getBalance).reversed())
//                 .limit(10)
//                 .map(person -> {
//                     Map<String, Object> userMap = new HashMap<>();
//                     userMap.put("id", person.getId());
//                     userMap.put("name", person.getName());
//                     userMap.put("balance", person.getBalance());
//                     return userMap;
//                 })
//                 .collect(Collectors.toList());

//         // Group by team and calculate team balances
//         Map<String, Double> teamBalances = new HashMap<>();
//         for (Person person : people) {
//             String team = person.getTeam();
//             if (team != null && !team.isEmpty()) {
//                 teamBalances.put(team, teamBalances.getOrDefault(team, 0.0) + person.getBalance());
//             }
//         }

//         // Sort teams by total balance (descending)
//         List<Map<String, Object>> rankedTeams = teamBalances.entrySet().stream()
//                 .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))  // Explicit comparator
//                 .map(entry -> {
//                     Map<String, Object> teamMap = new HashMap<>();
//                     teamMap.put("team", entry.getKey());
//                     teamMap.put("totalBalance", entry.getValue());
//                     return teamMap;
//                 })
//                 .collect(Collectors.toList());

//         // Combine rankings into a response
//         Map<String, Object> response = new HashMap<>();
//         response.put("topUsers", topUsers);
//         response.put("rankedTeams", rankedTeams);

//         return new ResponseEntity<>(response, HttpStatus.OK);
//     }
// }