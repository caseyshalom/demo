public class ArrayDim2 {
    public static void main(String[] args) {
        int i, j;
        int[][] nilai_akhir = { { 150, 159, 230 }, { 100, 125, 150 }, { 210, 125, 156 } };

        System.out.println("Data yang diinput ke elemen array \n");

        for (i = 0; i < 3; i++) {
            for (j = 0; j < 3; j++) {
                System.out.print("Nilai akhir index [" + i + "] [" + j + "]");
                System.out.println(" = " + nilai_akhir[i][j]);
            }
        }
    }
}
