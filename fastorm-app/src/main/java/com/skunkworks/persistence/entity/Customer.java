package com.skunkworks.persistence.entity;

import com.skunkworks.fastorm.annotations.GenerateRepository;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * stole on 21.01.17.
 */
@Data
@Entity
@GenerateRepository
@EqualsAndHashCode(of = "id")
public class Customer {

    @Id
    private Long id;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "lastname")
    private String lastName;
}
