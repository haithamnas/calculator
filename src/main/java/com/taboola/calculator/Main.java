package com.taboola.calculator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        CalculatorApp app = new CalculatorApp();
        Scanner scanner = new Scanner(System.in);

        log.info("Enter expressions or assignments ():");
        log.info("Press Enter on an empty line quit.");
        while (true) {
            String line;
            if (!scanner.hasNextLine() || (line = scanner.nextLine()).isBlank()) {
                log.info("No input or blank line. Quitting.");
                break;
            }
            try {
                app.processLine(line);
                app.printVariables();
            } catch (Exception e) {
                log.error("Error processing '{}': {}", line, e.getMessage());
            }
        }
    }
}
