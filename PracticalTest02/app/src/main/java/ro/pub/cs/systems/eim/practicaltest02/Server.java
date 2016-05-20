package ro.pub.cs.systems.eim.practicaltest02;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by student on 5/20/16.
 */
class CommunicationThread extends Thread {

    private Socket socket;

    public CommunicationThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);

            String received = bufferedReader.readLine();
            System.out.println("Server received" + received);

            PrintWriter printWriter = Utilities.getWriter(socket);
            if (received.contains("bad")) {
                printWriter.println("Firewall says stop");
            } else {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(received);
                ResponseHandler<String> responseHandlerGet = new BasicResponseHandler();

                try {
                    printWriter.println(httpClient.execute(httpGet, responseHandlerGet));
                } catch (ClientProtocolException clientProtocolException) {
                        clientProtocolException.printStackTrace();
                } catch (IOException ioException) {
                        ioException.printStackTrace();
                }
            }


            socket.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}

    class ServerThread extends Thread {

        private boolean isRunning;

        int port;
        private ServerSocket serverSocket;

        public ServerThread(int port) {
            super();
            this.port = port;
        }

        public void startServer() {
            isRunning = true;
            start();
        }

        public void stopServer() {
            isRunning = false;
            try {
                serverSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(port);
                while (isRunning) {
                    Socket socket = serverSocket.accept();
                    if (socket != null) {
                        CommunicationThread communicationThread = new CommunicationThread(socket);
                        communicationThread.start();
                    }
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

