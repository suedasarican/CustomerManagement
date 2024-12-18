import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main extends JFrame {
    private JButton chooseFileButton;
    private JTextField searchWordField;
    private JTextArea textArea;

    public Main() {
        setTitle("File Search and Copy");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create menu bar and menu items
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem chooseMenuItem = new JMenuItem("Choose File");
        fileMenu.add(chooseMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        chooseMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseFile();
            }
        });

        JPanel searchPanel = new JPanel();
        searchWordField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchWord();
            }
        });
        searchPanel.add(new JLabel("Search Word:"));
        searchPanel.add(searchWordField);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);

        textArea = new JTextArea(15, 40);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
        fileChooser.setFileFilter(filter);
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                // Copy the contents of the selected file to the text area
                copyFileContents(selectedFile);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error reading file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void copyFileContents(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine()).append("\n");
            }
        }
        textArea.setText(sb.toString());
    }

    private void searchWord() {
        String searchText = textArea.getText();
        String wordToSearch = searchWordField.getText();

        long startTime = System.currentTimeMillis(); // Start time

        int count = countOccurrences(searchText, wordToSearch);

        long endTime = System.currentTimeMillis(); // End time
        long elapsedTime = endTime - startTime; // Calculate elapsed time

        JOptionPane.showMessageDialog(this, "The word '" + wordToSearch + "' appears " + count + " times in the text.\nElapsed Time: " + elapsedTime + " milliseconds.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
    }

    private int countOccurrences(String text, String word) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(word, index)) != -1) {
            count++;
            index += word.length();
        }
        return count;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new Main();
            }
        });
    }
}
