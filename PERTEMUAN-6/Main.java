import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        // --- Input Section ---
        System.out.println("PT. PERMATA \"PRATAMA\"");
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
        System.out.print("Masukan Nama Petugas                : ");
        String namaPetugas = input.nextLine();
        System.out.print("Tanggal                             : ");
        String tanggal = input.nextLine();
        
        System.out.println();
        System.out.print("Jumlah Data yang akan di masukan    : ");
        int jumlahData = input.nextInt();
        input.nextLine(); // Clear newline buffer

        System.out.println("---------------------------------------------");

        // Deklarasi Array
        String[] kodeBarang = new String[jumlahData];
        int[] jumlahBarang = new int[jumlahData];
        
        String[] namaBarang = new String[jumlahData];
        int[] hargaBarang = new int[jumlahData];
        int[] totalHarga = new int[jumlahData];

        // Proses Input Data Barang
        for (int i = 0; i < jumlahData; i++) {
            System.out.println("        Data Ke- " + (i + 1));
            System.out.print("        Kode Barang                 : ");
            kodeBarang[i] = input.nextLine();
            System.out.print("        Jumlah                      : ");
            jumlahBarang[i] = input.nextInt();
            input.nextLine(); // Clear newline buffer

            // Proses pencarian Nama Barang dan Harga Barang berdasarkan Kode Barang
            if (kodeBarang[i].equalsIgnoreCase("P001")) {
                namaBarang[i] = "Printer";
                hargaBarang[i] = 700000;
            } else if (kodeBarang[i].equalsIgnoreCase("V001")) {
                namaBarang[i] = "VGA Card";
                hargaBarang[i] = 75000;
            } else if (kodeBarang[i].equalsIgnoreCase("M001")) {
                namaBarang[i] = "Motherboard";
                hargaBarang[i] = 950000;
            } else {
                namaBarang[i] = "Tidak Diketahui";
                hargaBarang[i] = 0;
            }

            // Hitung Total Harga per Barang
            totalHarga[i] = hargaBarang[i] * jumlahBarang[i];
            
            System.out.println("        -------------------------------------");
        }

        // --- Output Section ---
        System.out.println("\n\n");
        System.out.println("PT. PERMATA \"PRATAMA\"");
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.printf("Nama Petugas                : %-20s Tanggal : %s\n", namaPetugas, tanggal);
        System.out.println("Jumlah Data yang di masukan : " + jumlahData);
        System.out.println("------------------------------------------------------------------------------");
        System.out.printf("%-7s %-13s %-17s %-14s %-15s %-14s\n", "Data Ke", "Kode Barang", "Nama Barang", "Harga Barang", "Jumlah Barang", "Total Harga");
        System.out.println("------------------------------------------------------------------------------");

        int totalPendapatan = 0;
        
        // Looping untuk menampilkan data yang sudah disimpan di Array
        for (int i = 0; i < jumlahData; i++) {
            System.out.printf("   %-4d %-13s %-17s Rp. %-10d %-15d Rp. %-10d\n", 
                (i + 1), kodeBarang[i], namaBarang[i], hargaBarang[i], jumlahBarang[i], totalHarga[i]);
            
            // Hitung Total Pendapatan Keseluruhan
            totalPendapatan += totalHarga[i];
        }

        System.out.println("------------------------------------------------------------------------------");
        System.out.println("\nTotal Pendapatan Pada tanggal " + tanggal + " adalah sebesar  Rp. " + totalPendapatan);

        input.close();
    }
}
