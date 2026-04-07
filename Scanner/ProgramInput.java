package Scanner;

import java.util.Scanner;

class ProgramInput {

    public static void main(String[] args) {
        // membuat objek baru
        Scanner input = new Scanner(System.in);

        String nama;
        int nilai2;

        System.out.print("Masukkan Nama : ");
        nama = input.nextLine();

        System.out.print("Masukkan Nilai : ");
        nilai2 = input.nextInt();

        System.out.println("Nama : " + nama);
        System.out.println("Nilai : " + nilai2);

        // menutup scanner dan tidak ada warning pada IDE
        input.close();
    }
}
