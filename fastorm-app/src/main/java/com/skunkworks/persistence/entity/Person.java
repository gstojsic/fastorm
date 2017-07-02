package com.skunkworks.persistence.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * stole on 02.07.17.
 */
@Data
@Entity
@EqualsAndHashCode(of = "id")
public class Person {

    @Id
    private Long id;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "lastname")
    private String lastName;

    private int age;

    private boolean married;
}
