import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.table.DefaultTableModel;

public class CountermarkSelectionGUI extends JFrame {
    private Map<String, JCheckBox> attributeCheckBoxes;
    private JCheckBox showImagesCheckBox;
    private JButton confirmButton;
    private CountermarkList countermarkList;
    private JTable table;
    private DefaultTableModel tableModel;
    private int height = 60;

    private Map<String, ImageIcon> imageCache = new HashMap<>();

    public CountermarkSelectionGUI() {
        countermarkList = new CountermarkList();
        countermarkList.loadDataFromFile("countermark.txt"); // 确保文件路径正确
        setTitle("刻印属性选择器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); // 修改布局为BorderLayout

        // 初始化复选框面板
        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setLayout(new BorderLayout());

        attributeCheckBoxes = new HashMap<>();
        JPanel attributePanel = new JPanel();
        attributePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        addCheckBox(attributePanel, "攻击", "physicalAttack",true);
        addCheckBox(attributePanel, "特攻", "specialAttack",true);
        addCheckBox(attributePanel, "防御", "defence",true);
        addCheckBox(attributePanel, "特防", "specialDefence",true);
        addCheckBox(attributePanel, "速度", "speed",true);
        addCheckBox(attributePanel, "体力", "healthPoints",true);

        // 将属性复选框面板添加到主复选框面板
        checkBoxPanel.add(attributePanel,BorderLayout.NORTH);

        // 创建“显示图片”的复选框，并添加到复选框面板
        JPanel showImagesPanel = new JPanel();
        showImagesPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        showImagesCheckBox = new JCheckBox("显示图片");
        showImagesPanel.add(showImagesCheckBox);

        // 将“显示图片”的面板添加到主复选框面板
        checkBoxPanel.add(showImagesPanel, BorderLayout.CENTER);

        // 将主复选框面板添加到窗体的北部
        add(checkBoxPanel, BorderLayout.NORTH);

        // 初始化表格和确认按钮
        initializeTable();

        confirmButton = new JButton("确认");
        confirmButton.addActionListener(this::onConfirm);
        add(confirmButton, BorderLayout.SOUTH); // 将确认按钮放置在窗口底部

        pack(); // 调整窗口以适应组件大小
        setSize(800,600);
        setLocationRelativeTo(null); // 设置窗口居中显示

        setVisible(true); // 确保窗口可见
    }


    private void addCheckBox(JPanel panel, String label, String attributeKey,boolean isSelected) {
        JCheckBox checkBox = new JCheckBox(label,isSelected);
        attributeCheckBoxes.put(attributeKey, checkBox);
        panel.add(checkBox);
    }

    private void onConfirm(ActionEvent event) {
        // 根据用户选择计算sumSelect
        countermarkList.calculateSumSelect(attributeCheckBoxes);
        // 调用排序方法
        countermarkList.sortCountermarksBySumSelect();

        // 将计算和排序后的数据填充到表格中
        fillTableWithData();

        // 刷新表格显示
        table.revalidate();
        table.repaint();
    }

    private void fillTableWithData() {
        // 清空表格现有数据
        tableModel.setRowCount(0);

        // 根据是否选中“显示图片”来决定是否加载图片
        boolean loadImage = showImagesCheckBox.isSelected();

        // 添加数据到表格
        for (Countermark cm : countermarkList.getCountermarks()) {
            ImageIcon icon = null;
            if (showImagesCheckBox.isSelected()) {
                icon = getImageFromCache(cm);
            }
            Object[] rowData = {
                    cm.getId(),
                    cm.getAngle(),
                    cm.getName(),
                    icon, // 如果未选中“显示图片”，则为null
                    cm.getPhysicalAttack(),
                    cm.getSpecialAttack(),
                    cm.getDefence(),
                    cm.getSpecialDefence(),
                    cm.getSpeed(),
                    cm.getHealthPoints(),
                    cm.getSumAll(),
                    cm.getSumSelect()
            };
            tableModel.addRow(rowData);
        }

        // 如果不加载图片，可能需要调整行高以适应文本
        adjustRowHeight(showImagesCheckBox.isSelected());
    }

    private ImageIcon getImageFromCache(Countermark cm) {
        // 尝试使用name命名的图片路径
        String namePath = "img/" + cm.getName() + ".png";
        // 尝试使用id命名的图片路径
        String idPath = "img/" + cm.getId() + ".png";

        // 检查基于name的图片是否已在缓存中或尝试过且失败
        if (!imageCache.containsKey(namePath) && !imageCache.containsKey(idPath)) {
            // 如果基于name的图片不在缓存中，尝试加载
            ImageIcon nameIcon = loadImage(namePath);
            if (nameIcon != null) {
                imageCache.put(namePath, nameIcon);
                return nameIcon;
            }

            // 如果基于name的图片加载失败，尝试基于id的图片
            ImageIcon idIcon = loadImage(idPath);
            if (idIcon != null) {
                imageCache.put(idPath, idIcon);
                return idIcon;
            }

            // 如果两种方式都加载失败，可能希望记录这个情况，避免未来的重复加载尝试
            // 注意：这里用null作为占位符，表示已尝试加载但失败
            imageCache.put(namePath, null);
            imageCache.put(idPath, null);
        }

        // 返回缓存中的图片，如果之前加载失败，则为null
        ImageIcon cachedIcon = imageCache.getOrDefault(namePath, imageCache.get(idPath));
        return cachedIcon != null ? cachedIcon : new ImageIcon(); // 如果都是null，则返回空的ImageIcon
    }

    private ImageIcon loadImage(String path) {
        ImageIcon icon = new ImageIcon(path);
        if (icon.getIconWidth() > 0) {
            return resizeIcon(icon, this.height - 10); // 假设有resizeIcon方法调整大小
        }
        return null; // 图片加载失败
    }

    private void initializeTable() {
        int width = 50;
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        table.setRowHeight(this.height);
        tableModel = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) {
                    return ImageIcon.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };
        table = new JTable(tableModel);


        // 设置表头
        String[] columnNames = {"ID", "角数","名称","图片", "攻击", "特攻", "防御", "特防", "速度", "体力", "总和","选项总和"};
        tableModel.setColumnIdentifiers(columnNames);
        table.getColumnModel().getColumn(0).setPreferredWidth(width); // ID列
        table.getColumnModel().getColumn(1).setPreferredWidth(width); // 角数列
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // name
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // 图片
        table.getColumnModel().getColumn(4).setPreferredWidth(width); // 攻击
        table.getColumnModel().getColumn(5).setPreferredWidth(width); // 特攻
        table.getColumnModel().getColumn(6).setPreferredWidth(width); // 防御
        table.getColumnModel().getColumn(7).setPreferredWidth(width); // 特防
        table.getColumnModel().getColumn(8).setPreferredWidth(width); // 速度
        table.getColumnModel().getColumn(9).setPreferredWidth(width); // 体力
        table.getColumnModel().getColumn(10).setPreferredWidth(width); // 总和
        table.getColumnModel().getColumn(11).setPreferredWidth(width);

        table.getColumnModel().getColumn(3).setCellRenderer(new ImageRenderer());

        // 将表格添加到ScrollPane，然后将其添加到窗体中
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER); // 确保表格在中央区域
    }

    private ImageIcon resizeIcon(ImageIcon icon, int maxHeight) {
        int newHeight = maxHeight;
        int newWidth = (int) Math.round(icon.getIconWidth() * ((double) maxHeight / icon.getIconHeight()));

        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }
    private void adjustRowHeight(boolean showImages) {
        if (!showImages) {
            table.setRowHeight(20); // 示例值，适应没有图片时的高度
        } else {
            // 当显示图片时，可能需要一个更大的行高来适应图片
            // 这个值应该与resizeIcon方法中设置的图片高度相匹配
            table.setRowHeight(this.height); // 假设this.height是合适的图片高度加上一些边距
        }
    }



}
