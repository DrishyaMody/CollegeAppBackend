package com.nighthawk.spring_portfolio.mvc.person;


import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class personSections {
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private Long id;


   @Column(unique = true)
   private String name;


   @Column(unique = true)
   private String abbreviation;


   @Column(unique = true)
   private int year;


   public personSections(String name, String abbreviation, int year) {
       this.name = name;
       this.abbreviation = abbreviation;
       this.year = year;
   }

   public String getName () {
    return this.name;
   }
    // Method to return the current year
    public static int defaultYear() {
        return LocalDate.now().getYear(); // Returns the current year
    }

    public static personSections[] initializeSections() {
        return new personSections[] {
            new personSections("Computer Science A", "CSA", defaultYear()),
            new personSections("Computer Science Principles", "CSP", defaultYear()),
            new personSections("Engineering Robotics", "Robotics", defaultYear()),
            new personSections("Computer Science and Software Engineering", "CSSE", defaultYear())
        };
    }
}