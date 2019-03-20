package com.example.roman.tcpclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.io.*;
import java.net.*;

import static com.example.roman.tcpclient.MainActivity.APP_PREFERENCES_HOST;

public class TcpClient implements Runnable {
    public SharedPreferences mSettings;
    public static String LOG_PATH;
    public static int PORT = 2500;
    public static String HOST;
    public static final int READ_BUFFER_SIZE = 10;
    private String name = null;
    private Context context;
    public TcpClient(String s, Context context, SharedPreferences mSettings){
        name = s;
        this.context = context;
        this.mSettings = mSettings;
    }
    public void run(){
        char[] readed = new char[READ_BUFFER_SIZE];
        StringBuffer strBuff = new StringBuffer();
        try{
            InetAddress ipAddress = InetAddress.getByName(HOST);
            Socket socket = new Socket(HOST, PORT);
            OutputStream out = socket.getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(out);
            out.write(name.getBytes());
            out.flush();
            socket.shutdownOutput();
            Thread.sleep(500);
            InputStream in = socket.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);
            while(true){
                int count = reader.read(readed, 0, READ_BUFFER_SIZE);
                if(count == -1) break;
                strBuff.append(readed, 0, count);
                Thread.yield();
            }
            String oldFileContent =  null;
            File file = null;
            file = new File("/storage/emulated/0/" + LOG_PATH);
            try {
                if(!file.exists()) {
                    file.createNewFile();
                } else {
                    oldFileContent = this.readFile("/storage/emulated/0/" + LOG_PATH);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream("/storage/emulated/0/" + LOG_PATH);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            try {
                myOutWriter.append(oldFileContent != null ? oldFileContent + "Клиент " + name + " прочёл: " + strBuff.toString() + "\n" :  "Клиент " + name + " прочёл: " + strBuff.toString() + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                myOutWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Thread.yield();
        } catch (UnknownHostException e) {
            System.err.println("Исключение: " + e.toString());
        } catch (IOException e) {
            System.err.println("Исключение: " + e.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Клиент " + name + " прочёл: " + strBuff.toString());
    }

    private String readFile(String path){
        FileInputStream fin = null;
        byte[] bytes = null;
        try {
            fin = new FileInputStream(new File(path));
            bytes = new byte[fin.available()];
            fin.read(bytes);
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(fin != null) {
                    fin.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new String(bytes);
    }
}
