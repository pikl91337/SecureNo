package com.example.securno;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Класс для работы с терминалом
 */
public class TerminalWorker {

    /**
     * Метод который выполняет команду cmd от рута и возвращает ответ (если он есть)
     */
    String RunCommandAsRoot (String cmd) {
        StringBuffer cmdOut = new StringBuffer();
        //Process process;
        Process process;

        try {
            process = Runtime.getRuntime().exec("su");
            OutputStream stdin = process.getOutputStream();
            InputStream stderr = process.getErrorStream();
            InputStream stdout = process.getInputStream();
            stdin.write((cmd+"\n").getBytes());
            stdin.write("exit\n".getBytes());
            stdin.flush();
            //process = Runtime.getRuntime().exec(cmd);
            //InputStreamReader r = new InputStreamReader(process.getInputStream());
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(stdout));
            char[] buf = new char[100000];
            int nRead = 0; // здесь сразу становится -1
            while((nRead = bufReader.read(buf)) > 0){
                cmdOut.append(buf,0,nRead);
            }
            bufReader.close();
            bufReader =
                    new BufferedReader(new InputStreamReader(stderr));

            nRead = 0;
            while((nRead = bufReader.read(buf)) > 0){
                cmdOut.append(buf,0,nRead);
            }
            bufReader.close();

            process.waitFor();
            process.destroy();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return cmdOut.toString();


    }
}
