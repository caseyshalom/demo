import java.util.Scanner;

public class RentalMobil {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        // ARRAY
        String[] kodeMobil = new String[100];
        String[] namaMobil = new String[100];

        int[] hargaSewa = new int[100];
        int[] lamaSewa = new int[100];

        double[] subtotal = new double[100];
        double[] diskon = new double[100];
        double[] pajak = new double[100];
        double[] totalBayar = new double[100];

        String namaPetugas, tanggal;
        int jumlahData;

        double totalPendapatan = 0;

        // HEADER
        System.out.println("CV. MAJU JAYA RENTAL");
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        // INPUT
        System.out.print("Nama Petugas      : ");
        namaPetugas = input.nextLine();

        System.out.print("Tanggal Transaksi : ");
        tanggal = input.nextLine();

        System.out.print("Jumlah Data       : ");
        jumlahData = input.nextInt();

        System.out.println("--------------------------------------------------------");

        // PERULANGAN INPUT
        for (int i = 0; i < jumlahData; i++) {

            System.out.println("\nData Ke-" + (i + 1));

            System.out.print("Kode Mobil        : ");
            kodeMobil[i] = input.next();

            System.out.print("Lama Sewa (Hari)  : ");
            lamaSewa[i] = input.nextInt();

            // PERCABANGAN KODE MOBIL
            if (kodeMobil[i].equalsIgnoreCase("MB001")) {

                namaMobil[i] = "Avanza";
                hargaSewa[i] = 350000;

            } else if (kodeMobil[i].equalsIgnoreCase("MB002")) {

                namaMobil[i] = "Innova";
                hargaSewa[i] = 500000;

            } else if (kodeMobil[i].equalsIgnoreCase("MB003")) {

                namaMobil[i] = "Pajero";
                hargaSewa[i] = 850000;

            } else {

                namaMobil[i] = "Tidak Ada";
                hargaSewa[i] = 0;
            }

            // SUBTOTAL
            subtotal[i] = hargaSewa[i] * lamaSewa[i];

            // DISKON
            if (lamaSewa[i] >= 7) {

                diskon[i] = subtotal[i] * 0.10;

            } else if (lamaSewa[i] >= 3) {

                diskon[i] = subtotal[i] * 0.05;

            } else {

                diskon[i] = 0;
            }

            // PAJAK 11%
            pajak[i] = (subtotal[i] - diskon[i]) * 0.11;

            // TOTAL BAYAR
            totalBayar[i] = subtotal[i] - diskon[i] + pajak[i];

            // TOTAL PENDAPATAN
            totalPendapatan += totalBayar[i];

            System.out.println("--------------------------------------------------------");
        }

        // OUTPUT LAPORAN
        System.out.println("\n================ LAPORAN RENTAL MOBIL =================");

        System.out.println("Nama Petugas : " + namaPetugas);
        System.out.println("Tanggal      : " + tanggal);

        System.out.println("==============================================================================================");
        System.out.println("No  Kode   Nama Mobil   Harga/Hari   Lama   Subtotal   Diskon   Pajak   Total Bayar");
        System.out.println("==============================================================================================");

        // PERULANGAN OUTPUT
        for (int i = 0; i < jumlahData; i++) {

            System.out.printf(
                    "%-3d %-6s %-12s %-12d %-6d %-10.0f %-8.0f %-8.0f %-10.0f\n",
                    (i + 1),
                    kodeMobil[i],
                    namaMobil[i],
                    hargaSewa[i],
                    lamaSewa[i],
                    subtotal[i],
                    diskon[i],
                    pajak[i],
                    totalBayar[i]
            );
        }

        System.out.println("==============================================================================================");

        System.out.println("Total Pendapatan : Rp. " + totalPendapatan);

        System.out.println("==============================================================================================");
    }
}