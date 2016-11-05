package Lab2;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by valeriy on 21.10.16.
 */
public class Files {

    /**
     *
     * @param f
     * @param columns
     * @return
     */
    public static double[][] parseFile(File f, int columns){
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(f.getAbsolutePath()));
            double [][] array;
            int symbol = bufferedReader.read();
            StringBuffer sb = new StringBuffer();
            ArrayList<Double> list = new ArrayList<>();

            while (symbol != -1) {
                if((symbol != 9)&&(symbol != 13)&&(symbol != 10)) {
                    sb.append((char) symbol);
                }
                else{
                    if (!sb.toString().equals("")) {
                        list.add(Double.parseDouble(sb.toString()));
                        sb = new StringBuffer();
                    }
                }
                symbol = bufferedReader.read();
            }
            array = new double[list.size()/columns][columns];
            int k=0;
            for (int i=0;i<array.length;i++){
                for (int j=0;j<columns;j++){
                    array[i][j]=list.get(k++);
                }
            }
            return array;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}
