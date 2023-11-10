package com.kurenkievtimur;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // write your code here
        Scanner scanner = new Scanner(System.in);

        System.out.print("How many mines do you want on the field? ");
        int mines = handleMineInput(scanner, 81);

        char[][] mineField = generateMinefield(9, 9, mines);
        char[][] playerField = generatePlayerField(9, 9);

        printMinefield(playerField);
        boolean isMine = false;
        do {
            int[] input = handleUserInput(scanner);

            int row = input[0];
            int column = input[1];

            String command = scanner.next();

            if (!isValidInput(row, column, command)) {
                System.out.println("Invalid input. Please enter valid coordinates and command!");
                continue;
            }

            if (command.equals("free")) {
                isMine = exploreCell(mineField, playerField, 9, 9, row, column);
            } else {
                markCell(playerField, row, column);
            }

            if (isMine) {
                revealAllMines(mineField, playerField);
            }

            printMinefield(playerField);
        } while (!checkWin(mineField, playerField) && !isMine);

        if (isMine) {
            System.out.println("You stepped on a mine and failed!");
        } else {
            System.out.println("Congratulations! You found all the mines!");
        }
    }

    public static int handleMineInput(Scanner scanner, int cells) {
        while (true) {
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter valid mines!");
                System.out.print("How many mines do you want on the field? ");
                scanner.nextLine();
                continue;
            }

            int mines = scanner.nextInt();
            if (mines > cells - 1) {
                System.out.println("Invalid input. Please enter valid mines!");
                System.out.print("How many mines do you want on the field? ");
                scanner.nextLine();
                continue;
            }

            return mines;
        }
    }

    public static int[] handleUserInput(Scanner scanner) {
        System.out.print("Set/unset mines marks or claim a cell as free: ");
        boolean isError = false;
        while (true) {
            if (!scanner.hasNextInt() || !scanner.hasNextInt()) {
                if (isError) {
                    System.out.println("Invalid input. Please enter valid coordinates and command!");
                    System.out.print("Set/unset mines marks or claim a cell as free: ");
                }
                isError = true;
                scanner.nextLine();
                continue;
            }

            int column = scanner.nextInt();
            int row = scanner.nextInt();
            return new int[]{row - 1, column - 1};
        }
    }

    public static void revealAllMines(char[][] mineField, char[][] playerField) {
        for (int i = 0; i < mineField.length; i++) {
            for (int j = 0; j < mineField[i].length; j++) {
                if (mineField[i][j] == 'X') {
                    playerField[i][j] = 'X';
                }
            }
        }
    }

    public static void printMinefield(char[][] field) {
        System.out.println("\n  | 1 2 3 4 5 6 7 8 9 | ");
        System.out.println("- | - - - - - - - - - | ");
        for (int i = 0; i < field.length; i++) {
            System.out.print(i + 1 + " | ");
            for (int j = 0; j < field[i].length; j++) {
                System.out.print(field[i][j] + " ");
            }
            System.out.print("|\n");
        }
        System.out.println("- | - - - - - - - - - | ");
    }

    public static char[][] generateMinefield(int rows, int columns, int mines) {
        char[][] field = new char[rows][columns];
        Random random = new Random();

        for (int i = 0; i < rows; i++) {
            Arrays.fill(field[i], '.');
        }

        for (int i = 0; i < mines; i++) {
            int row, column;
            do {
                row = random.nextInt(rows);
                column = random.nextInt(columns);
            } while (field[row][column] == 'X');
            field[row][column] = 'X';
        }

        return field;
    }

    public static char[][] generatePlayerField(int rows, int columns) {
        char[][] field = new char[rows][columns];

        for (int i = 0; i < rows; i++) {
            Arrays.fill(field[i], '.');
        }

        return field;
    }

    public static boolean exploreCell(char[][] mineField, char[][] playerField, int rows, int columns, int row, int column) {
        if (mineField[row][column] == 'X') {
            return true;
        }

        int mines = countMines(mineField, rows, columns, column, row);
        if (mines > 0) {
            playerField[row][column] = (char) (mines + '0');
        } else {
            playerField[row][column] = '/';

            int[] targetRows = {-1, -1, -1, 0, 0, 1, 1, 1};
            int[] targetColumns = {-1, 0, 1, -1, 1, -1, 0, 1};

            for (int i = 0; i < 8; i++) {
                int targetRow = row + targetRows[i];
                int targetColumn = column + targetColumns[i];

                if (targetRow >= 0 && targetRow < rows && targetColumn >= 0 && targetColumn < columns && playerField[targetRow][targetColumn] != '/') {
                    exploreCell(mineField, playerField, rows, columns, targetRow, targetColumn);
                }
            }
        }

        return false;
    }

    public static void markCell(char[][] field, int row, int column) {
        if (field[row][column] == '.') {
            field[row][column] = '*';
        } else if (field[row][column] == '*') {
            field[row][column] = '.';
        }
    }

    public static int countMines(char[][] field, int rows, int columns, int column, int row) {
        int count = 0;

        int[] targetRows = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] targetColumns = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int i = 0; i < 8; i++) {
            int targetRow = row + targetRows[i];
            int targetColumn = column + targetColumns[i];

            if (targetRow >= 0 && targetRow < rows && targetColumn >= 0 && targetColumn < columns) {
                if (field[targetRow][targetColumn] == 'X')
                    count++;
            }
        }

        return count;
    }

    public static boolean checkWin(char[][] mineField, char[][] playerField) {
        for (int i = 0; i < mineField.length; i++) {
            for (int j = 0; j < mineField[i].length; j++) {
                if ((mineField[i][j] == '.' && playerField[i][j] == '.')) {
                    return false;
                } else if (mineField[i][j] == '*' && playerField[i][j] == '.') {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean isValidInput(int row, int column, String command) {
        return row >= 0 && row < 9 && column >= 0 && column < 9 && (command.equals("free") || command.equals("mine"));
    }
}
