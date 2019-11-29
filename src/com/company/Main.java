/*
Разработать программу реализующую параллельное перемножение матриц. Сравнить быстродействие с однопоточным исполнением
1. С использованием Fork-Join Pool
2. C использованием произвольного пула потоков, но минимизировать количество используемых потоков
 */


package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int[][] mat1 = {{1,1,1}, {1,1,1}};
        int[][] mat2 = {{1,1,1,1}, {1,1,1,1}, {1,1,1,1}};
        ForkJoinPool pool = new ForkJoinPool();
        List<Multiplier> tasks = new ArrayList<>(mat1.length*mat2[0].length);
        System.out.println(mat1.length);
        System.out.println(mat2[0].length);
        for(int i = 0; i < mat1.length; i++){
            for(int j = 0; j < mat2[0].length; j++){
                int[] a2 = new int[mat1[0].length];
                for(int k = 0; k < mat2.length; k++){
                    a2[k] = mat2[k][j];
                }
                tasks.add(new Multiplier(mat1[i], a2));
            }
        }
        ForkJoinTask<Integer>[] taskResults = new ForkJoinTask[mat1.length*mat2[0].length];
        long begTime = System.nanoTime();
        for (int i = 0; i < taskResults.length; i++) {
            taskResults[i] = pool.submit(tasks.get(i));
        }
        long endTime = System.nanoTime();
        System.out.println("Time: "+ (endTime-begTime)/1000);
        int[][] results = new int[mat1.length][mat2[0].length];
        System.out.println(results.length);
        for(int i = 0; i < results.length; i++){
            for(int j = 0; j < results[0].length; j++){
                results[i][j] = taskResults[(i*results.length)+j].get();
            }
        }
        printMat(results);
        printMat(mat1);
        printMat(mat2);
        begTime = System.nanoTime();
        printMat(matmul(mat1, mat2));
        endTime = System.nanoTime();
        System.out.println("Time: "+ (endTime-begTime)/1000);
    }

    static void printMat(int[][] mat){
        for(int i = 0; i < mat.length; i++){
            System.out.print("| ");
            for(int j = 0; j < mat[0].length; j++){
                System.out.print(mat[i][j] + ", ");
            }
            System.out.print("|");
            System.out.println();
        }
        System.out.println("\n");
    }

    static int[][] matmul(int[][] mat1, int[][] mat2){
        int[][] res = new int[mat1.length][mat2[0].length];
        for(int i = 0; i < mat1.length; i++){
            for(int j = 0; j < mat2[0].length; j++){
                int temp_res = 0;
                for(int k = 0; k < mat2.length; k++){
                    temp_res += mat1[i][k]*mat2[k][j];
                }
                res[i][j] = temp_res;
            }
        }
        return res;
    }
}

class Multiplier extends RecursiveTask<Integer>{
    int[] a1;
    int[] a2;
    Multiplier(int[] a1, int[] a2){
        this.a1 = a1;
        this.a2 = a2;
    }
    @Override
    protected Integer compute() {
        int res = 0;
        for(int i = 0; i < a1.length; i++){
            res += a1[i]*a2[i];
        }
        return res;
    }
}