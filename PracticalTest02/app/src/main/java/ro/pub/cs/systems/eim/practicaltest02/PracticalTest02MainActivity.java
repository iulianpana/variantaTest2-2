package ro.pub.cs.systems.eim.practicaltest02;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class PracticalTest02MainActivity extends ActionBarActivity {
    EditText serverPortEditText, clientPortEditText, clientAddressEditText, clientUrlEditText;
    Button clientButton;
    Button serverButton;
    TextView response;
    WebView webView;


    private class ButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.start_client_button:
                    ClientAsyncTask clientAsyncTask = new ClientAsyncTask();
                    clientAsyncTask.execute(clientAddressEditText.getText().toString(), clientPortEditText.getText().toString(), clientUrlEditText.getText().toString());
                    break;
                case R.id.start_server_button:
                    ServerThread serverThread = new ServerThread(Integer.parseInt(serverPortEditText.getText().toString()));
                    serverThread.startServer();
                    System.out.println("server started");
                    break;
            }
        }
    }
    private class ClientAsyncTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... params) {
            Socket socket = null;
            try {
                String serverAddress = params[0];
                int serverPort = Integer.parseInt(params[1]);
                String text = params[2];
                socket = new Socket(serverAddress, serverPort);
                if (socket == null) {
                    return null;
                }
                PrintWriter printWriter = Utilities.getWriter(socket);
                printWriter.println(text);
                BufferedReader bufferedReader = Utilities.getReader(socket);
                String currentLine;
                while ((currentLine = bufferedReader.readLine()) != null) {
                    System.out.println(currentLine);
                    publishProgress(currentLine);
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } finally {
                try {
                    if (socket != null) {
                        socket.close();
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            response.setText("");
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            response.append(progress[0] + "\n");
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);
        clientAddressEditText = (EditText) findViewById(R.id.client_address_edit_text);
        clientPortEditText = (EditText) findViewById(R.id.client_port_edit_text);
        clientUrlEditText = (EditText) findViewById(R.id.client_url_edit_text);
        serverPortEditText = (EditText) findViewById(R.id.server_port_edit_text);


        response = (TextView) findViewById(R.id.response_text_view);
        clientButton = (Button) findViewById(R.id.start_client_button);
        serverButton = (Button) findViewById(R.id.start_server_button);

        ButtonListener buttonListener = new ButtonListener();

        clientButton.setOnClickListener(new ButtonListener());
        serverButton.setOnClickListener(new ButtonListener());
        response.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_practical_test02_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
