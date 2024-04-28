package model;

public enum Angle {
    FIVE("5角", "5"),
    FOUR("4角", "4"),
    THREE("3角", "3"),
    TWO("2角", "2");

    private final String label;
    private final String key;

    Angle(String label, String key) {
        this.label = label;
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public String getKey() {
        return key;
    }
}

