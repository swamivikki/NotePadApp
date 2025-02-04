package org.example;

import javax.swing.*;
import java.io.*;

public class FileHandler {

    // Method to open and read a file
    public static String openFile(JFrame frame) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(frame);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder content = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }

                return content.toString();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Error reading file: " + e.getMessage());
            }
        }
        return null;
    }

    // Method to save text to a file (with file chooser dialog)
    public static void saveFile(JFrame frame, String content) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(frame);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            saveContentToFile(file, content, frame);
        }
    }

    // Overloaded method to save without file chooser
    public static void saveFile(JFrame frame, String content, String filePath) {
        if (filePath != null) {
            File file = new File(filePath);
            saveContentToFile(file, content, frame);
        }
    }

    // Method to save the file as a new file (Save As functionality)
    public static void saveFileAs(JFrame frame, String content) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(frame);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            saveContentToFile(file, content, frame);
        }
    }

    // Helper method to write content to a file
    private static void saveContentToFile(File file, String content, JFrame frame) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
            JOptionPane.showMessageDialog(frame, "File saved successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error saving file: " + e.getMessage());
        }
    }
}
