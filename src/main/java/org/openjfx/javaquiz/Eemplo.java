/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.openjfx.javaquiz;

/**
 *
 * @author angel
 */
public class Eemplo {
    public static void main(String[] args) {
        int Arr[] = {1,2,3,4,5};
        int contador = 2;
        System.out.println(5%5 + " + " + Arr[5%5]);
        System.out.println(4%5 + " + " + Arr[4%5]);
        System.out.println(3%5 + " + " + Arr[3%5]);
        System.out.println(2%5 + " + " + Arr[2%5]);
        System.out.println(1%5 + " + " + Arr[1%5]);
        System.out.println(0%5 + " + " + Arr[0%5]);
        contador = 4;
        for (int i = 0; i < Arr.length; i++, contador--) {
            System.out.println("al reves: " + Arr[(contador)%Arr.length]);
            int index = i%Arr.length;
            System.out.println("valor: " + Arr[index]);
            if(contador == 0) contador = 4;
            if(i == 4) contador = 0;
        }
    }
}
