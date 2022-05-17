package com.example.krg.models;

public class UserDTO {

    private Long id;

    private Long version;

    private String name;

    public UserDTO() {

    }

    public UserDTO(Long id, String name, Long version) {
        this.id = id;
        this.name = name;
        this.version = version;
    }

    public UserDTO buildFromUserEntity(final User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.version = user.getVersion();
        return this;
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