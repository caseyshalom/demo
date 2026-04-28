import java.util.Scanner;

public class LatArray2 {
    public static void main(String[] args) {
        int i, j;
        int[][] data_jual;
        data_jual = new int[4][3];
        Scanner input = new Scanner(System.in);

        for (i = 0; i < 3; i++) {
            for (j = 0; j < 3; j++) {
                System.out.print("Masukkan data jual ke [" + i + "] [" + j + "] = ");
                data_jual[i][j] = input.nextInt();
            }
        }

        System.out.println();
        System.out.println("\n\nData jual yang diinput ke elemen array \n");

        for (i = 0; i < 3; i++) {
            for (j = 0; j < 3; j++) {
                System.out.print("Nilai data jual [" + i + "][" + j + "]");
                System.out.println(" = " + data_jual[i][j]);
            }
            System.out.println();
        }
    }
}
