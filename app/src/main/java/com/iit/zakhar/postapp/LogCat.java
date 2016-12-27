package com.iit.zakhar.postapp;


import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class LogCat {

    private File outputFile;

    public String saveLogcatToFile(Context context) {

        String fileName = "logcat_" + System.currentTimeMillis() + ".txt";
        Process process;
        try {
            process = Runtime.getRuntime().exec("logcat -d");

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            StringBuilder log = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line);
            }
            outputFile = new File(context.getExternalFilesDir(null), fileName);

            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(log.toString().getBytes());
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputFile.getAbsolutePath();
    }
}
