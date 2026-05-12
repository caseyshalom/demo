import java.util.Scanner;

public class Rental {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        // deklarasi variabel dan array
        String[] kdMobil = new String[100];
        String[] nmMobil = new String[100];
        int[] hrgSewa = new int[100];
        int[] lmSewa = new int[100];

        double[] subTotal = new double[100];
        double[] diskon = new double[100];
        double[] pajak = new double[100];
        double[] totBayar = new double[100];

        double totPendapatan = 0;

        System.out.println("CV. MAJU JAYA RENTAL");
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        System.out.print("Nama Petugas      : ");
        String nmPetugas = scan.nextLine();

        System.out.print("Tanggal Transaksi : ");
        String tgl = scan.nextLine();

        System.out.print("Jumlah Data       : ");
        int jmlData = scan.nextInt();

        System.out.println("--------------------------------------------------------");

        // proses input data
        for (int i = 0; i < jmlData; i++) {
            System.out.println("\nData Ke-" + (i + 1));

            System.out.print("Kode Mobil        : ");
            kdMobil[i] = scan.next();

            System.out.print("Lama Sewa (Hari)  : ");
            lmSewa[i] = scan.nextInt();

            // penentuan mobil dan harga
            if (kdMobil[i].equalsIgnoreCase("MB001")) {
                nmMobil[i] = "Avanza";
                hrgSewa[i] = 350000;
            } else if (kdMobil[i].equalsIgnoreCase("MB002")) {
                nmMobil[i] = "Innova";
                hrgSewa[i] = 500000;
            } else if (kdMobil[i].equalsIgnoreCase("MB003")) {
                nmMobil[i] = "Pajero";
                hrgSewa[i] = 850000;
            } else {
                nmMobil[i] = "Tidak Ada";
                hrgSewa[i] = 0;
            }

            // hitung subtotal
            subTotal[i] = hrgSewa[i] * lmSewa[i];

            // hitung diskon
            if (lmSewa[i] >= 7) {
                diskon[i] = subTotal[i] * 0.10;
            } else if (lmSewa[i] >= 3) {
                diskon[i] = subTotal[i] * 0.05;
            } else {
                diskon[i] = 0;
            }

            // hitung pajak dan total bayar
            pajak[i] = (subTotal[i] - diskon[i]) * 0.11;
            totBayar[i] = subTotal[i] - diskon[i] + pajak[i];

            // akumulasi pendapatan
            totPendapatan += totBayar[i];

            System.out.println("--------------------------------------------------------");
        }

        // cetak laporan
        System.out.println("\n================ LAPORAN RENTAL MOBIL =================");
        System.out.println("Nama Petugas : " + nmPetugas);
        System.out.println("Tanggal      : " + tgl);
        System.out.println(
                "==============================================================================================");
        System.out.println("No  Kode   Nama Mobil   Harga/Hari   Lama   Subtotal   Diskon   Pajak   Total Bayar");
        System.out.println(
                "==============================================================================================");

        for (int i = 0; i < jmlData; i++) {
            System.out.printf(
                    "%-3d %-6s %-12s %-12d %-6d %-10.0f %-8.0f %-8.0f %-10.0f\n",
                    (i + 1), kdMobil[i], nmMobil[i], hrgSewa[i], lmSewa[i],
                    subTotal[i], diskon[i], pajak[i], totBayar[i]);
        }

        System.out.println(
                "==============================================================================================");
        System.out.println("Total Pendapatan : Rp. " + totPendapatan);
        System.out.println(
                "==============================================================================================");
    }
}
