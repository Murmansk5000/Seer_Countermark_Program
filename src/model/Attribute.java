package model;

public enum Attribute {
    PHYSICAL_ATTACK("攻击", "physicalAttack"),
    SPECIAL_ATTACK("特攻", "specialAttack"),
    DEFENCE("防御", "defence"),
    SPECIAL_DEFENCE("特防", "specialDefence"),
    SPEED("速度", "speed"),
    HEALTH_POINTS("体力", "healthPoints"),
    SUM_ALL("总和","sumAll"),
    SUM_SELECT("选项总和","sumSelect");

    private final String label;
    private final String key;

    Attribute(String label, String key) {
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
