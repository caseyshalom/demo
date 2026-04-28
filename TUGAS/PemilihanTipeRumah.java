import java.util.Scanner;

public class PemilihanTipeRumah {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Masukkan Kode Rumah: ");
        String kodeRumah = scanner.nextLine().toUpperCase();

        String tipeRumah;
        double hargaRumah;
        double diskon = 0;
        double hargaSetelahDiskon;

        if (kodeRumah.equals("A01")) {
            tipeRumah = "Tipe 36";
            hargaRumah = 150000000;
        } else if (kodeRumah.equals("A02")) {
            tipeRumah = "Tipe 45";
            hargaRumah = 250000000;
        } else if (kodeRumah.equals("A03")) {
            tipeRumah = "Tipe 60";
            hargaRumah = 400000000;
        } else if (kodeRumah.equals("B01")) {
            tipeRumah = "Tipe 70";
            hargaRumah = 550000000;
        } else if (kodeRumah.equals("B02")) {
            tipeRumah = "Tipe 90";
            hargaRumah = 750000000;
        } else {
            System.out.println("Kode rumah tidak tersedia");
            scanner.close();
            return;
        }

        if (hargaRumah > 500000000) {
            diskon = 0.10;
        } else if (hargaRumah > 300000000) {
            diskon = 0.05;
        }

        hargaSetelahDiskon = hargaRumah - (hargaRumah * diskon);

        System.out.println("==============================");
        System.out.println("   INFORMASI RUMAH            ");
        System.out.println("==============================");
        System.out.println("Kode Rumah       : " + kodeRumah);
        System.out.println("Tipe Rumah       : " + tipeRumah);
        System.out.printf("Harga Rumah      : Rp %,.0f%n", hargaRumah);

        if (diskon > 0) {
            System.out.printf("Diskon           : %.0f%%%n", diskon * 100);
            System.out.printf("Harga Setelah Diskon : Rp %,.0f%n", hargaSetelahDiskon);
        } else {
            System.out.println("Diskon           : Tidak ada diskon");
            System.out.printf("Harga Setelah Diskon : Rp %,.0f%n", hargaSetelahDiskon);
        }

        System.out.println("==============================");

        scanner.close();
    }
}