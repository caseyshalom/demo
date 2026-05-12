import java.util.Scanner;

public class Rental {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("CV. MAJU JAYA RENTAL");
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        System.out.print("Nama Petugas      : ");
        String petugas = scanner.nextLine();

        System.out.print("Tanggal Transaksi : ");
        String tanggalTx = scanner.nextLine();

        System.out.print("Jumlah Data       : ");
        int n = scanner.nextInt();

        System.out.println("--------------------------------------------------------");

        // Deklarasi array dengan ukuran sesuai jumlah data
        String[] kode = new String[n];
        String[] nama = new String[n];
        int[] harga = new int[n];
        int[] lama = new int[n];
        double[] subttl = new double[n];
        double[] disc = new double[n];
        double[] pjk = new double[n];
        double[] ttlBayar = new double[n];

        double pendapatanTotal = 0;

        for (int i = 0; i < n; i++) {
            System.out.println("\nData Ke-" + (i + 1));

            System.out.print("Kode Mobil        : ");
            kode[i] = scanner.next();

            System.out.print("Lama Sewa (Hari)  : ");
            lama[i] = scanner.nextInt();

            // Menggunakan switch-case untuk percabangan kode mobil
            switch (kode[i].toUpperCase()) {
                case "MB001":
                    nama[i] = "Avanza";
                    harga[i] = 350000;
                    break;
                case "MB002":
                    nama[i] = "Innova";
                    harga[i] = 500000;
                    break;
                case "MB003":
                    nama[i] = "Pajero";
                    harga[i] = 850000;
                    break;
                default:
                    nama[i] = "Tidak Ada";
                    harga[i] = 0;
                    break;
            }

            subttl[i] = harga[i] * lama[i];

            if (lama[i] >= 7) {
                disc[i] = subttl[i] * 0.10;
            } else if (lama[i] >= 3) {
                disc[i] = subttl[i] * 0.05;
            } else {
                disc[i] = 0;
            }

            pjk[i] = (subttl[i] - disc[i]) * 0.11;
            ttlBayar[i] = subttl[i] - disc[i] + pjk[i];
            pendapatanTotal += ttlBayar[i];

            System.out.println("--------------------------------------------------------");
        }

        System.out.println("\n================ LAPORAN RENTAL MOBIL =================");
        System.out.println("Nama Petugas : " + petugas);
        System.out.println("Tanggal      : " + tanggalTx);
        System.out.println("==============================================================================================");
        System.out.println("No  Kode   Nama Mobil   Harga/Hari   Lama   Subtotal   Diskon   Pajak   Total Bayar");
        System.out.println("==============================================================================================");

        // Menggunakan perulangan while untuk output
        int index = 0;
        while (index < n) {
            System.out.printf(
                    "%-3d %-6s %-12s %-12d %-6d %-10.0f %-8.0f %-8.0f %-10.0f\n",
                    (index + 1),
                    kode[index],
                    nama[index],
                    harga[index],
                    lama[index],
                    subttl[index],
                    disc[index],
                    pjk[index],
                    ttlBayar[index]
            );
            index++;
        }

        System.out.println("==============================================================================================");
        System.out.println("Total Pendapatan : Rp. " + pendapatanTotal);
        System.out.println("==============================================================================================");
        
        scanner.close();
    }
}
