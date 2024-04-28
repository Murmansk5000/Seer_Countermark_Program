package view;

import model.Angle;
import model.Attribute;
import model.Countermark;
import model.CountermarkList;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.*;


public class CountermarkSelectionGUI extends JFrame {
    // 界面相关的尺寸常量
    private final int height = 30;
    private final int heightWithPic = 2 * height;
    private final int width = 10;
    // 排序选项和组合框
    String[] sortOptions = {"选项总和", "总和", "攻击", "特攻", "防御", "特防", "速度", "体力"};
    private Map<String, JCheckBox> angleCheckBoxes;
    private JCheckBox showImagesCheckBox;
    int sortTime = 6;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JTextField filterTextField;
    JComboBox<String>[] sortCombos = new JComboBox[sortTime]; // 创建一个组合框数组
    // 表格的列数
    String[] columnNames = {"ID", "角数", //"系列",
            "名称", "图片",
            "攻击", "特攻", "防御", "特防", "速度", "体力",
            "总和", "选项总和"};
    int[] columnWidth = new int[columnNames.length];
    private JButton searchButton;
    private int lastSearchIndex = -1; // 初始化为-1，表示开始时没有搜索过
    private Map<String, ImageIcon> imageCache = new HashMap<>();
    // 复选框用于属性和角度选择
    private Map<String, JCheckBox> attributeCheckBoxes;
    // 模型和表格相关
    private CountermarkList countermarkList;

    /**
     * 构造方法，初始化GUI界面设置。
     * 调用此构造函数会启动GUI的初始化过程，包括加载数据和设置窗口属性。
     */
    public CountermarkSelectionGUI() {
        initializeGUI();
    }

    // 设置列宽的方法，使用switch语句优化列宽设置
    public void setWidth() {
        for (int i = 0; i < columnNames.length; i++) {
            switch (columnNames[i]) {
                case "系列", "名称", "图片" -> columnWidth[i] = 12; // 对于“系列”、“名称”和“图片”，宽度设为8
                case "总和", "选项总和" -> columnWidth[i] = 6; // 对于“总和”和“选项总和”，宽度设为4
                default -> columnWidth[i] = 4; // 其他情况，默认宽度设为3
            }
        }
    }

    /**
     * 初始化GUI界面的组件和布局。
     * 加载数据，配置窗口的关闭操作，设置布局，并添加各个面板。
     */
    private void initializeGUI() {
        countermarkList = new CountermarkList();
        countermarkList.loadDataFromFile("countermark.txt", this);
        setTitle("刻印自定义排序——By Murmansk 特别鸣谢：火火，感谢提供资源文件");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(createCheckBoxPanel(), BorderLayout.NORTH);
        initializeTable();
        fillTableWithData();
        add(createConfirmButton(), BorderLayout.SOUTH);
        configureWindow();
    }

    /**
     * 创建并返回包含所有属性和角度选择复选框的面板。
     * 左侧面板包含属性和角度选择，右侧面板包含图片显示选项和搜索功能。
     *
     * @return 完整配置的复选框面板。
     */
    private JPanel createCheckBoxPanel() {
        JPanel left = new JPanel(new BorderLayout());
        JPanel right = new JPanel(new BorderLayout());
        JPanel checkBox = new JPanel(new BorderLayout());

        checkBox.add(createAttributePanel(), BorderLayout.NORTH); // 属性面板
        left.add(createAnglePanel(), BorderLayout.NORTH); // 选角面板

        left.add(createShowImagesCheckBox(), BorderLayout.SOUTH); // 图片勾选框
        right.add(createSearchPanel(), BorderLayout.CENTER); // 搜索框

        right.add(createFilterPanel(), BorderLayout.NORTH); // 刻印名筛选

        checkBox.add(left, BorderLayout.WEST);
        checkBox.add(right, BorderLayout.EAST);
        checkBox.add(createSortPanel(), BorderLayout.SOUTH);
        return checkBox;
    }

    // 创建属性选择面板
    private JPanel createAttributePanel() {
        JPanel attributePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        attributeCheckBoxes = new HashMap<>();
        JButton selectAttributeButton = new JButton("全选");
        selectAttributeButton.addActionListener(this::selectCheckAll);
        attributePanel.add(selectAttributeButton);
        for (Attribute attribute : Attribute.values()) {
            addAttributeBox(attributePanel, attribute.getLabel(), attribute.getKey(), true);
        }
        return attributePanel;
    }

    // 创建角度选择面板
    private JPanel createAnglePanel() {
        JPanel anglePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        angleCheckBoxes = new HashMap<>();
        JButton selectAngleButton = new JButton("全选");
        selectAngleButton.addActionListener(this::selectAngleAll);
        anglePanel.add(selectAngleButton);
        for (Angle angle : Angle.values()) {
            addAngleBox(anglePanel, angle.getLabel(), angle.getKey(), true);
        }
        return anglePanel;
    }

    // 创建排序选择面板
    private JPanel createSortPanel() {
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));


        // 循环创建标签和组合框
        for (int i = 0; i < sortCombos.length; i++) {
            JLabel label = new JLabel("排序" + (i + 1) + ":");
            sortCombos[i] = new JComboBox<>(sortOptions);
            sortPanel.add(label);
            sortPanel.add(sortCombos[i]);
        }

        return sortPanel;
    }

    // 创建搜索面板
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel();
        searchField = new JTextField(20);
        JButton searchButton = new JButton("搜索");
        searchButton.addActionListener(this::onSearch);
        searchPanel.add(new JLabel("搜索刻印:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        return searchPanel;
    }

    // 创建显示图片的复选框
    private JCheckBox createShowImagesCheckBox() {
        showImagesCheckBox = new JCheckBox("显示图片");
        return showImagesCheckBox;
    }

    // 创建确认按钮
    private JButton createConfirmButton() {
        JButton confirmButton = new JButton("确认");
        confirmButton.addActionListener(this::onConfirm);
        return confirmButton;
    }

    /**
     * 创建并返回一个包含文本输入框的面板，用于输入过滤刻印的名称或系列。
     *
     * @return 配置好的过滤面板。
     */
    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel();  // 创建面板
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT));  // 设置布局

        JLabel filterLabel = new JLabel("筛选刻印:");  // 创建标签
        filterPanel.add(filterLabel);  // 将标签添加到面板
        filterTextField = new JTextField(20);
        filterPanel.add(filterTextField);  // 将文本框添加到面板

        return filterPanel;
    }

    // 配置窗口属性
    private void configureWindow() {
        pack();
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
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

    /**
     * 在给定面板上添加属性复选框。
     * 为每个属性创建一个复选框，并添加到面板上，同时更新属性复选框的映射。
     *
     * @param panel        需要添加复选框的面板。
     * @param label        复选框的标签文本。
     * @param attributeKey 属性的键，用于在映射中标识复选框。
     * @param isSelected   初始化时复选框是否被选中。
     */
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


    /**
     * 处理确认按钮点击事件。
     * 计算选择的属性的总和，对Countermark列表进行排序，并更新表格数据。
     *
     * @param event 发生的动作事件。
     */
    private void onConfirm(ActionEvent event) {

        List<RowSorter.SortKey> sortKeysList = new ArrayList<>();
        for (JComboBox<String> sortCombo : sortCombos) {
            sortKeysList.add(new RowSorter.SortKey(getIndex((String) sortCombo.getSelectedItem()), SortOrder.DESCENDING));

        }

        lastSearchIndex = -1;
//        System.out.println("Confirm button clicked!"); // 打印语句，确认方法被调用
        // 根据用户选择计算sumSelect
        countermarkList.calculateSumSelect(attributeCheckBoxes);
        // 调用排序方法
        countermarkList.sortCountermarksBySumSelect();

        // 将计算和排序后的数据填充到表格中
        fillTableWithData();


        TableRowSorter sorter = (TableRowSorter) table.getRowSorter();
        sorter.setSortKeys(sortKeysList);
        sorter.sort();

        // 刷新表格显示
        table.revalidate();
        table.repaint();
    }

    /**
     * 根据列名获取对应的列索引。
     *
     * @param select 需要查找索引的列名。
     * @return 如果找到相应的列，则返回其索引；如果未找到，则返回-1。
     */
    public int getIndex(String select) {
        int columnIndex = -1;
        for (int i = 0; i < columnNames.length; i++) {
            if (columnNames[i].equals(select)) {
                columnIndex = i;
                break;
            }
        }
        return columnIndex;
    }

    /**
     * 处理表格搜索功能。从上次搜索位置开始搜索输入的文本，如果到达表格末尾未找到，则从头开始并通知用户。
     *
     * @param event 触发搜索操作的动作事件。
     */
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

    /**
     * 根据选定的过滤条件从Countermark列表中获取数据并填充表格。
     * 如果选择显示图片，将检查缓存或根据需要加载图片。
     */
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

            String series = Optional.ofNullable(cm.getSeries()).orElse("");
            String name = Optional.ofNullable(cm.getName()).orElse("");

            boolean textMatched = name.toLowerCase().contains(filterTextField.getText()) ||
                    series.toLowerCase().contains(filterTextField.getText());


            // 若角数匹配，则将数据添加到表格中
            if (angleMatched  && textMatched) {
                ImageIcon icon = ifLoadImage ? getImageFromCache(cm) : null;
                Map<String, Object> rowData = new HashMap<>();
                rowData.put("ID", cm.getId());
                rowData.put("系列", cm.getSeries());
                rowData.put("角数", cm.getAngle());
                rowData.put("名称", cm.getName());
                rowData.put("图片", icon);
                rowData.put("攻击", cm.getPhysicalAttack());
                rowData.put("特攻", cm.getSpecialAttack());
                rowData.put("防御", cm.getDefence());
                rowData.put("特防", cm.getSpecialDefence());
                rowData.put("速度", cm.getSpeed());
                rowData.put("体力", cm.getHealthPoints());
                rowData.put("总和", cm.getSumAll());
                rowData.put("选项总和", cm.getSumSelect());
                addRowToTable(rowData);
            }
        }
        adjustRowHeight(ifLoadImage);
    }

    /**
     * 根据列名与各自值的映射关系，向表中添加一行。
     *
     * @param rowData 包含作为键的列名和作为值的相关数据的映射。
     */
    private void addRowToTable(Map<String, Object> rowData) {
        Vector<Object> rowVector = new Vector<>();
        for (String columnName : columnNames) {  // Ensure the data aligns with the column order
            rowVector.add(rowData.get(columnName));
        }
        tableModel.addRow(rowVector);
    }

    /**
     * 初始化表格，设置适当的列模型、排序机制和自定义渲染器。
     * 配置表格的数据模型、表头、列宽和文本及图片的自定义渲染器。
     */
    private void initializeTable() {
        int width = this.width;
        tableModel = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == getIndex("系列") || columnIndex == getIndex("名称")) {
                    return String.class;
                } else if (columnIndex == getIndex("图片")) {
                    return ImageIcon.class;
                }
                return Integer.class;
            }
        };

        // 创建表格模型并设置给JTable
        table = new JTable(tableModel);
        table.setRowHeight(60);


        // 设置表头
        tableModel.setColumnIdentifiers(columnNames);
        setWidth();
        for (int i = 0; i < columnNames.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(width * columnWidth[i]);
        }


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
            if (i == getIndex("图片")) { // 找到图片列并使用图片渲染
                table.getColumnModel().getColumn(i).setCellRenderer(new ImageRenderer());
            } else {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }


        // 将表格添加到ScrollPane，然后将其添加到窗体中
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER); // 确保表格在中央区域
    }

    private ImageIcon loadImage(String path) {
        ImageIcon icon = new ImageIcon(path);
        if (icon.getIconWidth() > 0) {
            return resizeIcon(icon, this.heightWithPic - 10); // 假设有resizeIcon方法调整大小
        }
        return null; // 图片加载失败
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