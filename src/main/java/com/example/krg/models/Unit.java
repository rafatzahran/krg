package com.example.krg.models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(	name = "unit", uniqueConstraints = {@UniqueConstraint(columnNames = {"version", "name"})})
public class Unit {
    @TableGenerator(name = "id_generator", table = "unit_id_gen", pkColumnName = "gen_name", valueColumnName = "gen_value",
            pkColumnValue="task_gen", initialValue=10, allocationSize=10)
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "id_generator")
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "version", columnDefinition = "integer DEFAULT 1", nullable = false)
    private Long version;

    @Enumerated(EnumType.STRING)
    @Column(length = 191)
    private EUnit name;

    @OneToMany(targetEntity = UserRole.class,
            mappedBy = "unitId",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<UserRole> userRoleList = new ArrayList<>();

    public Unit() {

    }

    public Unit(EUnit name, Long version) {
        this.name = name;
        this.version = version;
    }

    public Unit(Long id, EUnit name, Long version) {
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

    public EUnit getName() {
        return name;
    }

    public void setName(EUnit name) {
        this.name = name;
    }
}