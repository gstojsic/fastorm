package com.skunkworks.persistence;

import com.skunkworks.fastorm.annotations.GenerateRepository;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * stole on 21.01.17.
 */
@Data
@Entity
@GenerateRepository
public class Customer {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "lastname")
    private String lastName;

//    @Column(name = "address")
//    private long address;
}
