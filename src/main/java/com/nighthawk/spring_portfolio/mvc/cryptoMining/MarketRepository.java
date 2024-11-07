package com.nighthawk.spring_portfolio.mvc.cryptoMining;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketRepository extends JpaRepository<Market, Long> {
    Market findBySymbol(String symbol);
}