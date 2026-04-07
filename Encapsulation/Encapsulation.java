package Encapsulation;

class belajar {
    public String x = "Pintar";
    private String y = "Java";

    // method untuk mengakses private variable
    public String getY() {
        return this.y;
    }
}

public class Encapsulation {

    public static void main(String[] args) {
        belajar panggil = new belajar();
        System.out.println(panggil.x);

        // memanggil private variable
        System.out.println(panggil.getY());
    }
}