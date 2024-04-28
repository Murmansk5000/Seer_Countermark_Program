package controller;

import view.CountermarkSelectionGUI;

import javax.swing.*;

public class Programme {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CountermarkSelectionGUI gui = new CountermarkSelectionGUI();
            gui.setVisible(true);
        });
    }
}