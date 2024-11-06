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

    @GetMapping("/person/get")
    public ResponseEntity<Person> getPerson(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        Person person = repository.findByEmail(email);
        return person != null ? new ResponseEntity<>(person, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/people")
    public ResponseEntity<List<Person>> getPeople() {
        return new ResponseEntity<>(repository.findAllByOrderByNameAsc(), HttpStatus.OK);
    }

    @GetMapping("/person/{id}")
    public ResponseEntity<Person> getPerson(@PathVariable long id) {
        Optional<Person> optional = repository.findById(id);
        return optional.map(person -> new ResponseEntity<>(person, HttpStatus.OK))
                       .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Fetch person by email
    @GetMapping("/person")
    public ResponseEntity<Person> getPersonByEmail(@RequestParam String email) {
        Person person = repository.findByEmail(email);
        return person != null ? new ResponseEntity<>(person, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/person/{id}")
    public ResponseEntity<Person> deletePerson(@PathVariable long id) {
        Optional<Person> optional = repository.findById(id);
        if (optional.isPresent()) {
            repository.deleteById(id);
            return new ResponseEntity<>(optional.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Getter
    public static class PersonDto {
        private String email;
        private String password;
        private String name;
        private String dob;
        private String pfp;
        private Boolean kasmServerNeeded;
        private String team; // New field for team

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

    @PostMapping(value = "/person/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updatePerson(Authentication authentication, @RequestBody PersonDto personDto) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        Optional<Person> optionalPerson = Optional.ofNullable(repository.findByEmail(email));

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

    @PutMapping("/person/{id}/team")
    public ResponseEntity<String> assignTeamToPerson(@PathVariable Long id, @RequestBody TeamAssignmentRequest request) {
        Optional<Person> optionalPerson = repository.findById(id);

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
}
