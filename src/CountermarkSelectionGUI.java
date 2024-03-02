import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.SortOrder;



public class CountermarkSelectionGUI extends JFrame {
    private Map<String, JCheckBox> attributeCheckBoxes;
    private Map<String, JCheckBox> angleCheckBoxes;

    private JCheckBox showImagesCheckBox;
    private JButton selectAttributeButton; // 新增的全选按钮
    private JButton selectAngleButton; // 新增的全选按钮
    private JButton confirmButton;
    private CountermarkList countermarkList;
    private JTable table;
    private DefaultTableModel tableModel;
    private int height = 60;

    private Map<String, ImageIcon> imageCache = new HashMap<>();



    public CountermarkSelectionGUI() {
        countermarkList = new CountermarkList();
        countermarkList.loadDataFromFile("countermark.txt",this); // 确保文件路径正确
        setTitle("刻印属性选择器——By Murmansk");
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
        addAttributeBox(attributePanel, "攻击", "physicalAttack",true);
        addAttributeBox(attributePanel, "特攻", "specialAttack",true);
        addAttributeBox(attributePanel, "防御", "defence",true);
        addAttributeBox(attributePanel, "特防", "specialDefence",true);
        addAttributeBox(attributePanel, "速度", "speed",true);
        addAttributeBox(attributePanel, "体力", "healthPoints",true);

        angleCheckBoxes = new HashMap<>();
        selectAngleButton = new JButton("全选");
        selectAngleButton.addActionListener(this::selectAngleAll);
        JPanel anglePanel = new JPanel();
        anglePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        anglePanel.add(selectAngleButton);
        addAngleBox(anglePanel, "5角", "5",true);
        addAngleBox(anglePanel, "4角", "4",true);
        addAngleBox(anglePanel, "3角", "3",true);
        addAngleBox(anglePanel, "2角", "2",true);

        // 创建“显示图片”的复选框，并添加到复选框面板
        showImagesCheckBox = new JCheckBox("显示图片");

        // 将属性复选框面板添加到主复选框面板
        checkBoxPanel.add(attributePanel,BorderLayout.NORTH);

        checkBoxPanel.add(anglePanel, BorderLayout.CENTER);
        // 将“显示图片”的面板添加到主复选框面板
        checkBoxPanel.add(showImagesCheckBox, BorderLayout.SOUTH);

        // 将主复选框面板添加到窗体的北部
        add(checkBoxPanel, BorderLayout.NORTH);

        // 初始化表格和确认按钮
        initializeTable();
        fillTableWithData(); // 确保这一行在initializeTable方法调用之后

        confirmButton = new JButton("确认");
        confirmButton.addActionListener(this::onConfirm);
        add(confirmButton, BorderLayout.SOUTH); // 将确认按钮放置在窗口底部

        pack(); // 调整窗口以适应组件大小
        setSize(800,600);
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
        JCheckBox checkBox = new JCheckBox(label,isSelected);
        attributeCheckBoxes.put(attributeKey, checkBox);
        panel.add(checkBox);
    }

    private void addAngleBox(JPanel panel, String label, String key, boolean isSelected) {
        JCheckBox checkBox = new JCheckBox(label, isSelected);
        angleCheckBoxes.put(key, checkBox);
        panel.add(checkBox);
    }


    private void onConfirm(ActionEvent event) {
        System.out.println("Confirm button clicked!"); // 打印语句，确认方法被调用
        // 根据用户选择计算sumSelect
        countermarkList.calculateSumSelect(attributeCheckBoxes);
        // 调用排序方法
        countermarkList.sortCountermarksBySumSelect();

        // 将计算和排序后的数据填充到表格中
        fillTableWithData();

        // 可选：如果需要在每次点击确认后都按照特定列排序
        TableRowSorter sorter = (TableRowSorter) table.getRowSorter();
        sorter.setSortKeys(List.of(new RowSorter.SortKey(tableModel.getColumnCount() - 1, SortOrder.DESCENDING)));
        sorter.sort();

        // 刷新表格显示
        table.revalidate();
        table.repaint();
    }


    private void fillTableWithData() {
        tableModel.setRowCount(0); // 清空表格数据

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
                ImageIcon icon = showImagesCheckBox.isSelected() ? getImageFromCache(cm) : null;
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
        int width = 60;
        tableModel = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) {
                    return ImageIcon.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };

        // 创建表格模型并设置给JTable
        table = new JTable(tableModel);
        table.setRowHeight(60);


        // 设置表头
        String[] columnNames = {"ID", "角数","名称","图片", "攻击", "特攻", "防御", "特防", "速度", "体力", "总和","选项总和"};
        tableModel.setColumnIdentifiers(columnNames);
        table.getColumnModel().getColumn(0).setPreferredWidth(width); // ID列
        table.getColumnModel().getColumn(1).setPreferredWidth(width); // 角数列
        table.getColumnModel().getColumn(2).setPreferredWidth(2*width); // name
        table.getColumnModel().getColumn(3).setPreferredWidth(2*width); // 图片
        table.getColumnModel().getColumn(4).setPreferredWidth(width); // 攻击
        table.getColumnModel().getColumn(5).setPreferredWidth(width); // 特攻
        table.getColumnModel().getColumn(6).setPreferredWidth(width); // 防御
        table.getColumnModel().getColumn(7).setPreferredWidth(width); // 特防
        table.getColumnModel().getColumn(8).setPreferredWidth(width); // 速度
        table.getColumnModel().getColumn(9).setPreferredWidth(width); // 体力
        table.getColumnModel().getColumn(10).setPreferredWidth(width); // 总和
        table.getColumnModel().getColumn(11).setPreferredWidth(2*width);

        table.getColumnModel().getColumn(3).setCellRenderer(new ImageRenderer());

        // 应用排序器到JTable
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // 默认按照ID升序排序
        sorter.setSortKeys(List.of(new RowSorter.SortKey(0, SortOrder.ASCENDING)));


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
            table.setRowHeight(30); // 示例值，适应没有图片时的高度
        } else {
            // 当显示图片时，可能需要一个更大的行高来适应图片
            // 这个值应该与resizeIcon方法中设置的图片高度相匹配
            table.setRowHeight(this.height);
        }
    }
}