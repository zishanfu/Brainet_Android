package com.susanfu.mainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Random;

/**
 * Created by SammiFu on 28/11/2017.
 */

public class Tools {

    public static String getSignal(String username){
        String path = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        Random rd = new Random();
        int n = rd.nextInt(12) + 3;
        String inputPath ;
        if(n >= 10){
            inputPath = path + "/Android/data/eegmmidb/" + username+ "/" + username + "R" + n +".txt";
        }else{
            inputPath = path + "/Android/data/eegmmidb/" + username+ "/" + username + "R0" + n +".txt";
        }
        File myFile = new File(inputPath);
        StringBuilder str = read_file(myFile.getAbsolutePath());
        return str.toString();
    }

    public static StringBuilder read_file(String filename) {
        try {
            FileInputStream fis = new FileInputStream(filename);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return sb;
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder("");
            return sb;
        }
    }
}
