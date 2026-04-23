import java.util.*;

public class LatIf 
{
    public static void main(String[] args) 
    {
        double tot_beli, potongan = 0, jum_bayar = 0;
        Scanner input = new Scanner(System.in);

        System.out.print("Total Pembelian Rp. ");
        tot_beli = input.nextDouble();

        if (tot_beli >= 50000) 
        {
            potongan = 0.2 * tot_beli;
        }

        System.out.println("Besarnya Potongan Rp. " + potongan);
        jum_bayar = tot_beli - potongan;
        System.out.println("Jumlah yang harus dibayarkan Rp. " + jum_bayar);
    }
}

class IfClass {
    public double TotBeli, potongan;

    public void setTotalBeli (double a) {
        TotBeli = a;
    }

    public double getPotongan() {
        if (TotBeli >= 50000) {
            potongan = 0.2 * TotBeli;
        }
        return potongan;
    }

    public double JumlahBayar ()
    {
        return (TotBeli - potongan);
    }
}

class IfClassAksi {
    public static void main(String[] args)
    {
        double tot_beli;
        Scanner input = new Scanner(System.in);
        IfClass fungsiif = new IfClass();

        System.out.print("Total Pembelian Rp. ");
        fungsiif.TotBeli = input.nextDouble();  

        System.out.println("Besarnya Potongan Rp. " + fungsiif.getPotongan());
        System.out.println("Jumlah yang harus dibayarkan Rp. " + fungsiif.JumlahBayar());
    }
}