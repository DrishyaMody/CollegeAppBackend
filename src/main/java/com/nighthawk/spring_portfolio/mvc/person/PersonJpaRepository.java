package com.nighthawk.spring_portfolio.mvc.person;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

/**
 * The PersonJpaRepository interface is automatically implemented by Spring Data JPA at runtime.
 * It uses the Java Persistence API (JPA) - specifically, Hibernate - to map, store, update, and retrieve data from relational databases.
 * 
 * This interface extends JpaRepository, which provides CRUD (Create, Read, Update, Delete) operations.
 * Through the JPA, the developer can store and retrieve Java objects to and from the database.
 * 
 * JpaRepository is a generic interface and requires two parameters:
 * 1. The entity type to be persisted (in this case, Person).
 * 2. The type of the entity's primary key (in this case, Long).
 */
public interface PersonJpaRepository extends JpaRepository<Person, Long> {

    /**
     * Finds a person by their email address.
     * Spring Data JPA will automatically generate a query based on the method name.
     * @param email the email address to search for.
     * @return an Optional containing the person with the specified email if found, or empty if not.
     */
    Optional<Person> findByEmail(String email);

    /**
     * Finds all persons ordered by name in ascending order.
     * @return a list of all persons sorted by name.
     */
    List<Person> findAllByOrderByNameAsc();

    /**
     * Finds persons whose name or email contains the specified search term, ignoring case.
     * Spring Data JPA will automatically generate a query based on the method name.
     * @param name the name to search for.
     * @param email the email to search for.
     * @return a list of persons matching the name or email criteria.
     */
    List<Person> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);

    /**
     * Finds a person by their email and password.
     * This method is typically used for authentication purposes.
     * @param email the email address to search for.
     * @param password the password to search for.
     * @return the person with the specified email and password.
     */
    Person findByEmailAndPassword(String email, String password);

    /**
     * Custom JPA query using the @Query annotation.
     * This allows for more complex queries that can't be expressed through the method name.
     * The query will find all Person entities where the name or email contains the given term.
     * The 'nativeQuery = true' parameter indicates that the query is a native SQL query, not a JPQL query.
     * @param term the search term to look for in name or email.
     * @return a list of persons whose name or email contains the specified term.
     */
    @Query(
            value = "SELECT * FROM Person p WHERE p.name LIKE ?1 or p.email LIKE ?1",
            nativeQuery = true)
    List<Person> findByLikeTermNative(String term);

    /**
     * Updates the balance of a person by their email.
     * This query uses a modifying operation to directly update the balance of the specified person.
     * @param email the email of the person whose balance will be updated.
     * @param amount the amount to add to (or subtract from) the current balance.
     */
    @Modifying
    @Transactional
    @Query("UPDATE Person p SET p.balance = p.balance + :amount WHERE p.email = :email")
    void updateBalanceByEmail(@Param("email") String email, @Param("amount") Double amount);
}
