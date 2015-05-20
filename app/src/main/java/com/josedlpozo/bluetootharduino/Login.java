package com.josedlpozo.bluetootharduino;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Login extends ActionBarActivity {

    public static String USER = "usuario";

    private EditText  username=null;
    private EditText password=null;
    private Button login;
    private TextView usuario;

    private String URL_NEW_PREDICTION = "http://www.octopus3p.com/base_datos/acces_movil.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText)findViewById(R.id.editText1);
        password = (EditText)findViewById(R.id.editText2);
        login = (Button)findViewById(R.id.button1);
        usuario = (TextView) findViewById(R.id.usuario);

        if(Variables.INSTANCE.getUser() == null) {
            usuario.setText("No ha iniciado sesión.");
            login.setText("Login");
        }else{
            usuario.setText("Ha iniciado sesión como:"+Variables.INSTANCE.getUser());
            login.setText("Disconnect");
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Variables.INSTANCE.getUser() == null) {
                    new AddNewPrediction().execute(username.getText().toString(), password.getText().toString());
                }else{
                    Variables.INSTANCE.setUser(null);
                    usuario.setText("No ha iniciado sesión.");
                    login.setText("Login");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    private class AddNewPrediction extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... arg) {
            // TODO Auto-generated method stub
            String user = arg[0];
            String pass = arg[1];


            // Preparing post params
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("usuario", user));
            params.add(new BasicNameValuePair("password", pass));

            MySqlService serviceClient = new MySqlService();

            String json = serviceClient.makeServiceCall(URL_NEW_PREDICTION,
                    MySqlService.POST, params);

            //Log.d("Create Prediction Request: ", "> " + json);

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    boolean error = jsonObj.getBoolean("error");
                    // checking for error node in json
                    if (!error) {
                        // new category created successfully
                        //Log.e("Prediction added successfully ",
                          //      "> " + jsonObj.getString("message"));
                        Intent intent = new Intent();
                        intent.putExtra(USER,user);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    } else {

                        Handler handler =  new Handler(getApplicationContext().getMainLooper());
                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Usuario o contraseña incorrectos.", Toast.LENGTH_LONG).show();
                            }
                        });

                        Log.e("Add Prediction Error: ",
                                "> " + jsonObj.getString("message"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "JSON data error!");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}
