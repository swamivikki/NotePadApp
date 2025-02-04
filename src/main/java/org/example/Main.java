package org.example;
import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.file.*;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

    private static JTextArea textArea;
    private static UndoManager undoManager;
    private static Timer autosaveTimer;
    private static String currentFilePath;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Notepad - Step 7");

        // Create text area
        textArea = new JTextArea();
        frame.add(new JScrollPane(textArea));

        // Initialize UndoManager
        undoManager = new UndoManager();
        textArea.getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));

        // Initialize autosave timer
        autosaveTimer = new Timer();
        autosaveTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                autosaveDocument();
            }
        }, 60000, 60000);  // Autosave every 60 seconds

        // Menu bar and menus
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu viewMenu = new JMenu("View");
        JMenu searchMenu = new JMenu("Search");

        // Menu items
        JMenuItem newFileItem = new JMenuItem("New");
        JMenuItem openFileItem = new JMenuItem("Open");
        JMenuItem saveFileItem = new JMenuItem("Save");
        JMenuItem saveAsItem = new JMenuItem("Save As");
        JMenuItem undoItem = new JMenuItem("Undo");
        JMenuItem redoItem = new JMenuItem("Redo");
        JMenuItem fontSettingsItem = new JMenuItem("Font Settings");
        JMenuItem toggleDarkModeItem = new JMenuItem("Toggle Dark Mode");
        JMenuItem findItem = new JMenuItem("Find");
        JMenuItem replaceItem = new JMenuItem("Replace");

        fileMenu.add(newFileItem);
        fileMenu.add(openFileItem);
        fileMenu.add(saveFileItem);
        fileMenu.add(saveAsItem);

        editMenu.add(undoItem);
        editMenu.add(redoItem);

        viewMenu.add(fontSettingsItem);
        viewMenu.add(toggleDarkModeItem);

        searchMenu.add(findItem);
        searchMenu.add(replaceItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(searchMenu);

        frame.setJMenuBar(menuBar);

        // Event Listeners
        newFileItem.addActionListener(e -> {
            textArea.setText("");
            frame.setTitle("Notepad - New File");
            currentFilePath = null;
        });

        openFileItem.addActionListener(e -> {
            String content = FileHandler.openFile(frame);
            if (content != null) {
                textArea.setText(content);
                frame.setTitle("Notepad - File Opened");
            }
        });

        saveFileItem.addActionListener(e -> {
            if (currentFilePath == null) {
                FileHandler.saveFile(frame, textArea.getText());
            } else {
                FileHandler.saveFile(frame, textArea.getText(), currentFilePath);
            }
        });

        saveAsItem.addActionListener(e -> {
            FileHandler.saveFileAs(frame, textArea.getText());
        });

        undoItem.addActionListener(e -> {
            if (undoManager.canUndo()) undoManager.undo();
        });

        redoItem.addActionListener(e -> {
            if (undoManager.canRedo()) undoManager.redo();
        });

        fontSettingsItem.addActionListener(e -> openFontSettingsDialog(frame));

        toggleDarkModeItem.addActionListener(e -> toggleDarkMode());

        findItem.addActionListener(e -> openSearchDialog(frame, false));
        replaceItem.addActionListener(e -> openSearchDialog(frame, true));

        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // Method to handle search and replace dialog
    private static void openSearchDialog(JFrame frame, boolean isReplace) {
        JDialog searchDialog = new JDialog(frame, isReplace ? "Find and Replace" : "Find", true);
        searchDialog.setLayout(new FlowLayout());

        JLabel searchLabel = new JLabel("Find:");
        JTextField searchField = new JTextField(10);
        JButton findButton = new JButton("Find");

        searchDialog.add(searchLabel);
        searchDialog.add(searchField);
        searchDialog.add(findButton);

        if (isReplace) {
            JLabel replaceLabel = new JLabel("Replace with:");
            JTextField replaceField = new JTextField(10);
            JButton replaceButton = new JButton("Replace");

            searchDialog.add(replaceLabel);
            searchDialog.add(replaceField);
            searchDialog.add(replaceButton);

            replaceButton.addActionListener(e -> {
                String searchText = searchField.getText();
                String replaceText = replaceField.getText();
                String content = textArea.getText();

                if (!searchText.isEmpty() && content.contains(searchText)) {
                    textArea.setText(content.replaceFirst(searchText, replaceText));
                    JOptionPane.showMessageDialog(searchDialog, "First occurrence replaced!");
                } else {
                    JOptionPane.showMessageDialog(searchDialog, "Text not found!");
                }
            });
        }

        findButton.addActionListener(e -> {
            String searchText = searchField.getText();
            String content = textArea.getText();

            if (!searchText.isEmpty() && content.contains(searchText)) {
                int startIndex = content.indexOf(searchText);
                int endIndex = startIndex + searchText.length();

                textArea.setCaretPosition(endIndex);
                textArea.select(startIndex, endIndex);
                textArea.grabFocus();
            } else {
                JOptionPane.showMessageDialog(searchDialog, "Text not found!");
            }
        });

        searchDialog.setSize(300, 150);
        searchDialog.setVisible(true);
    }


    // Method to open a font settings dialog
    private static void openFontSettingsDialog(JFrame frame) {
        JDialog fontDialog = new JDialog(frame, "Font Settings", true);
        fontDialog.setLayout(new FlowLayout());

        JLabel fontLabel = new JLabel("Font Size:");
        JTextField fontSizeField = new JTextField(5);

        JButton applyButton = new JButton("Apply");

        applyButton.addActionListener(e -> {
            try {
                int fontSize = Integer.parseInt(fontSizeField.getText());
                textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, fontSize));
                fontDialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(fontDialog, "Invalid font size");
            }
        });

        fontDialog.add(fontLabel);
        fontDialog.add(fontSizeField);
        fontDialog.add(applyButton);

        fontDialog.setSize(200, 100);
        fontDialog.setVisible(true);
    }

    // Method to toggle dark mode
    private static void toggleDarkMode() {
        Color backgroundColor = textArea.getBackground();
        if (backgroundColor.equals(Color.BLACK)) {
            textArea.setBackground(Color.WHITE);
            textArea.setForeground(Color.BLACK);
        } else {
            textArea.setBackground(Color.BLACK);
            textArea.setForeground(Color.WHITE);
        }
    }

    // Method to autosave the document every minute
    private static void autosaveDocument() {
        if (currentFilePath != null && !textArea.getText().isEmpty()) {
            FileHandler.saveFile(null, textArea.getText(), currentFilePath);  // Save to the current path
            System.out.println("Document autosaved.");
        }
    }
}
