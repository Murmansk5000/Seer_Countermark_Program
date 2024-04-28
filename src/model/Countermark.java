package model;

import javax.swing.*;
import java.io.File;
import java.util.EnumMap;

public class Countermark {
    private final int id; // 序号
    private final int angle; // 角数
    private final String series;
    private final String name; // 名字
    private final EnumMap<Attribute, Integer> attributes;
    private final int sumAll; // 总和
    private ImageIcon image; // 图片
    private int sumSelect; // 选项总和

    public Countermark(int id, int angle, String name, int physicalAttack, int specialAttack,
                       int defence, int specialDefence, int speed, int healthPoints, String series,
                       String imagePath) {
        this.id = id;
        this.name = name;
        this.attributes = new EnumMap<>(Attribute.class);
        attributes.put(Attribute.PHYSICAL_ATTACK, physicalAttack);
        attributes.put(Attribute.SPECIAL_ATTACK, specialAttack);
        attributes.put(Attribute.DEFENCE, defence);
        attributes.put(Attribute.SPECIAL_DEFENCE, specialDefence);
        attributes.put(Attribute.SPEED, speed);
        attributes.put(Attribute.HEALTH_POINTS, healthPoints);

        this.series = series;
        this.angle = calculateAngle();
        this.sumAll = calculateSumAll();
        this.sumSelect = 0; // Default value, can be set later based on selected attributes
        // 加载图片
        this.loadImageIcon(imagePath);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public int calculateAngle() {
        int angle = 0;
        if(this.getPhysicalAttack()>0) angle++;
        if(this.getSpecialAttack()>0) angle++;
        if(this.getDefence()>0) angle++;
        if(this.getSpecialDefence()>0) angle++;
        if(this.getSpeed()>0) angle++;
        if(this.getHealthPoints()>0) angle++;
        return angle;
    }

    public int getPhysicalAttack() {
        return attributes.getOrDefault(Attribute.PHYSICAL_ATTACK, 0);
    }

    public int getSpecialAttack() {
        return attributes.getOrDefault(Attribute.SPECIAL_ATTACK, 0);
    }


    public int getDefence() {
        return attributes.getOrDefault(Attribute.DEFENCE, 0);
    }


    public int getSpecialDefence() {
        return attributes.getOrDefault(Attribute.SPECIAL_DEFENCE, 0);
    }

    public int getBothDefence() {
        return getDefence() + getSpecialDefence();
    }

    public int getSpeed() {
        return attributes.getOrDefault(Attribute.SPEED, 0);
    }

    public int getHealthPoints() {
        return attributes.getOrDefault(Attribute.HEALTH_POINTS, 0);
    }

    public int getSumAll() {
        return sumAll;
    }

    public int getSumSelect() {
        return sumSelect;
    }

    public String getSeries() {
        return series;
    }

    public void setSumSelect(int sumSelect) {
        this.sumSelect = sumSelect;
    }

    // Calculate the sum of all attributes
    private int calculateSumAll() {
        int total = 0;
        for (int value : attributes.values()) {
            total += value;
        }
        return total;
    }


}
