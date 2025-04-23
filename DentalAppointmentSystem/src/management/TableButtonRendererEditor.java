
package management;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

public class TableButtonRendererEditor extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
    private final JButton button;
    private String buttonLabel;

    public TableButtonRendererEditor(String label, ActionListener actionListener) {
        this.buttonLabel = label;
        this.button = new JButton(label);
        button.setFocusPainted(false);
        button.addActionListener(e -> {
            fireEditingStopped(); // Stop editing before triggering action
            actionListener.actionPerformed(e);
        });
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        button.setText(buttonLabel);
        return button;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                                                 int row, int column) {
        button.setText(buttonLabel);
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return buttonLabel;
    }
}

