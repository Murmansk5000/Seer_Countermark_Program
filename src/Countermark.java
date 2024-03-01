import javax.swing.*;
import java.io.File;

public class Countermark {
    private int id;
    private int angle;
    private String name;
    private ImageIcon image;
    private int physicalAttack;
    private int specialAttack;
    private int defence;
    private int specialDefence;
    private int speed;
    private int healthPoints;
    private int sumAll;
    private int sumSelect;

    public Countermark(int id, int angle, String name, int physicalAttack, int specialAttack,
                       int defence, int specialDefence, int speed, int healthPoints, String imagePath) {
        this.id = id;
        this.angle = angle;
        this.name = name;
        this.physicalAttack = physicalAttack;
        this.specialAttack = specialAttack;
        this.defence = defence;
        this.specialDefence = specialDefence;
        this.speed = speed;
        this.healthPoints = healthPoints;
        this.sumAll = calculateSumAll();
        this.sumSelect = 0; // Default value, can be set later based on selected attributes
        // 加载图片
        this.loadImageIcon(imagePath);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public ImageIcon getImage() {
        return image;
    }

    // loadImageIcon 方法尝试从路径加载 ImageIcon，如果路径为空或文件不存在，返回 null
    private ImageIcon loadImageIcon(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        // 检查文件是否存在
        File file = new File(imagePath);
        if (file.exists()) {
            return new ImageIcon(imagePath);
        } else {
            return null;
        }
    }
    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public int getPhysicalAttack() {
        return physicalAttack;
    }

    public void setPhysicalAttack(int physicalAttack) {
        this.physicalAttack = physicalAttack;
    }

    public int getSpecialAttack() {
        return specialAttack;
    }

    public void setSpecialAttack(int specialAttack) {
        this.specialAttack = specialAttack;
    }

    public int getDefence() {
        return defence;
    }

    public void setDefence(int defence) {
        this.defence = defence;
    }

    public int getSpecialDefence() {
        return specialDefence;
    }

    public void setSpecialDefence(int specialDefence) {
        this.specialDefence = specialDefence;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getHealthPoints() {
        return healthPoints;
    }

    public void setHealthPoints(int healthPoints) {
        this.healthPoints = healthPoints;
    }

    public int getSumAll() {
        return sumAll;
    }

    // 注意：没有为sumAll设置setter，因为它应该是其他属性值计算得出的

    public int getSumSelect() {
        return sumSelect;
    }

    public void setSumSelect(int sumSelect) {
        this.sumSelect = sumSelect;
    }

    // Calculate the sum of all attributes
    private int calculateSumAll() {
        return physicalAttack + specialAttack + defence + specialDefence + speed + healthPoints;
    }


}
