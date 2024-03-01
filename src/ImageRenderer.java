import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class ImageRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof ImageIcon) {
            // value 是预期的 ImageIcon 实例
            JLabel label = new JLabel();
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setIcon((ImageIcon) value);
            return label;
        } else {
            // 否则，使用默认的单元格渲染器
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
}
