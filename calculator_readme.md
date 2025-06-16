# 📜 Calculator Application - README

This project is a Java-based expression evaluator that supports variable assignments, arithmetic operations, and unary operations like `++` and `--` (both prefix and postfix). It can be used as a simple command-line calculator with persistent variable storage during runtime.

---

## 🏗️ Project Structure

```
com.taboola.calculator
├── Main.java              # Entry point for the application
├── CalculatorApp.java     # Facade managing line processing and state
├── LineProcessor.java     # Parses input lines and performs assignments/evaluations
├── Tokenizer.java         # Splits input strings into tokens
├── ExpressionParser.java  # Converts infix expressions to postfix (RPN)
├── Evaluator.java         # Evaluates postfix expressions
├── Token.java             # Represents units in an expression (numbers, vars, ops)
├── Operator.java          # Enum for all supported operators
├── VariableStore.java     # Manages variable states and their values
├── AssignmentOperator.java # Enum for assignment operators like =, +=, -=
```

---

## 🚀 How to Run

Compile and run the `Main` class. Enter expressions or assignments in the console:

```bash
$ java com.taboola.calculator.Main
```

You can enter expressions like:

- `a = 1`
- `b = ++a`
- `c = a++`
- `d = b`
- `result = (a + ++b) * (c-- - d)`

Press Enter on a blank line to exit.

---

## 🧐 Key Features

### 1. Assignment & Expression Support

Supports simple and compound assignments (`=`, `+=`, `-=`), variables, and arithmetic operations.

**Example:**

```text
a = 10
b = a + 5        // b = 15
c += b * 2       // if c was 0, now c = 30
```

### 2. Unary Operations

Supports prefix (`++x`, `--x`) and postfix (`x++`, `x--`) operators with correct side effects on variables.

**Example:**

```text
a = 1
b = ++a      // a = 2, b = 2
c = a++      // c = 2, a = 3
```

### 3. Expression Parsing

- Infix to postfix conversion.
- Operator precedence and associativity are respected.

**Example:**

```text
x = 2
y = 3
z = 4
result = ++x * y-- + z    // x = 3, y = 2, result = 3*3 + 4 = 13
```

---

## 🧹 Component Explanations

### `Main.java`

CLI interface that loops for user input, passes each line to `CalculatorApp`, and prints variable states.

**Example Run:**

```text
Enter expressions or assignments:
a = 5
b = a + 2
```

### `CalculatorApp.java`

Manages coordination between line parsing and evaluation. Delegates to `LineProcessor` and `VariableStore`.

**Example Use:**

```java
CalculatorApp app = new CalculatorApp();
app.processLine("x = 10");
app.printVariables();
```

### `LineProcessor.java`

Handles:

- Detecting assignments vs expressions
- Parsing operators and operands
- Validating variable names
- Updating variable values using `AssignmentOperator`

**Example Logic:**

```text
a += 5      // fetch 'a', add 5, store back
```

### `Tokenizer.java`

Tokenizes input strings:

- Recognizes variables, numbers, operators, and parentheses
- Handles `++`, `--` as prefix or postfix

**Example:**

```text
Input: ++x * (y + 3)
Tokens: [++, x, *, (, y, +, 3, )]
```

### `ExpressionParser.java`

Converts infix expressions to postfix:

- Uses a stack for operators and parentheses
- Ensures proper ordering and operator precedence

**Example:**

```text
Input: a + b * c
Postfix: a b c * +
```

### `Evaluator.java`

Evaluates postfix expressions:

- Uses a stack
- Applies binary or unary operators
- Modifies variable values as needed for `++`/`--`

**Example:**

```text
Postfix: [a, b, *, ++x]
Evaluation: performs multiplication, then increments x
```

### `Token.java`

Represents each item in an expression:

- Type: `NUMBER`, `VARIABLE`, `OPERATOR`, `PARENTHESIS`
- Flags: `isUnary`, `isPrefix`, `associatedVariable`

**Example:**

```text
Token("++", isPrefix=true, associatedVariable="x")
```

### `Operator.java`

Enum listing supported operators (`+`, `-`, `*`, `/`, `++`, `--`) with logic for applying each operation.

**Example:**

```java
Operator.ADD.apply(2, 3); // returns 5
```

### `AssignmentOperator.java`

Enum for assignment operations such as `=`, `+=`, `-=` with logic for applying them to integer values.

**Example:**

```java
AssignmentOperator.PLUS_ASSIGN.apply(10, 5); // returns 15
AssignmentOperator.MINUS_ASSIGN.apply(10, 3); // returns 7
```

### `VariableStore.java`

In-memory map of variable names to their integer values.

**Example:**

```java
store.set("x", 10);
int value = store.get("x");
```

---

## 📦 Example Expressions

```text
a = 1        // a = 1
b = ++a      // a = 2, b = 2
c = a++      // c = 2, a = 3
d = b        // d = 2
result = (a + ++b) * (c-- - d)
            // b = 3, a = 3, c = 2, d = 2
            // result = (3 + 4) * (2 - 2) = 7 * 0 = 0
```

---

## ✅ Tests and Extensibility

- Tests can be written for each component (especially `Tokenizer`, `ExpressionParser`, and `Evaluator`).
- Easily extendable to support additional operators or functions.

---

## 🗼 Error Handling

- Unknown tokens or mismatched parentheses raise clear exceptions.
- Invalid variable names are rejected.


