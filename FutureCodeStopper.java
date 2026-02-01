import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class FutureCodeStopper extends JFrame {

    // Modern Color Palette
    private final Color BG_DARK = new Color(15, 23, 42);      // Slate-900
    private final Color HEADER_BG = new Color(30, 41, 59);    // Slate-800
    private final Color ACCENT_BLUE = new Color(96, 165, 250); // Blue-400
    private final Color ERROR_RED = new Color(239, 68, 68);    // Red-500
    private final Color SUCCESS_GREEN = new Color(34, 197, 94); // Green-500
    private final Color TEXT_WHITE = new Color(248, 250, 252);

    private JTextArea editor;
    private JComboBox<String> langPicker;
    private JPanel statusLight;
    private JLabel errorLabel;
    private JPanel errorBanner;
    private Map<String, String> templates;

    public FutureCodeStopper() {
        initTemplates();
        setupUI();
        setupLogic();
    }

    private void initTemplates() {
        templates = new HashMap<>();
        templates.put("Java", "public class Main {\n    public static void main(String[] args) {\n        \n    }\n}");
        templates.put("Python", "def main():\n    print(\"Hello World\")\n\nif __name__ == \"__main__\":\n    main()");
        templates.put("JavaScript", "function init() {\n    console.log(\"Ready\");\n}");
        templates.put("HTML", "<!DOCTYPE html>\n<html>\n<head>\n</head>\n<body>\n\n</body>\n</html>");
        templates.put("CSS", ".container {\n    display: flex;\n    color: blue;\n}");
    }

    private void setupUI() {
        setTitle("Future Code Stopper Pro");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));

        // --- HEADER SECTION ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Title and Subtitle
        JPanel titleGroup = new JPanel(new GridLayout(2, 1));
        titleGroup.setOpaque(false);
        JLabel title = new JLabel("Future Code Stopper");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(ACCENT_BLUE);
        JLabel subtitle = new JLabel("Strict Syntax Enforcement Mode: ACTIVE");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(148, 163, 184));
        titleGroup.add(title);
        titleGroup.add(subtitle);

        // Controls (Combo + Light)
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        controls.setOpaque(false);

        langPicker = new JComboBox<>(new String[]{"Java", "Python", "JavaScript", "HTML", "CSS"});
        langPicker.setBackground(BG_DARK);
        langPicker.setForeground(Color.WHITE);
        langPicker.setFocusable(false);

        statusLight = new JPanel();
        statusLight.setPreferredSize(new Dimension(16, 16));
        statusLight.setBackground(SUCCESS_GREEN);
        statusLight.setBorder(new LineBorder(new Color(255,255,255,50), 1));

        controls.add(langPicker);
        controls.add(statusLight);

        header.add(titleGroup, BorderLayout.WEST);
        header.add(controls, BorderLayout.EAST);

        // --- EDITOR SECTION ---
        editor = new JTextArea();
        editor.setBackground(new Color(22, 30, 46));
        editor.setForeground(TEXT_WHITE);
        editor.setCaretColor(ACCENT_BLUE);
        editor.setFont(new Font("Consolas", Font.PLAIN, 18));
        editor.setMargin(new Insets(20, 20, 20, 20));
        editor.setText(templates.get("Java"));

        JScrollPane scroll = new JScrollPane(editor);
        scroll.setBorder(new CompoundBorder(
                new EmptyBorder(20, 25, 10, 25),
                new LineBorder(new Color(51, 65, 85), 2, true)
        ));
        scroll.getViewport().setBackground(BG_DARK);

        // --- ERROR BANNER (FOOTER) ---
        errorBanner = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        errorBanner.setBackground(new Color(127, 29, 29)); // Dark Red
        errorBanner.setVisible(false);

        errorLabel = new JLabel("Syntax Error Detected!");
        errorLabel.setForeground(Color.WHITE);
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        errorBanner.add(new JLabel("⚠️"));
        errorBanner.add(errorLabel);

        // Assemble
        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(errorBanner, BorderLayout.SOUTH);
    }

    private void setupLogic() {
        // Change template on language switch
        langPicker.addActionListener(e -> {
            editor.setText(templates.get((String) langPicker.getSelectedItem()));
            clearError();
        });

        // The "Stopper" Logic
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String currentLine = getCurrentLineText();
                    String lang = (String) langPicker.getSelectedItem();

                    ValidationResult result = validateStrictly(currentLine, lang);

                    if (!result.isValid) {
                        e.consume(); // BLOCK THE ENTER KEY
                        triggerError(result.message);
                    } else {
                        clearError();
                    }
                }
            }
        });
    }

    private String getCurrentLineText() {
        try {
            int caretPos = editor.getCaretPosition();
            int lineNum = editor.getLineOfOffset(caretPos);
            int start = editor.getLineStartOffset(lineNum);
            int end = editor.getLineEndOffset(lineNum);
            return editor.getText(start, end - start).trim();
        } catch (Exception ex) { return ""; }
    }

    private ValidationResult validateStrictly(String line, String lang) {
        if (line.isEmpty()) return new ValidationResult(true, "");

        switch (lang) {
            case "Java":
                if ((line.contains("class") || line.contains("void") || line.contains("public")) && !line.endsWith("{") && !line.endsWith(";"))
                    return new ValidationResult(false, "Java structure must end with '{' or ';'");
                if (count(line, '(') != count(line, ')')) return new ValidationResult(false, "Unbalanced parentheses ()");
                if (!line.endsWith("{") && !line.endsWith("}") && !line.endsWith(";") && !line.startsWith("//"))
                    return new ValidationResult(false, "Missing semicolon ';' at end of line.");
                break;

            case "Python":
                String[] pyBlocks = {"def", "if", "else", "elif", "for", "while", "class", "try", "with"};
                for (String b : pyBlocks) {
                    if (line.startsWith(b) && !line.endsWith(":"))
                        return new ValidationResult(false, "Python blocks must end with a colon ':'");
                }
                if (count(line, '"') % 2 != 0 || count(line, '\'') % 2 != 0)
                    return new ValidationResult(false, "Unclosed string quote detected.");
                break;

            case "JavaScript":
                if ((line.startsWith("function") || line.startsWith("if") || line.startsWith("for")) && !line.endsWith("{"))
                    return new ValidationResult(false, "JS block missing opening '{'");
                break;

            case "HTML":
                if (!line.startsWith("<") || !line.endsWith(">"))
                    return new ValidationResult(false, "HTML tags must be enclosed in '< >'");
                break;

            case "CSS":
                if (!line.contains(":") && !line.endsWith("{") && !line.endsWith("}"))
                    return new ValidationResult(false, "CSS selector must end with '{'");
                if (line.contains(":") && !line.endsWith(";"))
                    return new ValidationResult(false, "CSS properties must end with ';'");
                break;
        }
        return new ValidationResult(true, "");
    }

    private void triggerError(String msg) {
        errorLabel.setText("SYNTAX BLOCK: " + msg);
        errorBanner.setVisible(true);
        statusLight.setBackground(ERROR_RED);
        editor.setBorder(new LineBorder(ERROR_RED, 2));

        // Simple Shake Effect
        new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    editor.setLocation(editor.getX() + 5, editor.getY());
                    Thread.sleep(20);
                    editor.setLocation(editor.getX() - 10, editor.getY());
                    Thread.sleep(20);
                    editor.setLocation(editor.getX() + 5, editor.getY());
                }
            } catch (Exception ex) {}
        }).start();
    }

    private void clearError() {
        errorBanner.setVisible(false);
        statusLight.setBackground(SUCCESS_GREEN);
        editor.setBorder(null);
    }

    private int count(String s, char c) {
        int n = 0;
        for (char x : s.toCharArray()) if (x == c) n++;
        return n;
    }

    static class ValidationResult {
        boolean isValid; String message;
        ValidationResult(boolean v, String m) { this.isValid = v; this.message = m; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FutureCodeStopper().setVisible(true));
    }
}