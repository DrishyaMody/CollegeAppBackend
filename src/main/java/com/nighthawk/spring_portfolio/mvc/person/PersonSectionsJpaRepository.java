package com.nighthawk.spring_portfolio.mvc.person;

import org.springframework.data.jpa.repository.JpaRepository;

public interface  PersonSectionsJpaRepository extends JpaRepository<personSections, Long> {
    personSections findByName(String name);
}