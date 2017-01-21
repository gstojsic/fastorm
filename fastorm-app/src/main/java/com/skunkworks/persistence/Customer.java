package com.skunkworks.persistence;

import com.skunkworks.fastorm.GenerateRepository;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * stole on 21.01.17.
 */
@Data
@Entity
@GenerateRepository
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstName;

    private String lastName;
}
