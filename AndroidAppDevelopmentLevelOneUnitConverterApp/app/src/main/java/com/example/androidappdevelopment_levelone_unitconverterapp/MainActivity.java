package com.example.androidappdevelopment_levelone_unitconverterapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerCategory, spinnerFrom, spinnerTo;
    private EditText etValue;
    private Button btnConvert;
    private TextView tvResult;

    // Categories and corresponding units
    private final String[] categories = {"Length", "Weight", "Temperature"};
    private final String[] lengthUnits = {"Centimetres", "Metres", "Kilometres", "Inches", "Feet"};
    private final String[] weightUnits = {"Grams", "Kilograms", "Pounds", "Ounces"};
    private final String[] tempUnits = {"Celsius", "Fahrenheit", "Kelvin"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind views
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        etValue = findViewById(R.id.etValue);
        btnConvert = findViewById(R.id.btnConvert);
        tvResult = findViewById(R.id.tvResult);

        // Populate Category Spinner
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(categoryAdapter);

        // Change units dynamically when a category is selected
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = categories[position];
                loadUnitsForCategory(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Handle Convert Button Click
        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performConversion();
            }
        });
    }

    private void loadUnitsForCategory(String category) {
        String[] units;
        switch (category) {
            case "Weight":
                units = weightUnits;
                break;
            case "Temperature":
                units = tempUnits;
                break;
            case "Length":
            default:
                units = lengthUnits;
                break;
        }
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, units);
        spinnerFrom.setAdapter(unitAdapter);
        spinnerTo.setAdapter(unitAdapter);
    }

    private void performConversion() {
        String inputStr = etValue.getText().toString().trim();

        // Input Validation: Check if empty or non-numeric
        if (inputStr.isEmpty()) {
            Toast.makeText(this, "Please enter a value to convert", Toast.LENGTH_SHORT).show();
            return;
        }

        double inputValue;
        try {
            inputValue = Double.parseDouble(inputStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
            return;
        }

        String category = spinnerCategory.getSelectedItem().toString();
        String fromUnit = spinnerFrom.getSelectedItem().toString();
        String toUnit = spinnerTo.getSelectedItem().toString();
        double result = 0.0;

        if (category.equals("Length")) {
            result = convertLength(inputValue, fromUnit, toUnit);
        } else if (category.equals("Weight")) {
            result = convertWeight(inputValue, fromUnit, toUnit);
        } else if (category.equals("Temperature")) {
            result = convertTemperature(inputValue, fromUnit, toUnit);
        }

        tvResult.setText(String.format("Result: %.2f %s", result, toUnit));
    }

    private double convertLength(double val, String from, String to) {
        double valInMetres = val;
        switch (from) {
            case "Centimetres": valInMetres = val / 100.0; break;
            case "Kilometres": valInMetres = val * 1000.0; break;
            case "Inches": valInMetres = val * 0.0254; break;
            case "Feet": valInMetres = val * 0.3048; break;
        }

        switch (to) {
            case "Centimetres": return valInMetres * 100.0;
            case "Kilometres": return valInMetres / 1000.0;
            case "Inches": return valInMetres / 0.0254;
            case "Feet": return valInMetres / 0.3048;
            default: return valInMetres;
        }
    }

    private double convertWeight(double val, String from, String to) {
        double valInGrams = val;
        switch (from) {
            case "Kilograms": valInGrams = val * 1000.0; break;
            case "Pounds": valInGrams = val * 453.592; break;
            case "Ounces": valInGrams = val * 28.3495; break;
        }

        switch (to) {
            case "Kilograms": return valInGrams / 1000.0;
            case "Pounds": return valInGrams / 453.592;
            case "Ounces": return valInGrams / 28.3495;
            default: return valInGrams;
        }
    }

    private double convertTemperature(double val, String from, String to) {
        if (from.equals(to)) return val;

        double celsius = val;
        if (from.equals("Fahrenheit")) {
            celsius = (val - 32) * 5.0 / 9.0;
        } else if (from.equals("Kelvin")) {
            celsius = val - 273.15;
        }

        if (to.equals("Fahrenheit")) {
            return (celsius * 9.0 / 5.0) + 32;
        } else if (to.equals("Kelvin")) {
            return celsius + 273.15;
        }
        return celsius;
    }
}