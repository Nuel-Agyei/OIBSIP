package com.example.androidappdevelopmentlevelthreecalculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private TextView tvDisplay;
    private StringBuilder currentInput = new StringBuilder("0");
    private boolean isError = false;

    // Variables to hold calculation state
    private double firstValue = Double.NaN;
    private char currentOperator = ' ';
    private boolean startNewNumber = false;

    private final DecimalFormat decimalFormat = new DecimalFormat("#.########");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDisplay = findViewById(R.id.tvDisplay);

        // Set up Number Button Listeners (0-9 and Decimal)
        setNumberListeners();

        // Set up Operator Button Listeners (+, -, ×, ÷)
        setOperatorListeners();

        // Set up Special Button Listeners (C, Backspace, Equals, Percentage)
        setSpecialListeners();
    }

    private void setNumberListeners() {
        int[] numberIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot
        };

        View.OnClickListener listener = v -> {
            if (isError) {
                clearAll();
            }

            String buttonText = ((Button) v).getText().toString();

            if (startNewNumber) {
                currentInput.setLength(0);
                startNewNumber = false;
            }

            if (currentInput.toString().equals("0") && !buttonText.equals(".")) {
                currentInput.setLength(0);
            }

            // Prevent multiple decimals in the same number
            if (buttonText.equals(".") && currentInput.toString().contains(".")) {
                return;
            }

            currentInput.append(buttonText);
            tvDisplay.setText(currentInput.toString());
        };

        for (int id : numberIds) {
            findViewById(id).setOnClickListener(listener);
        }
    }

    private void setOperatorListeners() {
        findViewById(R.id.btnPlus).setOnClickListener(v -> selectOperator('+'));
        findViewById(R.id.btnMinus).setOnClickListener(v -> selectOperator('-'));
        findViewById(R.id.btnMultiply).setOnClickListener(v -> selectOperator('×'));
        findViewById(R.id.btnDivide).setOnClickListener(v -> selectOperator('÷'));
    }

    private void selectOperator(char operator) {
        if (isError) return;

        if (!Double.isNaN(firstValue) && !startNewNumber) {
            calculateResult();
        } else {
            try {
                firstValue = Double.parseDouble(currentInput.toString());
            } catch (NumberFormatException e) {
                firstValue = 0.0;
            }
        }
        currentOperator = operator;
        startNewNumber = true;
    }

    private void setSpecialListeners() {
        // Clear Button (C)
        findViewById(R.id.btnClear).setOnClickListener(v -> clearAll());

        // Backspace Button (⌫)
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (isError) {
                clearAll();
                return;
            }

            if (currentInput.length() > 0) {
                currentInput.deleteCharAt(currentInput.length() - 1);
            }

            if (currentInput.length() == 0 || currentInput.toString().equals("-")) {
                currentInput.setLength(0);
                currentInput.append("0");
            }
            tvDisplay.setText(currentInput.toString());
        });

        // Percentage Button (%)
        findViewById(R.id.btnPercent).setOnClickListener(v -> {
            if (isError) return;
            try {
                double val = Double.parseDouble(currentInput.toString());
                val = val / 100.0;
                currentInput.setLength(0);
                currentInput.append(decimalFormat.format(val));
                tvDisplay.setText(currentInput.toString());
            } catch (NumberFormatException e) {
                showError();
            }
        });

        // Equals Button (=)
        findViewById(R.id.btnEquals).setOnClickListener(v -> {
            if (isError || Double.isNaN(firstValue) || currentOperator == ' ') return;
            calculateResult();
            currentOperator = ' ';
            startNewNumber = true;
        });
    }

    private void calculateResult() {
        double secondValue;
        try {
            secondValue = Double.parseDouble(currentInput.toString());
        } catch (NumberFormatException e) {
            return;
        }

        switch (currentOperator) {
            case '+':
                firstValue += secondValue;
                break;
            case '-':
                firstValue -= secondValue;
                break;
            case '×':
                firstValue *= secondValue;
                break;
            case '÷':
                if (secondValue == 0.0) {
                    showError();
                    return;
                }
                firstValue /= secondValue;
                break;
        }

        currentInput.setLength(0);
        currentInput.append(decimalFormat.format(firstValue));
        tvDisplay.setText(currentInput.toString());
    }

    private void clearAll() {
        isError = false;
        firstValue = Double.NaN;
        currentOperator = ' ';
        currentInput.setLength(0);
        currentInput.append("0");
        startNewNumber = false;
        tvDisplay.setText("0");
    }

    private void showError() {
        isError = true;
        tvDisplay.setText("Error");
        currentInput.setLength(0);
        currentInput.append("0");
        firstValue = Double.NaN;
        currentOperator = ' ';
    }
}