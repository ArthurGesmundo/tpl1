import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import javax.swing.*;

public class Analyzee extends JFrame {
    private JTextField inputField;
    private JTextArea resultArea;
    private JButton lexemeButton;
    private JButton syntaxButton;
    private JButton semanticsButton;
    private Map<String, String> symbolTable = new HashMap<>();

    private Set<String> declaredVariables;

    public Analyzee() {
        declaredVariables = new HashSet<>();
        setTitle("Analyzee");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        inputField = new JTextField(20);
        resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);

        lexemeButton = new JButton("Check Lexeme");
        syntaxButton = new JButton("Check Syntax");
        semanticsButton = new JButton("Check Semantics");

        lexemeButton.addActionListener(new AnalyzeAction("lexeme"));
        syntaxButton.addActionListener(new AnalyzeAction("syntax"));
        semanticsButton.addActionListener(new AnalyzeAction("semantics"));

        add(new JLabel("Input:"));
        add(inputField);
        add(lexemeButton);
        add(syntaxButton);
        add(semanticsButton);
        add(new JScrollPane(resultArea));

        setVisible(true);
    }

    private class AnalyzeAction implements ActionListener {
        private String type;

        public AnalyzeAction(String type) {
            this.type = type;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String input = inputField.getText().trim();
            String result;

            switch (type) {
                case "lexeme":
                    result = checkLexeme(input);
                    break;
                case "syntax":
                    result = checkSyntax(input);
                    break;
                case "semantics":
                    result = checkSemantics(input);
                    break;
                default:
                    result = "Unknown analysis type.";
            }

            resultArea.setText(result);
        }
    }

    private String checkLexeme(String input) {

        String[] tokens = tokenize(input);
        StringBuilder result = new StringBuilder("Lexeme Check:\n");
    
        for (String token : tokens) {
            if (isValidIdentifier(token)) {
                declaredVariables.add(token); 
                result.append(token).append(" is a valid identifier.\n");
            } else if (isValidLiteral(token)) {
                result.append(token).append(" is a valid literal.\n");
            } else if (token.equals("=")) {
                result.append(token).append(" is a valid assignment operator.\n");
            } else if (token.equals(";")) {
                result.append(token).append(" is a valid statement terminator.\n");
            } else if (token.trim().isEmpty()) {
                // Ignore spaces
                continue;
            } else {
                result.append(token).append(" is an invalid lexeme.\n");
            }
        }
        return result.toString();
    }

    private String checkSyntax(String input) {
        String[] tokens = tokenize(input);
        
        if (tokens.length == 5 && 
            isValidType(tokens[0]) && 
            isValidIdentifier(tokens[1]) && 
            tokens[2].equals("=") && 
            isValidLiteral(tokens[3]) && 
            tokens[4].equals(";")) {
            

            symbolTable.put(tokens[1], tokens[0]); 
            return "Syntax is correct.";
        } else {
            return "Syntax is incorrect.";
        }
    }

    private String checkSemantics(String input) {
        String[] tokens = tokenize(input);
        
        
        if (tokens.length == 5 && 
            isValidType(tokens[0]) && 
            isValidIdentifier(tokens[1]) && 
            tokens[2].equals("=") && 
            isValidLiteral(tokens[3]) && 
            tokens[4].equals(";")) {
            
            
            symbolTable.put(tokens[1], tokens[0]); 
            return "Semantics are correct.";
        }
        
       
        if (tokens.length == 5 && tokens[2].equals("=")) {
            String variable = tokens[1].trim();
            if (symbolTable.containsKey(variable)) {
                String declaredType = symbolTable.get(variable);
                if (isTypeCompatible(declaredType, tokens[3])) {
                    return "Semantics are correct: Variable '" + variable + "' assigned value " + tokens[3] + ".";
                } else {
                    return "Semantic error: Type mismatch for variable '" + variable + "'. Expected " + declaredType + " but got " + tokens[3] + ".";
                }
            } else {
                return "Semantics are incorrect: Variable '" + variable + "' is not declared.";
            }
        }
        
        return "Semantics check failed: Invalid input.";
    }
    
    private boolean isTypeCompatible(String declaredType, String value) {
        if (declaredType.equals("int")) {
            return value.matches("\\d+"); 
        } else if (declaredType.equals("String")) {
            return value.matches("\".*\""); 
        }
        return false; 
    }

    private String[] tokenize(String input) {
    
    StringTokenizer tokenizer = new StringTokenizer(input, " =;()", true);
    String[] tokens = new String[tokenizer.countTokens()];
    int index = 0;
    while (tokenizer.hasMoreTokens()) {
        String token = tokenizer.nextToken().trim();
        if (!token.isEmpty()) {
            tokens[index++] = token;
        }
    }
    return Arrays.copyOf(tokens, index); 
}

    private boolean isValidIdentifier(String token) {
        
        return token.matches("[a-zA-Z_][a-zA-Z0-9_]*");
    }

    private boolean isValidLiteral(String token) {
        
        return token.matches("\".*\"") || token.matches("\\d+"); 
    }

    private boolean isValidType(String token) {
        return token.equals("int") || 
               token.equals("float") || 
               token.equals("double") || 
               token.equals("char") || 
               token.equals("String"); 
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Analyzee::new);
    }
}