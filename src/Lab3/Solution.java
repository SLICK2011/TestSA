package Lab3;

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
        int maxPowerSize;
        if ((powerX1*sizeX1>=powerX2*sizeX2)&&(powerX1*sizeX1>=powerX3*sizeX3))
            maxPowerSize = (powerX1+1)*sizeX1;
        else if ((powerX2*sizeX2>=powerX1*sizeX1)&&(powerX2*sizeX2>=powerX3*sizeX3))
            maxPowerSize = (powerX2+1)*sizeX2;
        else
            maxPowerSize = (powerX3+1)*sizeX3;

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
                matrixToOls[i][j] = Math.log(1.0+Polinomials.countPolinomValue(polinomType,normaX.get(i,currentX),count));
                count++;
                if (count == interval) {
                    count = 0;
                    currentX++;
                }
            }
            currentX = 0;
        }
        Matrix lambdaX = new Matrix(matrixToOls);
        Matrix lambdaY = new Matrix(normaY.getRowDimension(),normaY.getColumnDimension());
        for (int i=0;i<lambdaY.getRowDimension();i++){
            lambdaY.set(i,0,Math.log(normaY.get(i,0)+1.0));
        }
        try {
            lambda1 = MyMath.ordLeastSquares(lambdaX,lambdaY);
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
                matrixToOls[i][j] = Math.log(1+Polinomials.countPolinomValue(polinomType,normaX.get(i,currentX),count));
                count++;
                if (count == interval) {
                    count = 0;
                    currentX++;
                }
            }
            currentX = sizeX1;
        }
        lambdaX = new Matrix(matrixToOls);
        try {
            lambda2 = MyMath.ordLeastSquares(lambdaX,lambdaY);
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
                matrixToOls[i][j] = Math.log(1+Polinomials.countPolinomValue(polinomType,normaX.get(i,currentX),count));
                count++;
                if (count == interval) {
                    count = 0;
                    currentX++;
                }
            }
            currentX = sizeX1+sizeX2;
        }
        lambdaX = new Matrix(matrixToOls);
        try {
            lambda3 = MyMath.ordLeastSquares(lambdaX,lambdaY);
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
        double powerExp = 0.0;

        for (int i=0;i<normaX.getRowDimension();i++){
            for (int j=0;j<(powerX1+1)*sizeX1;j++){
                powerExp += lambda1.get(j,0)*Math.log(1.0+Polinomials.countPolinomValue(polinomType,normaX.get(i,currentX2),count));
                count++;
                if (count == interval) {
                    psi1[i][currentX] = Math.pow(Math.E,powerExp)-1.0;
                    count = 0;
                    currentX++;
                    currentX2++;
                    powerExp = 0.0;
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
        powerExp = 0.0;
        for (int i=0;i<normaX.getRowDimension();i++){
            for (int j=0;j<(powerX2+1)*sizeX2;j++){
                powerExp += lambda2.get(j,0)*Math.log(1.0+Polinomials.countPolinomValue(polinomType,normaX.get(i,currentX2),count));
                count++;
                if (count == interval) {
                    psi2[i][currentX] = Math.pow(Math.E,powerExp)-1.0;
                    count = 0;
                    currentX++;
                    currentX2++;
                    powerExp = 0.0;
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
        powerExp = 0.0;
        for (int i=0;i<normaX.getRowDimension();i++){
            for (int j=0;j<(powerX3+1)*sizeX3;j++){
                powerExp +=lambda3.get(j,0)*Math.log(1.0+Polinomials.countPolinomValue(polinomType,normaX.get(i,currentX2),count));
                count++;
                if (count == interval) {
                    psi3[i][currentX] = Math.pow(Math.E,powerExp)-1.0;
                    count = 0;
                    currentX++;
                    currentX2++;
                    powerExp = 0.0;
                }
            }
            currentX = 0;
            currentX2 = sizeX1+sizeX2;
        }

        //Нахождение матриц а

        //1
        Matrix a1;
        Matrix matrixPsi1 = new Matrix(psi1);
        Matrix modifyPSI = new Matrix(matrixPsi1.getRowDimension(),matrixPsi1.getColumnDimension());
        for (int i=0;i<modifyPSI.getRowDimension();i++){
            for (int j=0;j<modifyPSI.getColumnDimension();j++){
                modifyPSI.set(i,j,Math.log(1.0+matrixPsi1.get(i,j)));
            }
        }
        try {
            a1 = MyMath.ordLeastSquares(modifyPSI,lambdaY);
        }catch (RuntimeException e){
            e.printStackTrace();
            return;
        }


        //2
        Matrix a2;
        Matrix matrixPsi2 = new Matrix(psi2);
        modifyPSI = new Matrix(matrixPsi2.getRowDimension(),matrixPsi2.getColumnDimension());
        for (int i=0;i<modifyPSI.getRowDimension();i++){
            for (int j=0;j<modifyPSI.getColumnDimension();j++){
                modifyPSI.set(i,j,Math.log(1.0+matrixPsi2.get(i,j)));
            }
        }
        try {
            a2 = MyMath.ordLeastSquares(modifyPSI,lambdaY);
        }catch (RuntimeException e){
            return;
        }


        //3
        Matrix a3;
        Matrix matrixPsi3 = new Matrix(psi3);
        modifyPSI = new Matrix(matrixPsi3.getRowDimension(),matrixPsi3.getColumnDimension());
        for (int i=0;i<modifyPSI.getRowDimension();i++){
            for (int j=0;j<modifyPSI.getColumnDimension();j++){
                modifyPSI.set(i,j,Math.log(1.0+matrixPsi3.get(i,j)));
            }
        }
        try {
            a3 = MyMath.ordLeastSquares(modifyPSI,lambdaY);
        }catch (RuntimeException e){
            e.printStackTrace();
            return;
        }

        //Нахождение Ф

        double[][] allF = new double[normaX.getRowDimension()][3];
        //1

        double[] f1 = new double[normaX.getRowDimension()];
        powerExp = 0.0;
        for (int i=0;i<normaX.getRowDimension();i++){
            for (int j=0;j<a1.getRowDimension();j++){
                //f1[i] += a1.get(j,0)*matrixPsi1.get(i,j);
                powerExp += a1.get(j,0)*Math.log(1.0+matrixPsi1.get(i,j));
            }
            f1[i] = Math.pow(Math.E,powerExp)-1.0;
            powerExp = 0.0;
        }
        for (int k=0;k<normaX.getRowDimension();k++)
            allF[k][0] = f1[k];
        Matrix matrixf1 = new Matrix(f1,normaX.getRowDimension());

        //2
        double[] f2 = new double[normaX.getRowDimension()];
        powerExp = 0.0;
        for (int i=0;i<normaX.getRowDimension();i++){
            for (int j=0;j<a2.getRowDimension();j++){
                //f2[i] += a2.get(j,0)*matrixPsi2.get(i,j);
                powerExp += a2.get(j,0)*Math.log(1.0+matrixPsi2.get(i,j));
            }
            f2[i] = Math.pow(Math.E,powerExp)-1.0;
            powerExp = 0.0;
        }
        for (int k=0;k<normaX.getRowDimension();k++)
            allF[k][1] = f2[k];
        Matrix matrixf2 = new Matrix(f2,normaX.getRowDimension());

        //3
        double[] f3 = new double[normaX.getRowDimension()];
        powerExp = 0.0;
        for (int i=0;i<normaX.getRowDimension();i++){
            for (int j=0;j<a3.getRowDimension();j++){
                //f3[i] += a3.get(j,0)*matrixPsi3.get(i,j);
                powerExp += a3.get(j,0)*Math.log(1.0+matrixPsi3.get(i,j));
            }
            f3[i] = Math.pow(Math.E,powerExp)-1.0;
            powerExp = 0.0;
        }
        for (int k=0;k<normaX.getRowDimension();k++)
            allF[k][2] = f3[k];
        Matrix matrixf3 = new Matrix(f3,normaX.getRowDimension());

        //Нахождение матрицы с
        //1
        Matrix c;
        Matrix matrixAllF = new Matrix(allF);
        Matrix matrixAllModify = new Matrix(matrixAllF.getRowDimension(),matrixAllF.getColumnDimension());
        for (int i=0;i<matrixAllModify.getRowDimension();i++){
            for (int j=0;j<matrixAllModify.getColumnDimension();j++){
                matrixAllModify.set(i,j,Math.log(matrixAllF.get(i,j)+1.0));
            }
        }
        try {
            c = MyMath.ordLeastSquares(matrixAllModify,lambdaY);
        }catch (RuntimeException e){
            e.printStackTrace();
            return;
        }

        findFnorma = new Matrix(matrixAllF.getRowDimension(),1);
        powerExp = 0.0;
        for (int i=0;i<matrixAllF.getRowDimension();i++){
            for (int j=0;j<c.getRowDimension();j++){
               powerExp += c.get(j,0)*Math.log(1.0+matrixAllF.get(i,j));
            }
            findFnorma.set(i,0,Math.pow(Math.E,powerExp)-1.0);
            powerExp = 0.0;
        }
        findForiginal = new Matrix(matrixAllF.getRowDimension(),1);

        for (int i=0;i<findForiginal.getRowDimension();i++){
            findForiginal.set(i,0,findFnorma.get(i,0)*(MyMath.maxValue(originalY)-MyMath.minValue(originalY))+ MyMath.minValue(originalY));
        }

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
            //Формируем "ПСИ" на вывод
            //1
            sbForInterimResult.append("\n\nТретій рівень:");
            int slip = 0;
            sbForInterimResult.append("\n");
            for (int j=0;j<matrixPsi1.getColumnDimension();j++){
                sbForInterimResult.append("\n[1+psi1"+(j+1)+"(x1"+(j+1)+")] = ");
                for (int i=0;i<powerX1+1;i++){
                    if (i!=powerX1)
                        sbForInterimResult.append("[1+FI"+i+j+"(x1"+(j+1)+")]^("+lambda1.get(slip+i,0)+")+");
                    else
                        sbForInterimResult.append("[1+FI"+i+j+"(x1"+(j+1)+")]^("+lambda1.get(slip+i,0)+")");
                }
                slip += powerX1+1;
            }
            //2
            slip = 0;
            sbForInterimResult.append("\n");
            for (int j=0;j<matrixPsi2.getColumnDimension();j++){
                sbForInterimResult.append("\n[1+psi2"+(j+1)+"(x2"+(j+1)+")] = ");
                for (int i=0;i<powerX2+1;i++){
                    if (i!=powerX2)
                        sbForInterimResult.append("[1+FI"+i+j+"(x2"+(j+1)+")]^("+lambda2.get(slip+i,0)+")+");
                    else
                        sbForInterimResult.append("[1+FI"+i+j+"(x2"+(j+1)+")]^("+lambda2.get(slip+i,0)+")");
                }
                slip += powerX2+1;
            }
            //3
            slip = 0;
            sbForInterimResult.append("\n");
            for (int j=0;j<matrixPsi3.getColumnDimension();j++){
                sbForInterimResult.append("\n[1+psi3"+(j+1)+"(x3"+(j+1)+")] = ");
                for (int i=0;i<powerX3+1;i++){
                    if (i!=powerX3)
                        sbForInterimResult.append("[1+FI"+i+j+"(x3"+(j+1)+")]^("+lambda3.get(slip+i,0)+")+");
                    else
                        sbForInterimResult.append("[1+FI"+i+j+"(x3"+(j+1)+")]^("+lambda3.get(slip+i,0)+")");
                }
                slip += powerX3+1;
            }

            //Формируем "Фik" на вывод
            //1
            sbForInterimResult.append("\n\nДругий рівень:");
            sbForInterimResult.append("\n");
            sbForInterimResult.append("\n[1+Ф1'"+"(x1)] = ");
            for (int i=0;i<a1.getRowDimension();i++){
                if (i!=a1.getRowDimension()-1)
                    sbForInterimResult.append("[1+psi1"+(i+1)+"(x1"+(i+1)+")]^("+a1.get(i,0)+")+");
                else
                    sbForInterimResult.append("[1+psi1"+(i+1)+"(x1"+(i+1)+")]^("+a1.get(i,0)+")");
            }
            //2
            sbForInterimResult.append("\n[1+Ф2'"+"(x2)] = ");
            for (int i=0;i<a2.getRowDimension();i++){
                if (i!=a2.getRowDimension()-1)
                    sbForInterimResult.append("[1+psi2"+(i+1)+"(x2"+(i+1)+")]^("+a2.get(i,0)+")+");
                else
                    sbForInterimResult.append("[1+psi2"+(i+1)+"(x2"+(i+1)+")]^("+a2.get(i,0)+")");
            }
            //3
            sbForInterimResult.append("\n[1+Ф3'"+"(x3)] = ");
            for (int i=0;i<a3.getRowDimension();i++){
                if (i!=a3.getRowDimension()-1)
                    sbForInterimResult.append("[1+psi3"+(i+1)+"(x3"+(i+1)+")]^("+a3.get(i,0)+")+");
                else
                    sbForInterimResult.append("[1+psi3"+(i+1)+"(x3"+(i+1)+")]^("+a3.get(i,0)+")");
            }

            //Формируем Ф на вывод
            sbForInterimResult.append("\n\nПерший рівень:");
            sbForInterimResult.append("\n\n[1+Ф(x)] = ");
            for (int i=0;i<c.getRowDimension();i++){
                if (i!=c.getRowDimension()-1)
                    sbForInterimResult.append("[1+Ф"+(i+1)+"'(x"+(i+1)+")]^("+(c.get(i,0))+")+");
                else
                    sbForInterimResult.append("[1+Ф"+(i+1)+"'(x"+(i+1)+")]^("+(c.get(i,0))+")");
            }

            //Формировка оценки качества модели
            sbForInterimResult.append("\n---------------------------------------------------------------------------------------------------------");
            sbForInterimResult.append("\n\n[Оригінальний у  |   Знайдений у |   Нев'зка |   Оригінальний нормований у   |   Знайденний нормований у |   Нев'язка]\n");
            for (int i=0;i<findForiginal.getRowDimension();i++){
                sbForInterimResult.append(originalY.get(i,0)+"  |   "+findForiginal.get(i,0)+"  |   "+Math.abs(originalY.get(i,0)-findForiginal.get(i,0))+
                        "   |   "+normaY.get(i,0)+"  |   "+findFnorma.get(i,0)+"  |   "+Math.abs(normaY.get(i,0)-findFnorma.get(i,0)));
                sbForInterimResult.append("\n");
            }
            sbForInterimResult.append("\nМаксимальна похибка: "+MyMath.maxValue(originalY.minus(findForiginal)));
            sbForInterimResult.append("\nМаксимальна похибка(нормована): "+MyMath.maxValue(normaY.minus(findFnorma)));
            stringInterimResult = sbForInterimResult.toString();
        }
        Matrix errors = originalY.minus(findForiginal);
        maxError = MyMath.maxValue(errors);
    }
}
