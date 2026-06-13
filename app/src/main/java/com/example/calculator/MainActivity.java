package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private TextView displayText;
    private StringBuilder currentInput = new StringBuilder();
    private double firstOperand = 0;
    private String operator = "";
    private boolean isNewInput = true;
    private boolean hasDecimal = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayText = findViewById(R.id.displayText);
        setupButtonListeners();
    }

    private void setupButtonListeners() {
        // Number buttons
        int[] numberIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };
        for (int id : numberIds) {
            findViewById(id).setOnClickListener(this::onNumberClick);
        }

        // Operator buttons
        findViewById(R.id.btnAdd).setOnClickListener(v -> onOperatorClick("+"));
        findViewById(R.id.btnSubtract).setOnClickListener(v -> onOperatorClick("-"));
        findViewById(R.id.btnMultiply).setOnClickListener(v -> onOperatorClick("×"));
        findViewById(R.id.btnDivide).setOnClickListener(v -> onOperatorClick("÷"));

        // Other buttons
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

        if (isNewInput) {
            currentInput.setLength(0);
            hasDecimal = false;
            isNewInput = false;
        }

        // Prevent multiple leading zeros
        if (currentInput.length() == 0 && digit.equals("0")) {
            currentInput.append("0");
            updateDisplay();
            return;
        }

        // Limit input length
        if (currentInput.length() < 15) {
            currentInput.append(digit);
        }

        updateDisplay();
    }

    private void onOperatorClick(String op) {
        if (currentInput.length() == 0) {
            currentInput.append("0");
        }

        if (!operator.isEmpty() && !isNewInput) {
            calculate();
        }

        firstOperand = Double.parseDouble(currentInput.toString());
        operator = op;
        isNewInput = true;
    }

    private void onEqualsClick(View view) {
        if (operator.isEmpty()) return;

        if (currentInput.length() == 0) {
            currentInput.append("0");
        }

        calculate();
        operator = "";
        isNewInput = true;
    }

    private void calculate() {
        double secondOperand = Double.parseDouble(currentInput.toString());
        double result = 0;

        switch (operator) {
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
                    currentInput.setLength(0);
                    operator = "";
                    isNewInput = true;
                    return;
                }
                result = firstOperand / secondOperand;
                break;
        }

        // Handle overflow
        if (Double.isInfinite(result) || Double.isNaN(result)) {
            displayText.setText("错误");
            currentInput.setLength(0);
            operator = "";
            isNewInput = true;
            return;
        }

        formatResult(result);
    }

    private void formatResult(double result) {
        // Format to avoid unnecessary decimals
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
        firstOperand = 0;
        operator = "";
        isNewInput = true;
        hasDecimal = false;
        displayText.setText("0");
    }

    private void onDecimalClick(View view) {
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
        if (currentInput.length() == 0) return;

        double value = Double.parseDouble(currentInput.toString());
        double result = value / 100;
        formatResult(result);
    }

    private void onNegateClick(View view) {
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
        // If it ends with ".0", show clean integer
        if (text.endsWith(".0") && text.length() > 2) {
            text = text.substring(0, text.length() - 2);
        }
        displayText.setText(text);
    }
}
