package model;

public enum Attribute {
    PHYSICAL_ATTACK("Physical Attack"),
    SPECIAL_ATTACK("Special Attack"),
    DEFENCE("Defence"),
    SPECIAL_DEFENCE("Special Defence"),
    SPEED("Speed"),
    HEALTH_POINTS("Health Points");

    private final String displayName;

    Attribute(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
