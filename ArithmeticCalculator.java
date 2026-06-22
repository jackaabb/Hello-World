import java.util.Scanner;

public class ArithmeticCalculator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== 三數加減乘除計算機 ===");

        int[] numbers = new int[3];
        for (int i = 0; i < 3; i++) {
            while (true) {
                System.out.print("請輸入第 " + (i + 1) + " 個數字 (1-10): ");
                if (scanner.hasNextInt()) {
                    int n = scanner.nextInt();
                    if (n >= 1 && n <= 10) {
                        numbers[i] = n;
                        break;
                    } else {
                        System.out.println("錯誤：請輸入 1 到 10 之間的數字。");
                    }
                } else {
                    System.out.println("錯誤：請輸入有效的整數。");
                    scanner.next();
                }
            }
        }

        int a = numbers[0], b = numbers[1], c = numbers[2];
        System.out.println("\n您輸入的三個數字為：" + a + ", " + b + ", " + c);
        System.out.println("-------------------------------");

        System.out.println("加法：" + a + " + " + b + " + " + c + " = " + (a + b + c));

        System.out.println("減法：" + a + " - " + b + " - " + c + " = " + (a - b - c));

        System.out.println("乘法：" + a + " × " + b + " × " + c + " = " + (a * b * c));

        System.out.print("除法：" + a + " ÷ " + b + " ÷ " + c + " = ");
        if (b == 0 || c == 0) {
            System.out.println("無法計算（除數不能為零）");
        } else {
            double result = (double) a / b / c;
            System.out.printf("%.4f%n", result);
        }

        scanner.close();
    }
}
