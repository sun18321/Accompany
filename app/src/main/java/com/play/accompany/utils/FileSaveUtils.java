package com.play.accompany.utils;


import android.content.Context;
import android.text.TextUtils;

import com.play.accompany.view.AccompanyApplication;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

public class FileSaveUtils {
    private static FileSaveUtils mUtils = null;
    public static FileSaveUtils getInstance(){
        if (mUtils == null) {
            synchronized (FileSaveUtils.class) {
                if (mUtils == null) {
                    mUtils = new FileSaveUtils();
                }
            }
        }
        return mUtils;
    }

    public void saveData(String name, String data){
        if (data == null || TextUtils.isEmpty(data)) {
            return;
        }

        FileOutputStream fileOutputStream = null;
        BufferedWriter bufferedWriter = null;
        try {
            fileOutputStream = AccompanyApplication.getContext().openFileOutput(name, Context.MODE_PRIVATE);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
            bufferedWriter.newLine();
            bufferedWriter.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void saveData(String name, List list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        String json = GsonUtils.toJson(list);
        saveData(name, json);
    }


    public String getData(String name) {
        BufferedReader bufferedReader = null;
        StringBuilder data = new StringBuilder();
        try {
            FileInputStream inputStream = AccompanyApplication.getContext().openFileInput(name);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                data.append(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data.toString();
    }
}