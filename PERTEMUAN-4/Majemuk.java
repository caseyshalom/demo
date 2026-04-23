import java.util.Scanner;

public class Majemuk {
    public static void main(String[] args) {
        int pendapatan;
        double jasa, komisi, total;

        Scanner input = new Scanner(System.in);

        System.out.println("masukan pendapatan = ");
        pendapatan = input.nextInt();

        if(pendapatan >= 0 && pendapatan <= 200000){
            jasa = 10000;
            komisi = 0.1 * pendapatan;
        }
        else if(pendapatan <= 500000){
            jasa = 20000;
            komisi = 0.15 * pendapatan;
        }
        else{
            jasa = 30000;
            komisi = 0.2 * pendapatan;
        }

        total = komisi + jasa;

        System.out.println("uang jasa   = " + jasa);
        System.out.println("uang komisi = " + komisi);
        System.out.println("=============================");
        System.out.println("uang total = " + total);
    }
}