package com.nighthawk.spring_portfolio.mvc.crypto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CryptoJPArepo extends JpaRepository<Crypto, Long> {
    Crypto findByName(String name); // Optional custom query to find Crypto by name
}