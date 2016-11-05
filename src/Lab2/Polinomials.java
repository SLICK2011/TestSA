package Lab2;

import java.lang.Math;

/**
 * Created by valeriy on 22.10.16.
 */
public class Polinomials {

    public static double chebisheva(double x, int power){
        if (power == 0)
            return 0.5;
        else
        {
            if (power == 1)
                return (-1 + 2 * x);
            else
                return 2 * (-1 + 2 * x) * chebisheva(x, power - 1) - chebisheva(x, power - 2);
        }
    }

    public static double lezh(double x, int pow) {
        if (pow == 0)
            return 1;
        else
        {
            if (pow == 1)
                return x;
            else
                return (double)(((2 * (pow - 1) + 1) * x * lezh(x, pow - 1) - (pow - 1) * lezh(x, pow - 2)) / (pow));
        }
    }

    public static double lager(double x, int pow) {
        if (pow == 0)
            return 1;
        else
        {
            if (pow == 1)
                return -x + 1;
            else
                return (2 * (pow - 1) + 1 - x) * lager(x, pow - 1) - Math.pow((pow - 1), 2) * lager(x, pow - 2);
        }
    }

    public static double ermit(double x, int pow) {
        if (pow == 0)
            return 1;
        else
        {
            if (pow == 1)
                return 2 * x;
            else
                return 2 * x * ermit(x, pow - 1) - 2 * ermit(x, pow - 2);
        }
    }

    public static double countPolinomValue(int typePolinom, double x, int pow){
        switch (typePolinom){
            case 2:{
                return lezh(x,pow);
            }
            case 3:{
                return lager(x,pow);
            }
            case 4:{
                return ermit(x,pow);
            }
            default:{
                return chebisheva(x,pow);
            }
        }
    }
}
