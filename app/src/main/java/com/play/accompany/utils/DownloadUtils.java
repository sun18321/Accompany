package com.play.accompany.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class DownloadUtils {
    private static DownloadUtils mUtils = null;
    private ProgressDialog mDialog;

    public static DownloadUtils getInstance() {
        if (mUtils == null) {
            synchronized (DownloadUtils.class) {
                if (mUtils == null) {
                    mUtils = new DownloadUtils();
                }
            }
        }
        return mUtils;
    }

    public void downloadFile(String url, Context context, final DownloadListener listener) {
        showDialog(context);

        Request request = new Request.Builder().url(url).build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                ToastUtils.showCommonToast("下载失败");
                if (listener!=null) {
                    listener.downloadFailed();
                }
                mDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.body() != null) {
                    long total = response.body().contentLength();

                    LogUtils.d("download", "total:" + total);

                    InputStream is = null;
                    byte[] buf = new byte[1024];
                    int len = 0;
                    long sum = 0;
                    FileOutputStream fos = null;
                    File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }
                    String name = getHeaderFileName(response);
                    if (TextUtils.isEmpty(name)) {
                        name = DateUtils.getToday() + ".apk";
                    }
                    LogUtils.d("download", "name:" + name);
                    File filePath = new File(directory, name);
                    if (filePath.exists()) {
                        long length = filePath.length();
                        LogUtils.d("download", "filePath length:" + length);
                        if (length == total) {
                            mDialog.dismiss();
                            listener.downloadComplete(filePath);
                            return;
                        } else {
                            filePath.delete();
                        }
                    }
                    try {
                        fos = new FileOutputStream(filePath);
                        is = response.body().byteStream();
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                            sum += len;
                            int progress = (int) (sum * 1.0f / total * 100);
                            mDialog.setProgress(progress);
                        }
                        fos.flush();
                        mDialog.dismiss();
                        if (listener != null) {
                            listener.downloadComplete(filePath);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (is != null) {
                                is.close();
                            }
                            if (fos != null) {
                                fos.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private String getHeaderFileName(Response response) {
        String dispositionHeader = response.header("Content-Disposition");
        if (!TextUtils.isEmpty(dispositionHeader)) {
            dispositionHeader.replace("attachment;filename=", "");
            dispositionHeader.replace("filename*=utf-8", "");
            String[] strings = dispositionHeader.split("; ");
            if (strings.length > 1) {
                dispositionHeader = strings[1].replace("filename=", "");
                dispositionHeader = dispositionHeader.replace("\"", "");
                return dispositionHeader;
            }
            return "";
        }
        return "";
    }

    private void showDialog(Context context) {
        mDialog = new ProgressDialog(context);
        mDialog.setTitle("更新");
        mDialog.setMessage("正在下载");
        mDialog.setMax(100);
        mDialog.setCancelable(false);
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.show();
    }

    private void installApk() {

    }



   public interface DownloadListener{

        void downloadFailed();

        void downloadComplete(File filePath);
    }
}
