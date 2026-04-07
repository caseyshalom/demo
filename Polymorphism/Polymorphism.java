package Polymorphism;

class hewan {
    public void suara() {
        System.out.println("Hewan bersuara : ");
    }
}

class anjing extends hewan {
    public void suara() {
        System.out.println("Guk.. Guk.. Guk!");
    }
}

class kucing extends hewan {
    public void suara() {
        System.out.println("Meong.. Meong.. Meong!");
    }
}

public class Polymorphism {
    public static void main(String[] args) {
        hewan hewan = new hewan();
        anjing anjing = new anjing();
        kucing kucing = new kucing();

        // memanggil superclass dan subclass
        hewan.suara();
        anjing.suara();
        kucing.suara();
    }
}