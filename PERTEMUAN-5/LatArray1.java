import java.util.Scanner;

public class LatArray1 {
    public static void main(String[] args) {
        int i;
        int[] nilai_akhir;

        nilai_akhir = new int[6];
        Scanner input = new Scanner(System.in);

        for (i = 0; i < 6; i++) {
            System.out.print("Masukkan array ke " + i + " = ");
            nilai_akhir[i] = input.nextInt();
        }

        System.out.println("\n\nData yang diinput ke elemen array \n");
        for (i = 0; i < 6; i++) {
            System.out.print("Nilai akhir index " + i);
            System.out.println(" = " + nilai_akhir[i]);
        }

    }
}
