package com.example.krg.models;

import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(	name = "userRole")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name="userId", nullable=false)
    private Long userId;

    @ManyToOne(targetEntity = Unit.class, fetch = FetchType.LAZY)
    @MapsId("unitId")
    @JoinColumn(name="unitId", nullable=false)
    private Long unitId;

    @ManyToOne(targetEntity = Role.class, fetch = FetchType.LAZY)
    @MapsId("roleId")
    @JoinColumn(name="roleId", nullable=false)
    private Long roleId;

    @CreatedDate
    @Column(name = "validFrom", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime validFrom;

    @Column(name = "validTo", nullable = true)
    private LocalDateTime validTo;

    public UserRole() {
    }

    public UserRole(Long userId, Long unitId, Long roleId, LocalDateTime validFrom, LocalDateTime validTo) {
        this.userId = userId;
        this.unitId = unitId;
        this.roleId = roleId;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    public UserRole(Long id, Long userId, Long unitId, Long roleId, LocalDateTime validFrom, LocalDateTime validTo) {
        this.id = id;
        this.userId = userId;
        this.unitId = unitId;
        this.roleId = roleId;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }


}
