import java.util.Scanner;

public class LatIfElse {
    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        String keterangan, nama;
        int nilai;

        System.out.print("Masukkan Nama Anda : ");
        nama = input.nextLine();

        System.out.print("Masukkan Nilai Akhir : ");
        nilai = input.nextInt();
        if (nilai > 70) {
            keterangan = "Lulus";
        } else {
            keterangan = "Tidak Lulus";
        }
        System.out.println("Nama: " + nama);
        System.out.println("Nilai: " + nilai);
        System.out.println("Keterangan: " + keterangan);
    }
}

class IfElseClass {
    public String nama, keterangan;
    public int nilaiAkhir;
    Scanner input = new Scanner(System.in);

    public void setInputData() {
        System.out.print("Masukkan Nama Anda : ");
        nama = input.nextLine();

        System.out.print("Masukkan Nilai Akhir : ");
        nilaiAkhir = input.nextInt();
    }

    public string getKeterangan() {
        if (nilaiAkhir > 70) {
            keterangan = "Lulus";
        } else {
            keterangan = "Tidak Lulus";
        }
        return keterangan;
    }
}

class IfElseAksi {
    public static void main(String[] args) {

        IfElseClass ifclass = new IfElseClass();

        ifclass.setInputData();
        ifclass.getKeterangan();
        System.out.println("Nama: " + ifclass.nama);
        System.out.println("Nilai: " + ifclass.nilaiAkhir);
        System.out.println("Keterangan: " + ifclass.getKeterangan());
    }
}