import java.io.*;

public class BufferedReaderInput {
    public static void main(String[] args) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        String nama_barang;
        int jumlah;

        System.out.print("Masukkan Nama Barang: ");
        nama_barang = input.readLine();

        System.out.print("Masukkan Jumlah Barang: ");
        String inputJumlah = input.readLine();
        jumlah = Integer.parseInt(inputJumlah);

        System.out.println("\n--- Detail Barang ---");
        System.out.println("Nama Barang  : " + nama_barang);
        System.out.println("Jumlah Barang: " + jumlah);
    }
}
