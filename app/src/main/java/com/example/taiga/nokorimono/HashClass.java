package com.example.taiga.nokorimono;

/**
 * Created by taiga on 2017/11/01.
 */

public class HashClass {

    public String getRandomHash(byte bin[]){
        String s = "";
        int size = bin.length;
        for (int i = 0; i < size; i++) {
            int n = bin[i];
            if (n < 0) {
                n += 256;
            }
            String hex = Integer.toHexString(n);
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            s += hex;
        }
        return s;
    }

}
