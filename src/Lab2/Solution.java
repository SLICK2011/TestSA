package Lab2;

import Jama.Matrix;

/**
 * Created by valeriy on 22.10.16.
 */
public class Solution implements Runnable {

    private Matrix normaX;
    private Matrix normaY;
    private Matrix originalY;
    private int sizeX1;
    private int sizeX2;
    private int sizeX3;
    private int powerX1;
    private int powerX2;
    private int powerX3;
    private Matrix lambda1,lambda2,lambda3;
    private Thread t;
    private int yi;
    private Matrix findForiginal;
    private Matrix findFnorma;
    private int polinomType;
    private boolean flag;
    private double maxError = 100.0;
    private boolean inverseGood = true;


    public double getMaxError() {
        return maxError;
    }

    private String stringInterimResult;

    public Matrix getFindFnorma() {
        return findFnorma;
    }

    public String getStringInterimResult() {
        return stringInterimResult;
    }

    public Matrix getNormaY() {

        return normaY;
    }

    public Matrix getOriginalY() {
        return originalY;
    }

    public Solution(double[][] normaX, double[][] normaY,double[][] originalX, double[][] originalY, int sizeX1, int sizeX2, int sizeX3,
                    int powerX1, int powerX2, int powerX3, int yi, int polinomType,boolean flag){
        t = new Thread(this,"Y"+yi);
        this.normaX = new Matrix(normaX);
        this.sizeX1 = sizeX1;
        this.sizeX2 = sizeX2;
        this.sizeX3 = sizeX3;
        this.powerX1 = powerX1;
        this.powerX2 = powerX2;
        this.powerX3 = powerX3;
        this.polinomType = polinomType;
        this.flag = flag;
        this.yi = yi;
        double[][] help = new double[normaY.length][1];
        for (int i=0;i<normaY.length;i++){
            help[i][0] = normaY[i][yi];
        }
        this.normaY = new Matrix(help);
        this.originalY = new Matrix(originalY.length,1);
        for (int i=0;i<originalY.length;i++){
            this.originalY.set(i,0,originalY[i][yi]);
        }
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Matrix getFindForiginal() {
        return findForiginal;
    }

    @Override
    public void run() {
        double[][] matrixToOls;
        StringBuilder sbForInterimResult = new StringBuilder();
        StringBuilder sbForFinalResult = new StringBuilder();
        sbForFinalResult.append("Ф"+(yi+1)+"(x1,x2,x3) = ");


        //Нахождение лямбда 2 способом (3 системы для каждой лямбда)
        //lambda1
        matrixToOls = new double[normaX.getRowDimension()][sizeX1*(powerX1+1)];
        int interval = powerX1+1;
        int count = 0;
        int currentX = 0;
        for (int i=0;i<matrixToOls.length;i++){
            for (int j=0;j<(powerX1+1)*sizeX1;j++){
                matrixToOls[i][j] = Polinomials.countPolinomValue(polinomType,normaX.get(i,currentX),count);
                count++;
                if (count == interval) {
                    count = 0;
                    currentX++;
                }
            }
            currentX = 0;
        }
        Matrix lambda1X = new Matrix(matrixToOls);
        for (int i=0;i<lambda1X.getRowDimension();i++){
            lambda1X.set(i,0,lambda1X.get(i,0)+1e-7);
        }
        try {
            lambda1 = MyMath.ordLeastSquares(lambda1X,normaY);
        }catch (RuntimeException e){
            e.printStackTrace();
            return;
        }

        count = 0;
        currentX = 1;
        for (int j=0;j<(powerX1+1)*sizeX1;j++){
            count++;
            if (count == interval) {
                count = 0;
                currentX++;
            }
        }
        //lambda2
        matrixToOls = new double[normaX.getRowDimension()][sizeX2*(powerX2+1)];
        interval = powerX2 + 1;
        count = 0;
        currentX = sizeX1;
        for (int i=0;i<matrixToOls.length;i++){
            for (int j=0;j<(powerX2+1)*sizeX2;j++){
                matrixToOls[i][j] = Polinomials.countPolinomValue(polinomType,normaX.get(i,currentX),count);
                count++;
                if (count == interval) {
                    count = 0;
                    currentX++;
                }
            }
            currentX = sizeX1;
        }
        Matrix lambda2X = new Matrix(matrixToOls);
        for (int i=0;i<lambda2X.getRowDimension();i++){
            lambda2X.set(i,0,lambda2X.get(i,0)+1e-7);
        }
        try {
            lambda2 = MyMath.ordLeastSquares(lambda2X,normaY);
        }catch (RuntimeException e){
            e.printStackTrace();
            return;
        }

        count = 0;
        currentX = 1;
        for (int j=0;j<(powerX2+1)*sizeX2;j++){
            count++;
            if (count == interval) {
                count = 0;
                currentX++;
            }
        }
        //lambda3
        matrixToOls = new double[normaX.getRowDimension()][sizeX3*(powerX3+1)];
        interval = powerX3 + 1;
        count = 0;
        currentX = sizeX1+sizeX2;
        for (int i=0;i<matrixToOls.length;i++){
            for (int j=0;j<(powerX3+1)*sizeX3;j++){
                matrixToOls[i][j] = Polinomials.countPolinomValue(polinomType,normaX.get(i,currentX),count);
                count++;
                if (count == interval) {
                    count = 0;
                    currentX++;
                }
            }
            currentX = sizeX1+sizeX2;
        }
        Matrix lambda3X = new Matrix(matrixToOls);
        for (int i=0;i<lambda3X.getRowDimension();i++){
            lambda3X.set(i,0,lambda3X.get(i,0)+1e-7);
            lambda3X.set(i,4,lambda3X.get(i,4)+2e-7);
        }
        try {
            lambda3 = MyMath.ordLeastSquares(lambda3X,normaY);
        }catch (RuntimeException e){
            e.printStackTrace();
            return;
        }


        count = 0;
        currentX = 1;
        for (int j=0;j<(powerX3+1)*sizeX3;j++){
            count++;
            if (count == interval) {
                count = 0;
                currentX++;
            }
        }

        //Нахождение пси
        //1
        double[][] psi1 = new double[normaX.getRowDimension()][sizeX1];
        interval = powerX1 + 1;
        currentX = 0;
        count = 0;
        int currentX2 = 0;
        for (int i=0;i<normaX.getRowDimension();i++){
            for (int j=0;j<(powerX1+1)*sizeX1;j++){
                psi1[i][currentX] += lambda1.get(j,0)*Polinomials.countPolinomValue(polinomType,normaX.get(i,currentX2),count);
                count++;
                if (count == interval) {
                    count = 0;
                    currentX++;
                    currentX2++;
                }
            }
            currentX = 0;
            currentX2 = 0;
        }

        //2
        double[][] psi2 = new double[normaX.getRowDimension()][sizeX2];
        interval = powerX2 + 1;
        currentX = 0;
        count = 0;
        currentX2 = sizeX1;
        for (int i=0;i<normaX.getRowDimension();i++){
            for (int j=0;j<(powerX2+1)*sizeX2;j++){
                psi2[i][currentX] += lambda2.get(j,0)*Polinomials.countPolinomValue(polinomType,normaX.get(i,currentX2),count);
                count++;
                if (count == interval) {
                    count = 0;
                    currentX++;
                    currentX2++;
                }
            }
            currentX = 0;
            currentX2 = sizeX1;
        }

        //3
        double[][] psi3 = new double[normaX.getRowDimension()][sizeX3];
        interval = powerX3 + 1;
        currentX = 0;
        count = 0;
        currentX2 = sizeX1+sizeX2;
        for (int i=0;i<normaX.getRowDimension();i++){
            for (int j=0;j<(powerX3+1)*sizeX3;j++){
                psi3[i][currentX] += lambda3.get(j,0)*Polinomials.countPolinomValue(polinomType,normaX.get(i,currentX2),count);
                count++;
                if (count == interval) {
                    count = 0;
                    currentX++;
                    currentX2++;
                }
            }
            currentX = 0;
            currentX2 = sizeX1+sizeX2;
        }

        //Нахождение матриц а

        //1
        Matrix a1;
        Matrix matrixPsi1 = new Matrix(psi1);
        try {
            a1 = MyMath.ordLeastSquares(matrixPsi1,normaY);
        }catch (RuntimeException e){
            e.printStackTrace();
            return;
        }


        //2
        Matrix a2;
        Matrix matrixPsi2 = new Matrix(psi2);
        try {
            a2 = MyMath.ordLeastSquares(matrixPsi2,normaY);
        }catch (RuntimeException e){
            return;
        }


        //3
        Matrix a3;
        Matrix matrixPsi3 = new Matrix(psi3);
        try {
            a3 = MyMath.ordLeastSquares(matrixPsi3,normaY);
        }catch (RuntimeException e){
            e.printStackTrace();
            return;
        }

        //Нахождение Ф

        double[][] allF = new double[normaX.getRowDimension()][3];
        //1
        double[] f1 = new double[normaX.getRowDimension()];
        for (int i=0;i<normaX.getRowDimension();i++){
            for (int j=0;j<a1.getRowDimension();j++){
                f1[i] += a1.get(j,0)*matrixPsi1.get(i,j);
            }
        }
        for (int k=0;k<normaX.getRowDimension();k++)
            allF[k][0] = f1[k];
        Matrix matrixf1 = new Matrix(f1,normaX.getRowDimension());

        //2
        double[] f2 = new double[normaX.getRowDimension()];
        for (int i=0;i<normaX.getRowDimension();i++){
            for (int j=0;j<a2.getRowDimension();j++){
                f2[i] += a2.get(j,0)*matrixPsi2.get(i,j);
            }
        }
        for (int k=0;k<normaX.getRowDimension();k++)
            allF[k][1] = f2[k];
        Matrix matrixf2 = new Matrix(f2,normaX.getRowDimension());

        //3
        double[] f3 = new double[normaX.getRowDimension()];
        for (int i=0;i<normaX.getRowDimension();i++){
            for (int j=0;j<a3.getRowDimension();j++){
                f3[i] += a3.get(j,0)*matrixPsi3.get(i,j);
            }
        }
        for (int k=0;k<normaX.getRowDimension();k++)
            allF[k][2] = f3[k];
        Matrix matrixf3 = new Matrix(f3,normaX.getRowDimension());

        //Нахождение матрицы с
        //1
        Matrix c;
        Matrix matrixAllF = new Matrix(allF);
        try {
            c = MyMath.ordLeastSquares(matrixAllF,normaY);
        }catch (RuntimeException e){
            e.printStackTrace();
            return;
        }

        Matrix yiq1 = new Matrix(matrixPsi1.getRowDimension(),1);
        for (int i=0;i<matrixPsi1.getRowDimension();i++){
            double sum = 0.0;
            for (int j=0;j<matrixPsi1.getColumnDimension();j++){
                sum+=a1.get(j,0)*matrixPsi1.get(i,j);
            }
            yiq1.set(i,0,sum);
        }
        Matrix yiq2 = new Matrix(matrixPsi2.getRowDimension(),1);
        for (int i=0;i<matrixPsi2.getRowDimension();i++){
            double sum = 0.0;
            for (int j=0;j<matrixPsi2.getColumnDimension();j++){
                sum+=a2.get(j,0)*matrixPsi2.get(i,j);
            }
            yiq2.set(i,0,sum);
        }
        Matrix yiq3 = new Matrix(matrixPsi3.getRowDimension(),1);
        for (int i=0;i<matrixPsi3.getRowDimension();i++){
            double sum = 0.0;
            for (int j=0;j<matrixPsi3.getColumnDimension();j++){
                sum+=a3.get(j,0)*matrixPsi3.get(i,j);
            }
            yiq3.set(i,0,sum);
        }
        Matrix findFiNorma = new Matrix(yiq1.getRowDimension(),1);
        Matrix findFi = new Matrix(yiq1.getRowDimension(),1);
        for (int i=0;i<findFiNorma.getRowDimension();i++){
            findFiNorma.set(i,0,c.get(0,0)*yiq1.get(i,0)+c.get(1,0)*yiq2.get(i,0)+c.get(2,0)*yiq3.get(i,0));
        }
        for (int i=0;i<findFi.getRowDimension();i++){
            findFi.set(i,0,findFiNorma.get(i,0)*(MyMath.maxValue(originalY)-MyMath.minValue(originalY))+ MyMath.minValue(originalY));
        }
        findFnorma = findFiNorma;
        findForiginal = findFi;

        //Вывод
        if (flag&&a1!=null&&a2!=null&&a3!=null&&c!=null){
                        sbForInterimResult.append("\n\nLambda1");
            for (int i=0;i<lambda1.getRowDimension();i++){
                sbForInterimResult.append("\n"+lambda1.get(i,0));
            }
            sbForInterimResult.append("\n\nLambda2");
            for (int i=0;i<lambda2.getRowDimension();i++){
                sbForInterimResult.append("\n"+lambda2.get(i,0));
            }
            sbForInterimResult.append("\n\nLambda3");
            for (int i=0;i<lambda3.getRowDimension();i++){
                sbForInterimResult.append("\n"+lambda3.get(i,0));
            }
            sbForInterimResult.append("\n\npsi1:\n");
            for (int i=0;i<matrixPsi1.getRowDimension();i++){
                for (int j=0;j<matrixPsi1.getColumnDimension();j++){
                    sbForInterimResult.append(matrixPsi1.get(i,j)+"         ");
                }
                sbForInterimResult.append("\n");
            }
            sbForInterimResult.append("\n\npsi2:\n");
            for (int i=0;i<matrixPsi2.getRowDimension();i++){
                for (int j=0;j<matrixPsi2.getColumnDimension();j++){
                    sbForInterimResult.append(matrixPsi2.get(i,j)+"         ");
                }
                sbForInterimResult.append("\n");
            }
            sbForInterimResult.append("\n\npsi3:\n");
            for (int i=0;i<matrixPsi3.getRowDimension();i++){
                for (int j=0;j<matrixPsi3.getColumnDimension();j++){
                    sbForInterimResult.append(matrixPsi3.get(i,j)+"         ");
                }
                sbForInterimResult.append("\n");
            }
            sbForInterimResult.append("\n\n||a1||\n");
            for (int i=0;i<a1.getRowDimension();i++){
                sbForInterimResult.append(a1.get(i,0)+"     ");
            }
            sbForInterimResult.append("\n\n||a2||\n");
            for (int i=0;i<a2.getRowDimension();i++){
                sbForInterimResult.append(a2.get(i,0)+"     ");
            }
            sbForInterimResult.append("\n\n||a3||\n");
            for (int i=0;i<a3.getRowDimension();i++){
                sbForInterimResult.append(a3.get(i,0)+"     ");
            }
            sbForInterimResult.append("\n\nФ1------------------------------------------Ф2------------------------------------------Ф3\n");
            for (int i=0;i<matrixAllF.getRowDimension();i++){
                for (int j=0;j<matrixAllF.getColumnDimension();j++){
                    sbForInterimResult.append(matrixAllF.get(i,j)+"         ");
                }
                sbForInterimResult.append("\n");
            }
            sbForInterimResult.append("\n\n||c||\n");
            for (int i=0;i<c.getRowDimension();i++){
                sbForInterimResult.append(c.get(i,0)+"      ");
            }
            sbForInterimResult.append("\n---------------------------------------------------------------------------------------------------------");
            sbForInterimResult.append("\n\nФ"+(yi+1)+"(x1,x2,x3) = "+c.get(0,0)+"Ф1'(x1'[q]) + " + c.get(1,0)+ "Ф2'(x2'[q]) + " +c.get(2,0)+"Ф3'(x3'[q])");
            sbForInterimResult.append("\n\nФ1'(x1'[q]) = ");
            for (int i=0;i<a1.getRowDimension();i++){
                if (i==a1.getRowDimension()-1)
                    sbForInterimResult.append(a1.get(i,0)+"psi1(x1"+i+"[q])");
                else
                    sbForInterimResult.append(a1.get(i,0)+"psi1(x1"+i+"[q]) + ");
            }
            sbForInterimResult.append("\nФ2'(x2'[q]) = ");
            for (int i=0;i<a2.getRowDimension();i++){
                if (i==a2.getRowDimension()-1)
                    sbForInterimResult.append(a2.get(i,0)+"psi2(x2"+i+"[q])");
                else
                    sbForInterimResult.append(a2.get(i,0)+"psi2(x2"+i+"[q]) + ");
            }
            sbForInterimResult.append("\nФ3'(x3'[q]) = ");
            for (int i=0;i<a3.getRowDimension();i++){
                if (i==a3.getRowDimension()-1)
                    sbForInterimResult.append(a3.get(i,0)+"psi3(x3"+i+"[q])");
                else
                    sbForInterimResult.append(a3.get(i,0)+"psi3(x3"+i+"[q]) + ");
            }

            sbForInterimResult.append("\n\npsi1(x1'[q]) = ");
            int k=1;
            int power = 0;
            for (int i=0;i<k*(powerX1+1);i++){
                if (i<lambda1.getRowDimension()-1) {
                    sbForInterimResult.append(lambda1.get(i, 0) + "T"+(power++)+"'(x1" + (k - 1) + ") + ");
                }
                else
                    sbForInterimResult.append(lambda1.get(i,0)+"T"+(power++)+"'(x1"+(k-1)+")");
                if ((i==k*(powerX1+1)-1)&&(i!=lambda1.getRowDimension()-1)) {
                    k++;
                    power = 0;
                    sbForInterimResult.append("\n");
                }
            }
            sbForInterimResult.append("\n\npsi2(x2'[q]) = ");
            k=1;
            power = 0;
            for (int i=0;i<k*(powerX2+1);i++){
                if (i<lambda2.getRowDimension()-1) {
                    sbForInterimResult.append(lambda2.get(i, 0) + "T"+(power++)+"'(x2" + (k - 1) + ") + ");
                }
                else
                    sbForInterimResult.append(lambda2.get(i,0)+"T"+(power++)+"'(x2"+(k-1)+")");
                if ((i==k*(powerX2+1)-1)&&(i!=lambda2.getRowDimension()-1)) {
                    k++;
                    power = 0;
                    sbForInterimResult.append("\n");
                }
            }
            sbForInterimResult.append("\n\npsi3(x3'[q]) = ");
            k=1;
            power = 0;
            for (int i=0;i<k*(powerX3+1);i++){
                if (i<lambda3.getRowDimension()-1) {
                    sbForInterimResult.append(lambda3.get(i, 0) + "T"+(power++)+"'(x3" + (k - 1) + ") + ");
                }
                else
                    sbForInterimResult.append(lambda3.get(i,0)+"T"+(power++)+"'(x3"+(k-1)+")");
                if ((i==k*(powerX3+1)-1)&&(i!=lambda3.getRowDimension()-1)) {
                    k++;
                    power = 0;
                    sbForInterimResult.append("\n");
                }
            }
            sbForInterimResult.append("\n---------------------------------------------------------------------------------------------------------");

            String type = "";
            switch (polinomType){
                case 1: type = "Чебишева";
                        break;
                case 2: type = "Лежандра";
                        break;
                case 3: type = "Лагера";
                        break;
                case 4: type = "Ерміта";
                        break;
            }
            sbForInterimResult.append("\n\nФункція виражена через поліноми "+type+":\nФ"+(yi+1)+"(x1,x2,x3) = ");
            count = 0;
            currentX = 1;
            interval = powerX1 + 1;
            for (int i=0;i<lambda1.getRowDimension();i++){
                sbForInterimResult.append("("+lambda1.get(i,0)+")"+"T"+count+"(x1"+currentX+") + ");
                count++;
                if (count == interval) {
                    count = 0;
                    currentX++;
                    sbForInterimResult.append("\n");
                }
            }
            count = 0;
            currentX = 1;
            interval = powerX2 + 1;
            for (int i=0;i<lambda2.getRowDimension();i++){
                sbForInterimResult.append(" + ("+lambda2.get(i,0)+")"+"T"+count+"(x2"+currentX+")");
                count++;
                if (count == interval) {
                    count = 0;
                    currentX++;
                    sbForInterimResult.append("\n");
                }
            }
            count = 0;
            currentX = 1;
            interval = powerX3 + 1;
            for (int i=0;i<lambda3.getRowDimension();i++){
                sbForInterimResult.append(" + ("+lambda3.get(i,0)+")"+"T"+count+"(x3"+currentX+")");
                count++;
                if (count == interval) {
                    count = 0;
                    currentX++;
                    sbForInterimResult.append("\n");
                }
            }
            sbForInterimResult.append("\n\n[Оригінальний у  |   Знайдений у |   Нев'зка |   Оригінальний нормований у   |   Знайденний нормований у |   Нев'язка]\n");
            for (int i=0;i<findForiginal.getRowDimension();i++){
                sbForInterimResult.append(originalY.get(i,0)+"  |   "+findForiginal.get(i,0)+"  |   "+Math.abs(originalY.get(i,0)-findForiginal.get(i,0))+
                        "   |   "+normaY.get(i,0)+"  |   "+findFnorma.get(i,0)+"  |   "+Math.abs(normaY.get(i,0)-findFnorma.get(i,0)));
                sbForInterimResult.append("\n");
            }
            sbForInterimResult.append("\nМаксимальна похибка: "+MyMath.maxValue(originalY.minus(findForiginal)));
            sbForInterimResult.append("\nМаксимальна похибка(нормована): "+MyMath.maxValue(normaY.minus(findFiNorma)));
            stringInterimResult = sbForInterimResult.toString();
        }
        Matrix errors = originalY.minus(findForiginal);
        maxError = MyMath.maxValue(errors);
    }
}
