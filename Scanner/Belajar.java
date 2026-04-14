import java.util.Scanner;

public class Belajar {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        String nama;
        int n2;
        double n1, n3;

        System.out.println("Masukkan Nama : ");
        nama = input.nextLine();

        System.out.println("Masukkan Nilai : ");
        n1 = input.nextDouble();

        System.out.println("Masukkan Nilai : ");
        n2 = input.nextInt();

        n3 = n1 + n2;
        System.out.println("Nama : " + nama);
        System.out.println("Nilai : " + n3);

        input.close();
    }
}
