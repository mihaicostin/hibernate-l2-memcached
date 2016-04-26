package com.integration.com.mc.hibernate.memcached.entities;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Person {

    @Id
    private Long id;

    @Column
    private String name;

    @Column
    private String phoneNr;

    public Person(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
