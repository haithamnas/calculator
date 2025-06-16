package com.taboola.calculator;

import java.util.HashMap;
import java.util.Map;

public class VariableStore {
    private final Map<String, Integer> variables = new HashMap<>();

    public int get(String var) {
        return variables.getOrDefault(var, 0);
    }

    public void set(String var, int value) {
        variables.put(var, value);
    }

    public int getOrDefault(String var, int defaultValue) {
        return variables.getOrDefault(var, defaultValue);
    }

    @Override
    public String toString() {
        return variables.toString();
    }
}
