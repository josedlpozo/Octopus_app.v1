package com.josedlpozo.bluetootharduino;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * Created by josedlpozo on 18/4/15.
 */
public class SensoresFragment extends Fragment {

    TextView temp;
    TextView haire;
    TextView htierra;
    TextView rocio;
    TextView gradosBruj;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.sensores_fragment,container,false);

        if(v != null){
            temp = (TextView) v.findViewById(R.id.temperatura);
            haire = (TextView) v.findViewById(R.id.haire);
            htierra = (TextView) v.findViewById(R.id.htierra);
            rocio = (TextView) v.findViewById(R.id.rocio);
            gradosBruj = (TextView) v.findViewById(R.id.grados_bruj);

            Thread th1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    setMedidas();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            th1.start();

        }
        return v;
    }

    private void setMedidas() {
        String dataInPrint = Variables.INSTANCE.getRecibido();
        if (dataInPrint != null) {
            int dataLength = dataInPrint.length(); //get length of data received
            if (dataInPrint.substring(0, 1).equals("#")) //if it starts with # we know it is what we are looking for
            {
                String[] sensores = {"", "", "", "", "", "", ""};
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

            }
        }
    }

}
