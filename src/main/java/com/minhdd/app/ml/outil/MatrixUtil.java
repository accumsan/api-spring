package com.minhdd.app.ml.outil;

import org.apache.spark.mllib.linalg.Matrix;

/**
 * Created by minhdao on 17/03/16.
 */
public class MatrixUtil {
    private abstract static class Operation {
        public abstract double run(double value);
    }

    private static void apply(Matrix matrix, Operation operation) {
        for (int i=0; i< matrix.numRows(); i++) {
            for (int j=0; j< matrix.numCols(); j++) {
                matrix.update(i,j, operation.run(matrix.apply(i,j)));
            }
        }
    }

    public static void divide(Matrix matrix, long m) {
        apply(matrix, new Operation() {
            @Override
            public double run(double value) {
                return value/m;
            }
        });
    }

    public static void multiply(Matrix matrix, long m) {
        apply(matrix, new Operation() {
            @Override
            public double run(double value) {
                return value * m;
            }
        });
    }

    public static void plus(Matrix matrix, long m) {
        apply(matrix, new Operation() {
            @Override
            public double run(double value) {
                return value + m;
            }
        });
    }

    public static void minus(Matrix matrix, long m) {
        apply(matrix, new Operation() {
            @Override
            public double run(double value) {
                return value - m;
            }
        });
    }
}
