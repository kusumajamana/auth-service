package com.demo.authentication_service.dao.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "roles")

public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Enumerated(EnumType.STRING)
    private ERole name;
    
    public enum ERole {
        ROLE_USER,
        ROLE_ADMIN
    }

    public Role() {
    }

    public Role(Integer id, ERole name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ERole getName() {
        return name;
    }

    public void setName(ERole name) {
        this.name = name;
    }
}