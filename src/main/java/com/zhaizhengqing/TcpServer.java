package com.zhaizhengqing;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.String.format;

public class TcpServer {
  public static void main(String[] args) throws IOException {
    ServerSocket serverSocket = new ServerSocket(8001);
    while (true) {
      final Socket accept = serverSocket.accept();
      Runnable runnable =
          new Runnable() {
            public void run() {
              try {
                String name = Thread.currentThread().getName();
                final InputStream inputStream = accept.getInputStream();
                final OutputStream outputStream = accept.getOutputStream();
                BufferedWriter bufferedWriter =
                    new BufferedWriter(new OutputStreamWriter(outputStream));
                BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(inputStream));
                System.out.println(format("%s connected", name));
                bufferedWriter.write(format("hello %s\n", name));
                bufferedWriter.flush();
                while (true) {
                  String line = bufferedReader.readLine();
                  if (line == null) {
                    System.out.println(format("%s client closed", name));
                    accept.close();
                    break;
                  }
                  if (line.startsWith("exit")) {
                    System.out.println(format("%s server closed", name));
                    bufferedWriter.write(format("bye %s\n", name));
                    bufferedWriter.flush();
                    accept.close();
                    break;
                  }
                  System.out.println(format("%s:\t%s", name, line));
                }
              } catch (IOException e) {
                throw new RuntimeException(e);
              } finally {
                try {
                  accept.close();
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
              }
            }
          };
      Thread thread = new Thread(runnable);
      thread.start();
    }
  }
}
