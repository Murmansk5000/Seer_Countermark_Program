package view;

import model.Countermark;
import model.CountermarkList;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class AnglePanel extends JPanel {
    private Map<String, JCheckBox> angleCheckBoxes = new HashMap<>();
    private JButton selectAngleButton;

    public AnglePanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        selectAngleButton = new JButton("全选");
        add(selectAngleButton);
        addAngleBox("5角", "5", true);
        addAngleBox("4角", "4", true);
        addAngleBox("3角", "3", true);
        addAngleBox("2角", "2", true);
    }

    public void addAngleBox(String label, String key, boolean isSelected) {
        JCheckBox checkBox = new JCheckBox(label, isSelected);
        angleCheckBoxes.put(key, checkBox);
        add(checkBox);
    }

    public void selectAngleAll(ActionEvent event) {

        // 选中所有角数复选框
        for (JCheckBox checkBox : angleCheckBoxes.values()) {
            checkBox.setSelected(true);
        }
    }

}

class AttributePanel extends JPanel {

    private Map<String, JCheckBox> attributeCheckBoxes = new HashMap<>();
    private JButton selectAttributeButton;

    public AttributePanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        selectAttributeButton = new JButton("全选");
        // 事件监听可以在外部设置，以便更灵活的控制
        add(selectAttributeButton);
        // 假设 addAttributeBox 方法从外部提供，或者在这里实现
        addAttributeBox("攻击", "physicalAttack", true);
        addAttributeBox("特攻", "specialAttack", true);
        addAttributeBox("防御", "defence", true);
        addAttributeBox("特防", "specialDefence", true);
        addAttributeBox("速度", "speed", true);
        addAttributeBox("体力", "healthPoints", true);
    }

    public void addAttributeBox(String label, String attributeKey, boolean isSelected) {
        JCheckBox checkBox = new JCheckBox(label, isSelected);
        attributeCheckBoxes.put(attributeKey, checkBox);
        add(checkBox);
    }

    // 全选按钮的事件处理方法
    public void selectCheckAll(ActionEvent event) {
        // 选中所有属性复选框
        for (JCheckBox checkBox : attributeCheckBoxes.values()) {
            checkBox.setSelected(true);
        }
    }

}

class ConfirmPanel extends JPanel {
    private JButton confirmButton = new JButton("确认");
    private JCheckBox showImagesCheckBox = new JCheckBox("显示图片");

    public ConfirmPanel() {
        add(confirmButton);
        add(showImagesCheckBox);
    }
}

class SearchPanel extends JPanel {
    private JTextField searchField = new JTextField(20);
    private JButton searchButton = new JButton("搜索");

    public SearchPanel() {
        add(new JLabel("搜索刻印:"));
        add(searchField);
        add(searchButton);
    }


    public String getTxt() {
        return searchField.getText().trim();
    }
}

public class CountermarkSelectionGUI extends JFrame {
    private final int height = 30;
    private final int heightWithPic = 2 * height;
    private final int width = 40;
    private Map<String, JCheckBox> attributeCheckBoxes;
    private Map<String, JCheckBox> angleCheckBoxes;
    private JCheckBox showImagesCheckBox;
    private JButton selectAttributeButton; // 新增的全选按钮
    private JButton selectAngleButton; // 新增的全选按钮
    private JButton confirmButton;
    private CountermarkList countermarkList;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchButton;
    private int lastSearchIndex = -1; // 初始化为-1，表示开始时没有搜索过
    private Map<String, ImageIcon> imageCache = new HashMap<>();


    public CountermarkSelectionGUI() {
        countermarkList = new CountermarkList();
        countermarkList.loadDataFromFile("countermark.txt", this); // 确保文件路径正确
        setTitle("刻印自定义排序——By Murmansk      特别鸣谢：火火，感谢提供资源文件");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); // 修改布局为BorderLayout

        // 初始化复选框面板
        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setLayout(new BorderLayout());

        attributeCheckBoxes = new HashMap<>();
        selectAttributeButton = new JButton("全选");
        selectAttributeButton.addActionListener(this::selectCheckAll);
        JPanel attributePanel = new JPanel();
        attributePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        attributePanel.add(selectAttributeButton);
        addAttributeBox(attributePanel, "攻击", "physicalAttack", true);
        addAttributeBox(attributePanel, "特攻", "specialAttack", true);
        addAttributeBox(attributePanel, "防御", "defence", true);
        addAttributeBox(attributePanel, "特防", "specialDefence", true);
        addAttributeBox(attributePanel, "速度", "speed", true);
        addAttributeBox(attributePanel, "体力", "healthPoints", true);

        angleCheckBoxes = new HashMap<>();
        selectAngleButton = new JButton("全选");
        selectAngleButton.addActionListener(this::selectAngleAll);
        JPanel anglePanel = new JPanel();
        anglePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        anglePanel.add(selectAngleButton);
        addAngleBox(anglePanel, "5角", "5", true);
        addAngleBox(anglePanel, "4角", "4", true);
        addAngleBox(anglePanel, "3角", "3", true);
        addAngleBox(anglePanel, "2角", "2", true);

        // 初始化搜索框和按钮
        searchField = new JTextField(20); // 设置搜索框宽度
        searchButton = new JButton("搜索");
        searchButton.addActionListener(this::onSearch);

        // 创建搜索面板并添加搜索框和按钮
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("搜索刻印:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // 创建“显示图片”的复选框，并添加到复选框面板
        showImagesCheckBox = new JCheckBox("显示图片");

        // 将属性复选框面板添加到主复选框面板
        checkBoxPanel.add(attributePanel, BorderLayout.NORTH);

        checkBoxPanel.add(anglePanel, BorderLayout.CENTER);
        // 将“显示图片”的面板添加到主复选框面板
        checkBoxPanel.add(showImagesCheckBox, BorderLayout.SOUTH);
        checkBoxPanel.add(searchPanel, BorderLayout.EAST);

        // 将主复选框面板添加到窗体的北部
        add(checkBoxPanel, BorderLayout.NORTH);


        // 初始化表格和确认按钮
        initializeTable();
        fillTableWithData(); // 确保这一行在initializeTable方法调用之后

        confirmButton = new JButton("确认");
        confirmButton.addActionListener(this::onConfirm);
        add(confirmButton, BorderLayout.SOUTH); // 将确认按钮放置在窗口底部

        pack(); // 调整窗口以适应组件大小
        setSize(800, 600);
        setLocationRelativeTo(null); // 设置窗口居中显示


        setVisible(true); // 确保窗口可见
    }

    // 全选按钮的事件处理方法
    private void selectCheckAll(ActionEvent event) {
        // 选中所有属性复选框
        for (JCheckBox checkBox : attributeCheckBoxes.values()) {
            checkBox.setSelected(true);
        }
    }

    private void selectAngleAll(ActionEvent event) {

        // 选中所有角数复选框
        for (JCheckBox checkBox : angleCheckBoxes.values()) {
            checkBox.setSelected(true);
        }
    }


    private void addAttributeBox(JPanel panel, String label, String attributeKey, boolean isSelected) {
        JCheckBox checkBox = new JCheckBox(label, isSelected);
        attributeCheckBoxes.put(attributeKey, checkBox);
        panel.add(checkBox);
    }

    private void addAngleBox(JPanel panel, String label, String key, boolean isSelected) {
        JCheckBox checkBox = new JCheckBox(label, isSelected);
        angleCheckBoxes.put(key, checkBox);
        panel.add(checkBox);
    }


    private void onConfirm(ActionEvent event) {
        lastSearchIndex = -1;
        System.out.println("Confirm button clicked!"); // 打印语句，确认方法被调用
        // 根据用户选择计算sumSelect
        countermarkList.calculateSumSelect(attributeCheckBoxes);
        // 调用排序方法
        countermarkList.sortCountermarksBySumSelect();

        // 将计算和排序后的数据填充到表格中
        fillTableWithData();

        // 可选：如果需要在每次点击确认后都按照特定列排序
        // 假设你已经有一个名为table的JTable实例，以及一个相应的tableModel
        TableRowSorter sorter = (TableRowSorter) table.getRowSorter();
        sorter.setSortKeys(Collections.unmodifiableList(Arrays.asList(
                new RowSorter.SortKey(tableModel.getColumnCount() - 1, SortOrder.DESCENDING))));
        sorter.sort();

        // 刷新表格显示
        table.revalidate();
        table.repaint();
    }

    private void onSearch(ActionEvent event) {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            return; // 如果搜索框为空，则不执行任何操作
        }

        // 从上次搜索位置的下一行开始搜索
        int startIndex = lastSearchIndex + 1;
        boolean wrapped = false; // 用于标记是否已经回到起点重新搜索

        for (int i = startIndex; i < table.getRowCount(); i++) {
            String name = table.getValueAt(i, 2).toString();
            if (name.contains(searchText)) {
                // 选中匹配的行
                table.setRowSelectionInterval(i, i);
                // 将匹配的行滚动到视图中
                table.scrollRectToVisible(new Rectangle(table.getCellRect(i, 0, true)));
                // 更新最后搜索位置
                lastSearchIndex = i;
                return; // 结束搜索
            }

            // 如果搜索到了末尾，回到起点
            if (i == table.getRowCount() - 1 && !wrapped) {
                i = -1; // 从-1开始，因为循环会增加1
                wrapped = true; // 标记已经回到起点
            }
        }

        // 如果没有找到匹配项，重置lastSearchIndex并通知用户
        if (wrapped) {
            JOptionPane.showMessageDialog(this, "已到达列表末尾，未找到匹配项。搜索将从头开始。");
            lastSearchIndex = -1;
        }
    }

    private void fillTableWithData() {
        tableModel.setRowCount(0); // 清空表格数据
        boolean ifLoadImage = showImagesCheckBox.isSelected();

        // 遍历所有Countermark对象，根据勾选的角数筛选数据
        for (Countermark cm : countermarkList.getCountermarks()) {
            // 如果没有复选框被勾选，跳过当前迭代
            if (angleCheckBoxes.isEmpty() || angleCheckBoxes.values().stream().noneMatch(JCheckBox::isSelected)) {
                continue;
            }

            // 检查Countermark对象的角数是否与任何被勾选的复选框相匹配
            boolean angleMatched = angleCheckBoxes.entrySet().stream()
                    .filter(e -> e.getValue().isSelected()) // 筛选出被勾选的复选框
                    .mapToInt(e -> Integer.parseInt(e.getKey())) // 将复选框的key（字符串）转换为整数
                    .anyMatch(angle -> angle == cm.getAngle()); // 检查是否与Countermark的角数相匹配

            // 若角数匹配，则将数据添加到表格中
            if (angleMatched) {
                ImageIcon icon = ifLoadImage ? getImageFromCache(cm) : null;
                tableModel.addRow(new Object[]{
                        cm.getId(),
                        cm.getAngle(),
                        cm.getName(),
                        icon,
                        cm.getPhysicalAttack(),
                        cm.getSpecialAttack(),
                        cm.getDefence(),
                        cm.getSpecialDefence(),
                        cm.getSpeed(),
                        cm.getHealthPoints(),
                        cm.getSumAll(),
                        cm.getSumSelect()
                });
            }
        }
        adjustRowHeight(ifLoadImage);
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
            return resizeIcon(icon, this.heightWithPic - 10); // 假设有resizeIcon方法调整大小
        }
        return null; // 图片加载失败
    }

    private void initializeTable() {
        int width = this.width;
        tableModel = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) {
                    return String.class;
                } else if (columnIndex == 3) {
                    return ImageIcon.class;
                }
                return Integer.class;
            }
        };

        // 创建表格模型并设置给JTable
        table = new JTable(tableModel);
        table.setRowHeight(60);


        // 设置表头
        String[] columnNames = {"ID", "角数", "名称", "图片", "攻击", "特攻", "防御", "特防", "速度", "体力", "总和", "选项总和"};
        tableModel.setColumnIdentifiers(columnNames);
        table.getColumnModel().getColumn(0).setPreferredWidth(width); // ID列
        table.getColumnModel().getColumn(1).setPreferredWidth(width); // 角数列
        table.getColumnModel().getColumn(2).setPreferredWidth(4 * width); // name
        table.getColumnModel().getColumn(3).setPreferredWidth(3 * width); // 图片
        table.getColumnModel().getColumn(4).setPreferredWidth(width); // 攻击
        table.getColumnModel().getColumn(5).setPreferredWidth(width); // 特攻
        table.getColumnModel().getColumn(6).setPreferredWidth(width); // 防御
        table.getColumnModel().getColumn(7).setPreferredWidth(width); // 特防
        table.getColumnModel().getColumn(8).setPreferredWidth(width); // 速度
        table.getColumnModel().getColumn(9).setPreferredWidth(width); // 体力
        table.getColumnModel().getColumn(10).setPreferredWidth(width); // 总和
        table.getColumnModel().getColumn(11).setPreferredWidth(3 * width);

        table.getColumnModel().getColumn(3).setCellRenderer(new ImageRenderer());

        // 应用排序器到JTable
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // 默认按照ID升序排序
        sorter.setSortKeys(Collections.unmodifiableList(Arrays.asList(
                new RowSorter.SortKey(0, SortOrder.ASCENDING))));
        sorter.sort();


        // 自定义单元格渲染器，用于文本居中和调整字体大小
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER); // 文字居中
        centerRenderer.setFont(new Font("SansSerif", Font.BOLD, 36)); // 设置字体为36号


        // 应用这个渲染器到所有文本列
        int columnCount = tableModel.getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            if (i != 3) { // 假设第4列是图片列，跳过这一列
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        // 设置图片列的渲染器，如果需要
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
            table.setRowHeight(this.height); // 示例值，适应没有图片时的高度
        } else {
            // 当显示图片时，可能需要一个更大的行高来适应图片
            // 这个值应该与resizeIcon方法中设置的图片高度相匹配
            table.setRowHeight(this.heightWithPic);
        }
    }
}