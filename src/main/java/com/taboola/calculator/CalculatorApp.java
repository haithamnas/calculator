package com.taboola.calculator;

import java.util.List;

public class CalculatorApp {
    private final VariableStore variableStore = new VariableStore();
    private final LineProcessor processor = new LineProcessor(variableStore);

    public void processLine(String input) {
        processor.processLine(input);
    }

    public void printVariables() {
        System.out.println(variableStore);
    }
}
