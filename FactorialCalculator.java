import java.util.Scanner;

public class FactorialCalculator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== 階層計算機 ===");

        int number;
        while (true) {
            System.out.print("請輸入一個非負整數來計算其階層: ");
            if (scanner.hasNextInt()) {
                int n = scanner.nextInt();
                if (n >= 0) {
                    number = n;
                    break;
                } else {
                    System.out.println("錯誤：請輸入非負整數（0 或以上）。");
                }
            } else {
                System.out.println("錯誤：請輸入有效的整數。");
                scanner.next();
            }
        }

        long result = factorial(number);
        System.out.println("\n計算結果：" + number + "! = " + result);

        scanner.close();
    }

    public static long factorial(int n) {
        if (n == 0 || n == 1) {
            return 1;
        }
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}
