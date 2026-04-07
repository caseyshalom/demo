class Mahasiswa {
    private String nama;
    private String nim;

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNama() {
        return this.nama;
    }

    public void setNIM(String nim) {
        this.nim = nim;
    }

    public String getNIM() {
        return this.nim;
    }
}

public class Main {
    public static void main(String[] args) {
        // Membuat objek dari class Mahasiswa
        Mahasiswa mhs = new Mahasiswa();

        // Memasukkan data mahasiswa menggunakan method setter
        mhs.setNama("Budi");
        mhs.setNIM("123456");

        // Menampilkan data mahasiswa menggunakan method getter
        System.out.println("Data Mahasiswa:");
        System.out.println("Nama : " + mhs.getNama());
        System.out.println("NIM  : " + mhs.getNIM());
    }
}
