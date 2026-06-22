import java.util.Scanner;

public class Welcome {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("請輸入姓名: ");
        String name = scanner.nextLine();
        System.out.println(name + " helloworld!");
        scanner.close();
    }
}
