package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private TextView displayText;
    private TextView expressionText;
    private StringBuilder currentInput = new StringBuilder();
    private StringBuilder expression = new StringBuilder();
    private double firstOperand = 0;
    private String pendingOperator = "";
    private boolean isNewInput = true;
    private boolean hasDecimal = false;
    private boolean justCalculated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayText = findViewById(R.id.displayText);
        expressionText = findViewById(R.id.expressionText);
        setupButtonListeners();
    }

    private void setupButtonListeners() {
        int[] numberIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };
        for (int id : numberIds) {
            findViewById(id).setOnClickListener(this::onNumberClick);
        }

        findViewById(R.id.btnAdd).setOnClickListener(v -> onOperatorClick("+"));
        findViewById(R.id.btnSubtract).setOnClickListener(v -> onOperatorClick("-"));
        findViewById(R.id.btnMultiply).setOnClickListener(v -> onOperatorClick("×"));
        findViewById(R.id.btnDivide).setOnClickListener(v -> onOperatorClick("÷"));

        findViewById(R.id.btnEquals).setOnClickListener(this::onEqualsClick);
        findViewById(R.id.btnClear).setOnClickListener(this::onClearClick);
        findViewById(R.id.btnDecimal).setOnClickListener(this::onDecimalClick);
        findViewById(R.id.btnBackspace).setOnClickListener(this::onBackspaceClick);
        findViewById(R.id.btnPercent).setOnClickListener(this::onPercentClick);
        findViewById(R.id.btnNegate).setOnClickListener(this::onNegateClick);
    }

    private void onNumberClick(View view) {
        Button btn = (Button) view;
        String digit = btn.getText().toString();

        if (justCalculated) {
            // Start fresh after calculation
            currentInput.setLength(0);
            expression.setLength(0);
            pendingOperator = "";
            justCalculated = false;
        }

        if (isNewInput) {
            currentInput.setLength(0);
            hasDecimal = false;
            isNewInput = false;
        }

        if (currentInput.length() == 0 && digit.equals("0")) {
            currentInput.append("0");
            updateDisplay();
            return;
        }

        if (currentInput.length() < 15) {
            currentInput.append(digit);
        }

        updateDisplay();
    }

    private void onOperatorClick(String op) {
        if (justCalculated) {
            // Use result as first operand for chained calculation
            justCalculated = false;
        }

        // If there's an expression already and user presses another operator,
        // calculate first then continue
        if (!pendingOperator.isEmpty() && !isNewInput) {
            calculate();
        }

        // Set current input as first operand
        String currentStr = currentInput.toString();
        if (currentStr.isEmpty()) {
            currentStr = "0";
        }

        firstOperand = Double.parseDouble(currentStr);

        // Build expression text
        String cleanStr = formatForDisplay(currentStr);
        if (expression.length() > 0 && !isNewInput) {
            expression.append(" ").append(cleanStr);
        } else if (isNewInput && expression.length() == 0) {
            expression.append(cleanStr);
        } else if (expression.length() > 0 && isNewInput) {
            // Replace last operator if we're just changing the operator
            int lastSpace = expression.lastIndexOf(" ");
            if (lastSpace > 0) {
                expression.setLength(lastSpace);
            } else {
                expression.setLength(0);
                expression.append(cleanStr);
            }
        } else {
            expression.append(cleanStr);
        }

        pendingOperator = op;
        expression.append(" ").append(op);
        isNewInput = true;

        updateExpression();
        displayText.setText(currentStr);
    }

    private void onEqualsClick(View view) {
        if (pendingOperator.isEmpty()) return;

        String currentStr = currentInput.toString();
        if (currentStr.isEmpty()) {
            currentStr = "0";
        }

        // Build complete expression
        String cleanStr = formatForDisplay(currentStr);
        if (expression.length() > 0 && !expression.toString().endsWith(cleanStr)) {
            expression.append(" ").append(cleanStr);
        }
        expression.append(" =");
        updateExpression();

        calculate();
        pendingOperator = "";
        justCalculated = true;
    }

    private void calculate() {
        double secondOperand;
        try {
            secondOperand = Double.parseDouble(currentInput.toString());
        } catch (NumberFormatException e) {
            secondOperand = 0;
        }

        double result = 0;
        String opSymbol = pendingOperator;

        switch (opSymbol) {
            case "+":
                result = firstOperand + secondOperand;
                break;
            case "-":
                result = firstOperand - secondOperand;
                break;
            case "×":
                result = firstOperand * secondOperand;
                break;
            case "÷":
                if (secondOperand == 0) {
                    displayText.setText("错误");
                    expression.setLength(0);
                    pendingOperator = "";
                    isNewInput = true;
                    justCalculated = true;
                    updateExpression();
                    return;
                }
                result = firstOperand / secondOperand;
                break;
        }

        if (Double.isInfinite(result) || Double.isNaN(result)) {
            displayText.setText("错误");
            expression.setLength(0);
            pendingOperator = "";
            isNewInput = true;
            justCalculated = true;
            updateExpression();
            return;
        }

        formatResult(result);

        // Allow chaining: the result becomes firstOperand for next op
        firstOperand = result;
    }

    private void formatResult(double result) {
        if (result == (long) result) {
            currentInput.setLength(0);
            currentInput.append((long) result);
        } else {
            DecimalFormat df = new DecimalFormat("#.##########");
            currentInput.setLength(0);
            currentInput.append(df.format(result));
        }
        updateDisplay();
    }

    private void onClearClick(View view) {
        currentInput.setLength(0);
        expression.setLength(0);
        firstOperand = 0;
        pendingOperator = "";
        isNewInput = true;
        hasDecimal = false;
        justCalculated = false;
        displayText.setText("0");
        updateExpression();
    }

    private void onDecimalClick(View view) {
        if (justCalculated) {
            currentInput.setLength(0);
            expression.setLength(0);
            pendingOperator = "";
            justCalculated = false;
            updateExpression();
        }

        if (isNewInput) {
            currentInput.setLength(0);
            currentInput.append("0");
            isNewInput = false;
        }

        if (!hasDecimal) {
            currentInput.append(".");
            hasDecimal = true;
            updateDisplay();
        }
    }

    private void onBackspaceClick(View view) {
        if (justCalculated) return; // Don't backspace after result

        if (currentInput.length() > 0) {
            char lastChar = currentInput.charAt(currentInput.length() - 1);
            if (lastChar == '.') {
                hasDecimal = false;
            }
            currentInput.deleteCharAt(currentInput.length() - 1);

            if (currentInput.length() == 0) {
                displayText.setText("0");
                isNewInput = true;
            } else {
                updateDisplay();
            }
        }
    }

    private void onPercentClick(View view) {
        if (justCalculated) return;
        if (currentInput.length() == 0) return;

        double value = Double.parseDouble(currentInput.toString());
        double result = value / 100;
        formatResult(result);
    }

    private void onNegateClick(View view) {
        if (justCalculated) return;
        if (currentInput.length() == 0) return;

        if (currentInput.charAt(0) == '-') {
            currentInput.deleteCharAt(0);
        } else {
            currentInput.insert(0, '-');
        }
        updateDisplay();
    }

    private void updateDisplay() {
        String text = currentInput.toString();
        if (text.endsWith(".0") && text.length() > 2) {
            text = text.substring(0, text.length() - 2);
        }
        displayText.setText(text);
    }

    private void updateExpression() {
        String text = expression.toString();
        expressionText.setText(text);
    }

    private String formatForDisplay(String numStr) {
        if (numStr.endsWith(".0") && numStr.length() > 2) {
            return numStr.substring(0, numStr.length() - 2);
        }
        return numStr;
    }
}