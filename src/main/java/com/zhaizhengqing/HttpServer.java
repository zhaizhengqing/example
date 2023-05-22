package com.zhaizhengqing;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.String.format;

public class HttpServer {
  public static void main(String[] args) throws IOException {
    ServerSocket serverSocket = new ServerSocket(8002);
    while (true) {
      final Socket accept = serverSocket.accept();
      Runnable runnable =
              () -> {
                String name = Thread.currentThread().getName();
                try {
                  final InputStream inputStream = accept.getInputStream();
                  final OutputStream outputStream = accept.getOutputStream();
                  BufferedWriter bufferedWriter =
                      new BufferedWriter(new OutputStreamWriter(outputStream));
                  BufferedReader bufferedReader =
                      new BufferedReader(new InputStreamReader(inputStream));
                  System.out.println(format("%s connected", name));
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

                    if ("".equals(line)) {
                      String body = name;
                      bufferedWriter.write(format("HTTP/1.1 200 OK\n"));
                      bufferedWriter.write(format("Content-Type: text/html\n"));
                      bufferedWriter.write(format("Content-Length: %s\n", body.getBytes().length));
                      bufferedWriter.write(format("Connection: Keep-Alive\n"));
                      bufferedWriter.write(format("Keep-Alive: timeout=%s, max=%s\n", 10, 3));
                      bufferedWriter.write(format("\n"));
                      bufferedWriter.write(format(body));
                      bufferedWriter.flush();
                    }
                    System.out.println(format("%s:\t%s", name, line));
                  }
                } catch (IOException e) {
                  throw new RuntimeException(e);
                } finally {
                  try {
                    System.out.println(format("%s end", name));
                    accept.close();
                  } catch (IOException e) {
                    throw new RuntimeException(e);
                  }
                }
              };
      Thread thread = new Thread(runnable);
      thread.start();
    }
  }
}
