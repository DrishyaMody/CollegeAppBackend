package com.nighthawk.spring_portfolio.mvc.cryptoMining;

import com.nighthawk.spring_portfolio.mvc.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MiningUserRepository extends JpaRepository<MiningUser, Long> {
    Optional<MiningUser> findByPerson(Person person);
    Optional<MiningUser> findByPerson_Email(String email);  // Add this line
    boolean existsByPerson(Person person);
}