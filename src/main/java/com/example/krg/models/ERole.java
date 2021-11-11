package com.example.krg.models;

public enum ERole {
    USER_ADMINISTRATION(Integer.valueOf(1), "UserRole administration"),
    ENDOSCOPIST_ADMINISTRATION(Integer.valueOf(2), "Endoscopist administration"),
    REPORT_COLONOSCOPY_CAPACITY(Integer.valueOf(3), "Report colonoscopy capacity"),
    SEND_INVITAIONS(Integer.valueOf(4), "Send invitations"),
    VIEW_STATISTICS(Integer.valueOf(5), "View statistics");

    private Integer value;
    private String description;

    private ERole(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    public Integer getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}
