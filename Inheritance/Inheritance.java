package Inheritance;

//Superclass
class Kendaraan {
    String merk = "BYD";

    void Klakson() {
        System.out.println("Teeet... Teeet...");
    }
}

// Subclass
class Mobil extends Kendaraan {
    String warna = "Merah";
}

// Class Utama untuk memanggil program
public class Inheritance {
    public static void main(String[] args) {
        // membuat objek dari subclass
        Mobil mobil = new Mobil();

        // memanggil method dari superclass
        mobil.Klakson();

        System.out.println("Merk Mobil : " + mobil.merk);
        System.out.println("Warna Mobil : " + mobil.warna);

    }
}
