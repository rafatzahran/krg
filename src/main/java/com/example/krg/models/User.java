package com.example.krg.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(	name = "user", uniqueConstraints = {@UniqueConstraint(columnNames = {"version", "name"})})
public class User {
    private final static Long defaultVersion = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "version", columnDefinition = "integer DEFAULT 1", nullable = false)
    private Long version;

    @Column(name = "name", length = 191,  nullable = false)
    private String name;

    @OneToMany(targetEntity = UserRole.class, mappedBy = "userId")
    private List<UserRole> userRoleList = new ArrayList<>();

    public User() {

    }

    public User(String name, final Long version) {
        this.name = name;
        this.version = version == null ? defaultVersion : version;
    }

    public User(Long id, String name, Long version) {
        this.id = id;
        this.name = name;
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}