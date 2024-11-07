package com.nighthawk.spring_portfolio.mvc.person;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToMany
    @JoinTable(
        name = "person_person_sections",
        joinColumns = @JoinColumn(name = "person_id"),
        inverseJoinColumns = @JoinColumn(name = "section_id")
    )
    private Collection<personSections> sections = new ArrayList<>();

    @ManyToMany
    private Collection<PersonRole> roles = new ArrayList<>();

    @NotEmpty
    @Size(min = 5)
    @Column(unique = true)
    @Email
    private String email;

    @NotEmpty
    private String password;

    @NonNull
    @Size(min = 2, max = 30, message = "Name (2 to 30 chars)")
    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dob;

    @Column(length = 255, nullable = true)
    private String pfp;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean kasmServerNeeded = false;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Map<String, Object>> stats = new HashMap<>();

    private String team;

    // New balance field
    @Column(nullable = false, columnDefinition = "double default 0.0")
    private Double balance = 5000.0;

    // Constructor matching the parameters in postPerson method
    public Person(String email, String password, String name, Date dob, String pfp, Boolean kasmServerNeeded, PersonRole role, String team) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.dob = dob;
        this.pfp = pfp;
        this.kasmServerNeeded = kasmServerNeeded;
        this.roles.add(role);
        this.team = team;
        this.balance = 0.0; // Initial balance set to 0.0
    }

    public int getAge() {
        if (this.dob != null) {
            LocalDate birthDay = this.dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return Period.between(birthDay, LocalDate.now()).getYears();
        }
        return -1;
    }

    public static Person createPerson(String name, String email, String password, Boolean kasmServerNeeded, String dob) {
        return createPerson(name, email, password, null, kasmServerNeeded, dob, Arrays.asList("ROLE_USER"));
    }

    public static Person createPerson(String name, String email, String password, String pfp, Boolean kasmServerNeeded, String dob, List<String> roleNames) {
        Person person = new Person();
        person.setName(name);
        person.setEmail(email);
        person.setPassword(password);
        person.setKasmServerNeeded(kasmServerNeeded);
        person.setPfp(pfp);
        try {
            Date date = new SimpleDateFormat("MM-dd-yyyy").parse(dob);
            person.setDob(date);
        } catch (Exception e) {
            // handle exception
        }

        List<PersonRole> roles = new ArrayList<>();
        for (String roleName : roleNames) {
            PersonRole role = new PersonRole(roleName);
            roles.add(role);
        }
        person.setRoles(roles);

        return person;
    }

    public static Person[] init() {
        ArrayList<Person> persons = new ArrayList<>();
        persons.add(createPerson("Thomas Edison", "toby@gmail.com", "123toby", "pfp1", true, "01-01-1840", Arrays.asList("ROLE_ADMIN", "ROLE_USER", "ROLE_TESTER")));
        persons.add(createPerson("Alexander Graham Bell", "lexb@gmail.com", "123lex", "pfp2", true, "01-01-1847", Arrays.asList("ROLE_USER")));
        persons.add(createPerson("Nikola Tesla", "niko@gmail.com", "123niko", "pfp3", true, "01-01-1850", Arrays.asList("ROLE_USER")));
        persons.add(createPerson("Madam Curie", "madam@gmail.com", "123madam", "pfp4", true, "01-01-1860", Arrays.asList("ROLE_USER")));
        persons.add(createPerson("Grace Hopper", "hop@gmail.com", "123hop", "pfp5", true, "12-09-1906", Arrays.asList("ROLE_USER")));
        persons.add(createPerson("John Mortensen", "jm1021@gmail.com", "123Qwerty!", "pfp6", true, "10-21-1959", Arrays.asList("ROLE_ADMIN")));
        return persons.toArray(new Person[0]);
    }

    public static void main(String[] args) {
        Person[] persons = init();
        for (Person person : persons) {
            System.out.println(person);
        }
    }
}
