package com.nighthawk.spring_portfolio.mvc.cryptoMining;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Data  // Generates getters, setters, and other methods (like toString, equals, and hashCode)
@NoArgsConstructor  // Generates a no-argument constructor
@AllArgsConstructor  // Generates a constructor with all arguments
@Entity  // Marks this class as a JPA entity
@Table(name = "users")  // Specifies the table name in the database
public class User {

    @Id  // Specifies the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-generate the ID
    private Long id;

    @Column(unique = true, nullable = false)  // Ensures the username is unique and cannot be null
    private String username;

    @Column(nullable = false)  // Ensures the password cannot be null
    private String password;  // Store hashed passwords

    @Column(nullable = false)  // Ensures the email cannot be null
    private String email;

    // Explicit constructor for creating a User with username, password, and email
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // Example method to create a default user
    public static User createDefaultUser() {
        return new User("defaultUser", "hashedPassword", "default@example.com");
    }
}
