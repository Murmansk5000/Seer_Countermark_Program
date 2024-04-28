package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class ImageRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof ImageIcon) {
            // 创建一个标签，并将图标设置为标签的图标
            JLabel label = new JLabel();
            label.setIcon((ImageIcon) value);
            label.setHorizontalAlignment(JLabel.CENTER); // 居中显示
            return label;
        } else {
            // 如果值不是ImageIcon类型，回退到默认的渲染器
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
}

