package com.nighthawk.spring_portfolio.mvc.cryptoMining;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private double btcBalance = 0.0;

    @Column(nullable = false)
    private double usdBalance = 1000.0;

    @ManyToOne
    @JoinColumn(name = "current_gpu_id")
    private GPU currentGpu;

    private boolean miningActive = false;
    private double totalMined = 0.0;
    private int totalShares = 0;
    private String currentPool = "NiceHash";

    // Constructor for creating a User with username, password, and email
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // Static method to create a default user
    public static User createDefaultUser() {
        return new User("defaultUser", "hashedPassword", "default@example.com");
    }
}