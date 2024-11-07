package com.nighthawk.spring_portfolio.mvc.person;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import lombok.Getter;

@RestController
@RequestMapping("/api")
public class PersonApiController {

    @Autowired
    private PersonJpaRepository repository;

    @Autowired
    private PersonDetailsService personDetailsService;

    // Retrieves the person based on JWT authentication details
    @GetMapping("/person/get")
    public ResponseEntity<Person> getPerson(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        Optional<Person> person = repository.findByEmail(email);
        return person.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                     .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Retrieves all people, sorted by name
    @GetMapping("/people")
    public ResponseEntity<List<Person>> getPeople() {
        return new ResponseEntity<>(repository.findAllByOrderByNameAsc(), HttpStatus.OK);
    }

    // Retrieves a person by ID
    @GetMapping("/person/{id}")
    public ResponseEntity<Person> getPerson(@PathVariable long id) {
        Optional<Person> optional = repository.findById(id);
        return optional.map(person -> new ResponseEntity<>(person, HttpStatus.OK))
                       .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Fetch person by email
    @GetMapping("/person")
    public ResponseEntity<Person> getPersonByEmail(@RequestParam String email) {
        Optional<Person> person = repository.findByEmail(email);
        return person.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                     .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Retrieves the balance of the authenticated user
    @GetMapping("/person/balance")
    public ResponseEntity<Double> getBalance(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        Optional<Person> person = repository.findByEmail(email);
        return person.map(value -> new ResponseEntity<>(value.getBalance(), HttpStatus.OK))
                     .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Updates the balance of the authenticated user
    @PutMapping("/person/balance")
    public ResponseEntity<String> updateBalance(Authentication authentication, @RequestBody BalanceUpdateRequest request) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        Optional<Person> optionalPerson = repository.findByEmail(email);

        if (optionalPerson.isPresent()) {
            Person person = optionalPerson.get();
            person.setBalance(person.getBalance() + request.getAmount());
            repository.save(person);
            return new ResponseEntity<>("Balance updated successfully", HttpStatus.OK);
        }

        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    }

    // Deletes a person by ID
    @DeleteMapping("/person/{id}")
    public ResponseEntity<Person> deletePerson(@PathVariable long id) {
        Optional<Person> optional = repository.findById(id);
        if (optional.isPresent()) {
            repository.deleteById(id);
            return new ResponseEntity<>(optional.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // DTO for Person creation and update
    @Getter
    public static class PersonDto {
        private String email;
        private String password;
        private String name;
        private String dob;
        private String pfp;
        private Boolean kasmServerNeeded;
        private String team;

        public PersonDto(String email, String password, String name, String dob, String pfp, Boolean kasmServerNeeded, String team) {
            this.email = email;
            this.password = password;
            this.name = name;
            this.dob = dob;
            this.pfp = pfp;
            this.kasmServerNeeded = kasmServerNeeded;
            this.team = team;
        }

        public String getTeam() {
            return team;
        }
    }

    // Creates a new person
    @PostMapping("/person/create")
    public ResponseEntity<Object> postPerson(@RequestBody PersonDto personDto) {
        Date dob;
        try {
            dob = new SimpleDateFormat("MM-dd-yyyy").parse(personDto.getDob());
        } catch (Exception e) {
            return new ResponseEntity<>(personDto.getDob() + " error; try MM-dd-yyyy", HttpStatus.BAD_REQUEST);
        }

        Person person = new Person(
            personDto.getEmail(),
            personDto.getPassword(),
            personDto.getName(),
            dob,
            personDto.getPfp(),
            personDto.getKasmServerNeeded(),
            personDetailsService.findRole("USER"),
            personDto.getTeam()
        );

        personDetailsService.save(person);
        return new ResponseEntity<>(personDto.getEmail() + " is created successfully", HttpStatus.CREATED);
    }

    // Updates a person's details based on authentication
    @PostMapping(value = "/person/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updatePerson(Authentication authentication, @RequestBody PersonDto personDto) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        Optional<Person> optionalPerson = repository.findByEmail(email);

        if (optionalPerson.isPresent()) {
            Person existingPerson = optionalPerson.get();
            if (personDto.getEmail() != null) existingPerson.setEmail(personDto.getEmail());
            if (personDto.getPassword() != null) existingPerson.setPassword(personDto.getPassword());
            if (personDto.getName() != null) existingPerson.setName(personDto.getName());
            if (personDto.getPfp() != null) existingPerson.setPfp(personDto.getPfp());
            if (personDto.getKasmServerNeeded() != null) existingPerson.setKasmServerNeeded(personDto.getKasmServerNeeded());
            if (personDto.getTeam() != null) existingPerson.setTeam(personDto.getTeam());

            if (personDto.getDob() != null) {
                try {
                    Date dob = new SimpleDateFormat("MM-dd-yyyy").parse(personDto.getDob());
                    existingPerson.setDob(dob);
                } catch (Exception e) {
                    return new ResponseEntity<>("Invalid date format; use MM-dd-yyyy", HttpStatus.BAD_REQUEST);
                }
            }

            personDetailsService.save(existingPerson);
            return new ResponseEntity<>(existingPerson, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Assigns a team to a person by email
    @PutMapping("/person/team")
    public ResponseEntity<String> assignTeamToPerson(@RequestParam String email, @RequestBody TeamAssignmentRequest request) {
        Optional<Person> optionalPerson = repository.findByEmail(email);

        if (optionalPerson.isPresent()) {
            Person person = optionalPerson.get();

            if (request.getTeam() == null || request.getTeam().isEmpty()) {
                return new ResponseEntity<>("Team name cannot be null or empty", HttpStatus.BAD_REQUEST);
            }

            if (request.getTeam().equals(person.getTeam())) {
                return new ResponseEntity<>("User is already assigned to this team.", HttpStatus.CONFLICT);
            }

            person.setTeam(request.getTeam());
            repository.save(person);

            return new ResponseEntity<>("Team assigned successfully", HttpStatus.OK);
        }

        return new ResponseEntity<>("Person not found", HttpStatus.NOT_FOUND);
    }

    @Getter
    public static class TeamAssignmentRequest {
        private String team;

        public String getTeam() {
            return team;
        }

        public void setTeam(String team) {
            this.team = team;
        }
    }

    // Request body for balance update
    public static class BalanceUpdateRequest {
        private Double amount; // Positive to add, negative to deduct

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }
    }
}