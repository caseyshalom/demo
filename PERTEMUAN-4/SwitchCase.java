import java.util.Scanner;

public class Switchcase{
    public static void main(String[] args){
    int kd; 
        String nb; 
        Scanner input=new Scanner(System.in); 

        System.out.print("masukan kode barang[1..3]:"); 
        kd=input.nextInt(); 

        switch(kd) 
        { 
            case 1: 
                nb="Alat Olah raga"; 
                break; 
            case 2: 
                nb="Alat Elektronik"; 
                break; 
            case 3: 
                nb="Alat Alat Masak"; 
                break; 
            default: 
                nb=" Anda Salah Kode !!!"; 
                break; 
        } 

        System.out.println("Nama Barang :"  +nb); 
}
}