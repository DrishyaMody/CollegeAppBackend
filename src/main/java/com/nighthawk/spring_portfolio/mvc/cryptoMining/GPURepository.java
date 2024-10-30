package com.nighthawk.spring_portfolio.mvc.cryptoMining;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GPURepository extends JpaRepository<GPU, Long> {
    List<GPU> findByAvailableTrue();
    List<GPU> findByCategory(String category);
}