package com.emkaar;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String statement = scanner.nextLine();
        Calculator calculator = new Calculator();
        System.out.println(calculator.evaluate(statement));
        scanner.close();

    }
}
