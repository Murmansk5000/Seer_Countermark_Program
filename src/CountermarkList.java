import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.swing.*;

public class CountermarkList {
    private ArrayList<Countermark> countermarks;
    public CountermarkList(){
        this.countermarks = new ArrayList<>();
    }

    public void loadDataFromFile(String fileName, Component parentComponent) {
        // 清空现有数据
        countermarks.clear();

        try {
            List<String> lines = Files.readAllLines(Paths.get(fileName));
            for (String line : lines) {
                String[] data = line.split(","); // 假设您的文件是逗号分隔的
                if (data.length == 10) { // 替换为您期望的字段数量
                    int id = Integer.parseInt(data[0]);
                    String name = data[1];
                    String idPath = "img/" + id + ".png";
                    String namePath = "img/" + name + ".png";
                    String imagePath = new File(idPath).exists() ? idPath : (new File(namePath).exists() ? namePath : null);

                    Countermark cm = new Countermark(
                            Integer.parseInt(data[0]), // id
                            Integer.parseInt(data[1]), // 角数
                            data[2], // 名称

                            Integer.parseInt(data[3]), // 物攻
                            Integer.parseInt(data[4]), // 特攻
                            Integer.parseInt(data[5]), // 防御
                            Integer.parseInt(data[6]), // 特防
                            Integer.parseInt(data[7]), // 速度
                            Integer.parseInt(data[8]), // 体力
                            imagePath);
                    countermarks.add(cm);
                } else {
                    System.out.println("Skipping malformed line: " + line);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parentComponent, "无法加载刻印数据文件: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parentComponent, "加载数据时发生未知错误: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void sortCountermarksBySumSelect() {
        Collections.sort(countermarks, new Comparator<Countermark>() {
            @Override
            public int compare(Countermark cm1, Countermark cm2) {
                // 首先根据getSumSelect降序排序
                int compareResult = Integer.compare(cm2.getSumSelect(), cm1.getSumSelect());

                // 如果getSumSelect相等，则根据getSumAll降序排序
                if (compareResult == 0) {
                    compareResult = Integer.compare(cm2.getSumAll(), cm1.getSumAll());
                }

                return compareResult;
            }
        });
    }


    public int size(){
        return countermarks.size();
    }

    public Countermark get(int i){
        return this.countermarks.get(i);
    }

    public List<Countermark> getCountermarks() {
        return countermarks;
    }

    public void calculateSumSelect(Map<String, JCheckBox> attributeCheckBoxes) {
        for (Countermark cm : countermarks) {
            int sumSelect = 0;
            if (attributeCheckBoxes.get("physicalAttack").isSelected()) {
                sumSelect += cm.getPhysicalAttack();
            }
            if (attributeCheckBoxes.get("specialAttack").isSelected()) {
                sumSelect += cm.getSpecialAttack();
            }
            if (attributeCheckBoxes.get("defence").isSelected()) {
                sumSelect += cm.getDefence();
            }
            if (attributeCheckBoxes.get("specialDefence").isSelected()) {
                sumSelect += cm.getSpecialDefence();
            }
            if (attributeCheckBoxes.get("speed").isSelected()) {
                sumSelect += cm.getSpeed();
            }
            if (attributeCheckBoxes.get("healthPoints").isSelected()) {
                sumSelect += cm.getHealthPoints();
            }

            cm.setSumSelect(sumSelect); // 更新Countermark对象的sumSelect属性
        }
    }
}
