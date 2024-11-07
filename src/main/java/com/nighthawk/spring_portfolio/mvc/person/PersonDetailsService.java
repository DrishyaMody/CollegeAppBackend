package com.nighthawk.spring_portfolio.mvc.person;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PersonDetailsService implements UserDetailsService {

    @Autowired
    private PersonJpaRepository personJpaRepository;

    @Autowired
    private PersonRoleJpaRepository personRoleJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /* loadUserByUsername Overrides and maps Person & Roles POJO into Spring Security */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Person> optionalPerson = personJpaRepository.findByEmail(email); // Use Optional to handle possible null values
        Person person = optionalPerson.orElseThrow(() -> 
            new UsernameNotFoundException("User not found with email: " + email)
        );

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        person.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        });

        return new User(person.getEmail(), person.getPassword(), authorities);
    }

    /* Person Section */

    public List<Person> listAll() {
        return personJpaRepository.findAllByOrderByNameAsc();
    }

    public List<Person> list(String name, String email) {
        return personJpaRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(name, email);
    }

    public List<Person> listLike(String term) {
        return personJpaRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(term, term);
    }

    public List<Person> listLikeNative(String term) {
        String likeTerm = String.format("%%%s%%", term);  // Like term wrapped in %
        return personJpaRepository.findByLikeTermNative(likeTerm);
    }

    public void save(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        personJpaRepository.save(person);
    }

    public Person get(long id) {
        return personJpaRepository.findById(id).orElse(null);
    }

    public Person getByEmail(String email) {
        return personJpaRepository.findByEmail(email).orElse(null);
    }

    public void delete(long id) {
        personJpaRepository.deleteById(id);
    }

    public void defaults(String password, String roleName) {
        for (Person person : listAll()) {
            if (person.getPassword() == null || person.getPassword().isEmpty() || person.getPassword().isBlank()) {
                person.setPassword(passwordEncoder.encode(password));
            }
            if (person.getRoles().isEmpty()) {
                PersonRole role = personRoleJpaRepository.findByName(roleName);
                if (role != null) {
                    person.getRoles().add(role);
                }
            }
        }
    }

    public List<PersonRole> listAllRoles() {
        return personRoleJpaRepository.findAll();
    }

    public PersonRole findRole(String roleName) {
        return personRoleJpaRepository.findByName(roleName);
    }

    public void addRoleToPerson(String email, String roleName) {
        Optional<Person> optionalPerson = personJpaRepository.findByEmail(email);
        if (optionalPerson.isPresent()) {
            Person person = optionalPerson.get();
            PersonRole role = personRoleJpaRepository.findByName(roleName);
            if (role != null && !person.getRoles().contains(role)) {
                person.getRoles().add(role);
            }
        }
    }
}
