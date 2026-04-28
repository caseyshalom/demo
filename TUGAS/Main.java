import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Masukkan Kode Rumah: ");
        String kodeRumah = scanner.nextLine();

        String tipeRumah = "";
        long hargaRumah = 0;
        boolean isAvailable = true;

        // Menentukan Tipe dan Harga berdasarkan Kode Rumah
        if (kodeRumah.equalsIgnoreCase("A01")) {
            tipeRumah = "Tipe 36";
            hargaRumah = 150000000;
        } else if (kodeRumah.equalsIgnoreCase("A02")) {
            tipeRumah = "Tipe 45";
            hargaRumah = 250000000;
        } else if (kodeRumah.equalsIgnoreCase("A03")) {
            tipeRumah = "Tipe 60";
            hargaRumah = 400000000;
        } else if (kodeRumah.equalsIgnoreCase("B01")) {
            tipeRumah = "Tipe 70";
            hargaRumah = 550000000;
        } else if (kodeRumah.equalsIgnoreCase("B02")) {
            tipeRumah = "Tipe 90";
            hargaRumah = 750000000;
        } else {
            isAvailable = false;
        }

        // Jika kode ditemukan, hitung diskon dan tampilkan output
        if (isAvailable) {
            long hargaAkhir = hargaRumah;

            // Perhitungan diskon
            if (hargaRumah > 500000000) {
                hargaAkhir = hargaRumah - (hargaRumah * 10 / 100);
            } else if (hargaRumah > 300000000) {
                hargaAkhir = hargaRumah - (hargaRumah * 5 / 100);
            }

            System.out.println("Kode rumah: " + kodeRumah);
            System.out.println("Tipe rumah: " + tipeRumah);
            System.out.println("Harga rumah: " + hargaAkhir);
        } else {
            System.out.println("Kode rumah tidak tersedia");
        }

        scanner.close();
    }
}
