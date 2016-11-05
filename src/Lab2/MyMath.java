package Lab2;

import Jama.Matrix;

/**
 * Created by valeriy on 21.10.16.
 */
public class MyMath {

    public static double[][] norma(double[][] array,int columns){

        if (array.length != 0){
            double max;
            double min;
            double[][] res = new double[array.length][columns];
            for (int j=0;j<columns;j++){
                max = array[0][j];
                min = array[0][j];
                for (int i=0;i<array.length;i++){
                    if (array[i][j]>=max)
                        max = array[i][j];
                    if (array[i][j]<=min)
                        min = array[i][j];
                }
                for (int k=0;k<res.length;k++){
                    res[k][j] = (array[k][j]-min)/(max-min);
                }
            }
            return res;
        }
        else {
            return null;
        }
    }

    public static void output(double[][] array,int columns){
        for (int i=0;i<array.length;i++) {
            for (int j=0;j<columns;j++){
                System.out.print(array[i][j]+"          ");
            }
            System.out.println();
        }
    }

    public static Matrix ordLeastSquares(Matrix x, Matrix y){

        Matrix help = x.transpose().times(x);
        try {
            return help.inverse().times(x.transpose()).times(y);
        }catch (RuntimeException e){
            //help.print(help.getColumnDimension(),10);
            //System.out.println(help.det());
            for (int i=0;i<help.getRowDimension();i++){
                for (int j=0;j<help.getColumnDimension();j++){
                    if (i==j){
                        help.set(i,j,help.get(i,j)+1e-4);
                    }

                }
            }
            //help.print(help.getColumnDimension(),10);
            //System.out.println(help.det());
            return help.inverse().times(x.transpose()).times(y);
        }
    }

    public static double maxValue(Matrix x){
        for (int i=0;i<x.getRowDimension();i++){
            for (int j=0;j<x.getColumnDimension();j++){
                x.set(i,j,Math.abs(x.get(i,j)));
            }
        }
        double max = x.get(0,0);
        for (int i=0;i<x.getRowDimension();i++) {
            for (int j=0;j<x.getColumnDimension();j++){
                if (x.get(i,j)>max){
                    max = x.get(i,j);
                }
            }
        }
        return max;
    }

    public static double minValue(Matrix x){
        double min = x.get(0,0);
        for (int i=0;i<x.getRowDimension();i++) {
            for (int j=0;j<x.getColumnDimension();j++){
                if (x.get(i,j)<min){
                    min = x.get(i,j);
                }
            }
        }
        return min;
    }

    public static Matrix coeffC(Matrix x,Matrix y,int powerPolin){
        int power;
        Matrix a = new Matrix(powerPolin+1,powerPolin+1);
        Matrix b = new Matrix(powerPolin+1,1);
        //System.out.println("Rows: "+a.getRowDimension());
        //System.out.println("Columns: "+a.getColumnDimension());
        for (int i=0;i<a.getRowDimension();i++){
            power = i;
            for (int j=0;j<a.getColumnDimension();j++){
                if (power==0) {
                    a.set(i,j,x.getRowDimension());
                }else {
                    double sum = 0.0;
                    for (int k=0;k<x.getRowDimension();k++){
                        sum+=Math.pow(x.get(k,0),power);
                    }
                    a.set(i,j,sum);
                    System.out.println("");
                }
                power++;
            }
        }
        for (int i=0;i<b.getRowDimension();i++){
            power = i;
            double sum = 0.0;
            for (int j=0;j<x.getRowDimension();j++){
                sum+=Math.pow(x.get(j,0),power)*y.get(j,0);
            }
            b.set(i,0,sum);
        }
        return ordLeastSquares(a,b);
    }

}
