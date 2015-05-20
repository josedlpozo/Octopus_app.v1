package com.josedlpozo.bluetootharduino;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;

/**
 * Created by josedlpozo on 18/4/15.
 */
public class SensoresFragment extends Fragment {

    private static final String TAG = "SENSORES_FRAGMENT";

    TextView temp;
    TextView haire;
    TextView htierra;
    TextView rocio;
    TextView gradosBruj;

    private Handler mHandler;

    private String URL_NEW_PREDICTION = "http://www.octopus3p.com/base_datos/send.php";

    private int contador = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.sensores_fragment,container,false);

        mHandler = new Handler();

        if(v != null){
            temp = (TextView) v.findViewById(R.id.temperatura);
            haire = (TextView) v.findViewById(R.id.haire);
            htierra = (TextView) v.findViewById(R.id.htierra);
            rocio = (TextView) v.findViewById(R.id.rocio);
            gradosBruj = (TextView) v.findViewById(R.id.grados_bruj);
            mStatusChecker.run();

        }
        return v;
    }

     Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(mStatusChecker, 100);
            setMedidas();
        }
    };

    private void setMedidas() {

        String dataInPrint = Variables.INSTANCE.getRecibido();
        if (dataInPrint != null ) {
            contador++;
            htierra.setVisibility(View.VISIBLE);
            haire.setVisibility(View.VISIBLE);
            rocio.setVisibility(View.VISIBLE);
            gradosBruj.setVisibility(View.VISIBLE);
            int dataLength = dataInPrint.length(); //get length of data received
            if (dataInPrint.substring(0, 1).equals("#")) //if it starts with # we know it is what we are looking for
            {
                Log.i("MEDIDAS",dataInPrint );
                String[] sensores = {"", "", "", "", "", "", "",""};
                int contador = 0;
                int index = 0;
                for (int i = 0; i < dataInPrint.length(); i++) {
                    if (dataInPrint.charAt(i) == '+') {
                        sensores[contador] = dataInPrint.substring(index + 1, i);
                        contador++;
                        index = i;
                    } else if (dataInPrint.charAt(i) == '*') {
                        sensores[contador] = dataInPrint.substring(index + 1, i);
                        contador = 0;
                        index = 0;
                    }

                }
                temp.setText(" Temperatura: " + sensores[0] + "C"); //update the textviews with sensor values
                htierra.setText("Humedad de la tierra: " + sensores[1] + "%");
                rocio.setText("Nivel de rocio: " + sensores[2] + "%");
                haire.setText("Humedad del aire: " + sensores[3] + "%");
                gradosBruj.setText("Grados Brujula: " + sensores[4] + "º");

                if(Variables.INSTANCE.getUser() != null)
                new AddNewPrediction().execute(sensores[0], sensores[1],sensores[2],sensores[3],sensores[4],sensores[5],sensores[6],sensores[7]);

                if(Math.abs(Double.parseDouble(sensores[5])) > 5 || Math.abs(Double.parseDouble(sensores[6]))> 5 )
                    Toast.makeText(getActivity().getBaseContext(),"Upps! El coche se ha caído",Toast.LENGTH_LONG).show();

            }
        }else{

            htierra.setVisibility(View.GONE);
            haire.setVisibility(View.GONE);
            rocio.setVisibility(View.GONE);
            gradosBruj.setVisibility(View.GONE);
            temp.setText("No hay datos recibidos.");
        }
    }

    private class AddNewPrediction extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... arg) {
            // TODO Auto-generated method stub
            String temp = arg[0];
            String htierra = arg[1];
            String rocio = arg[2];
            String haire = arg[3];
            String gradosbruj = arg[4];
            String acx = arg[5];
            String acy = arg[6];
            String acz = arg[7];



            // Preparing post params
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("temp",temp));
            params.add(new BasicNameValuePair("htierra", htierra));
            params.add(new BasicNameValuePair("rocio",rocio));
            params.add(new BasicNameValuePair("haire", haire));
            params.add(new BasicNameValuePair("gradosbruj",gradosbruj));
            params.add(new BasicNameValuePair("acx", acx));
            params.add(new BasicNameValuePair("acy",acy));
            params.add(new BasicNameValuePair("acz", acz));
            params.add(new BasicNameValuePair("user", Variables.INSTANCE.getUser()));

            MySqlService serviceClient = new MySqlService();

            String json = serviceClient.makeServiceCall(URL_NEW_PREDICTION,
                    MySqlService.POST, params);

            Log.e(TAG,json);

            //Log.d("Create Prediction Request: ", "> " + json);

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    boolean error = jsonObj.getBoolean("error");

                    // checking for error node in json
                    if (!error) {
                        // new category created successfully
                        Log.e("Prediction added succes","> " + jsonObj.getString("message"));
                        Log.i(TAG,"NO HAY ERROR");

                    } else {
                        Log.i(TAG,"HAY ERROR");

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
