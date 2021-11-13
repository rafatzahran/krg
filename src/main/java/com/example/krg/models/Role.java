package com.example.krg.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(	name = "role", uniqueConstraints = {@UniqueConstraint(columnNames = {"version", "name"})})
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "version", columnDefinition = "integer DEFAULT 1", nullable = false)
    private Long version;

    @Enumerated(EnumType.STRING)
    @Column(length = 191)
    private ERole name;

    @OneToMany(targetEntity = UserRole.class, mappedBy = "roleId")
    private List<UserRole> userRoleList = new ArrayList<>();
    public Role() {

    }

    public Role(ERole name, Long version) {
        this.name = name;
        this.version = version;
    }

    public Role(Long id, ERole name, Long version) {
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

    public ERole getName() {
        return name;
    }

    public void setName(ERole name) {
        this.name = name;
    }

    public List<UserRole> getUserRoleList() {
        return userRoleList;
    }

    public void addUserRole(UserRole userRole) {
        this.userRoleList.add(userRole);
    }

    public void removeUserRole(UserRole userRole) {
        this.userRoleList.remove(userRole);
    }
}