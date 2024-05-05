package model;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;

public class CountermarkList {
    private ArrayList<Countermark> countermarks;

    public CountermarkList() {
        this.countermarks = new ArrayList<>();
    }

    public void loadDataFromFile(String fileName, Component parentComponent) {
        // 清空现有数据
        countermarks.clear();

        try {
            List<String> lines = Files.readAllLines(Paths.get(fileName));
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] data = line.split(","); // 假设您的文件是逗号分隔的
                if (data.length == 11) { // 替换为您期望的字段数量
                    int id = Integer.parseInt(data[0]);
                    String name = data[1];
                    String idPath = "img/" + id + ".png";
                    String namePath = "img/" + name + ".png";
                    String imagePath = new File(idPath).exists() ? idPath : (new File(namePath).exists() ? namePath : null);


                    String series = null;
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
                            data[10], // 系列
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

    public void loadDataFromFile2(String fileName, Component parentComponent) {
        countermarks.clear(); // 清空现有数据列表

        try {
            List<String> lines = Files.readAllLines(Paths.get(fileName));
            if (lines.isEmpty()) return; // 如果文件为空，则直接返回

            // 处理文件头，建立列索引映射
            String[] headers = lines.get(0).split(",");
            Map<String, Integer> headerMap = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                headerMap.put(headers[i], i);
            }

            // 从第二行开始读取数据
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] data = line.split(",");
                if (data.length == headers.length) { // 确保数据行的列数与头部列数相同
                    try {
                        Countermark cm = new Countermark(
                                Integer.parseInt(data[headerMap.get("ID")]), // ID
                                Integer.parseInt(data[headerMap.get("角数")]), // 角数
                                data[headerMap.get("名称")], // 名称
                                Integer.parseInt(data[headerMap.get("攻击")]), // 物攻
                                Integer.parseInt(data[headerMap.get("特攻")]), // 特攻
                                Integer.parseInt(data[headerMap.get("防御")]), // 防御
                                Integer.parseInt(data[headerMap.get("特防")]), // 特防
                                Integer.parseInt(data[headerMap.get("速度")]), // 速度
                                Integer.parseInt(data[headerMap.get("体力")]), // 体力
                                data[headerMap.get("系列")], // 系列
                                getImagePath(data[headerMap.get("ID")], data[headerMap.get("名称")]) // 图片路径
                        );
                        countermarks.add(cm);
                    } catch (NumberFormatException e) {
                        System.out.println("Skipping malformed line: " + line);
                    }
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

    private String getImagePath(String id, String name) {
        String idPath = "img/" + id + ".png";
        String namePath = "img/" + name + ".png";
        if (new File(idPath).exists()) {
            return idPath;
        } else if (new File(namePath).exists()) {
            return namePath;
        }
        return null;
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


    public int size() {
        return countermarks.size();
    }

    public Countermark get(int i) {
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