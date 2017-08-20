package ufam.scm.scmprojetofinal;


import android.os.Bundle;
import android.widget.Toast;

import ufam.scm.scmprojetofinal.conn.VolleyConnectionQueue;

public class BaseActivity extends android.support.v7.app.AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        INICIA A FILA DE REQUISIÇÕES HTTP
        ESSA FILA ESTA DENTRO DE UMA CLASSE SINGLETON, POIS DEVE HAVER APENAS UMA FILA DE REQUISIÇÕES
        PARA TODA A APLICAÇÃO

        COMO TODAS AS ACTIVITYS HERDAM DE BASE ACTIVITY, TODAS TEM A MESMA FILA DE REQUISIÇÕES
         */
        VolleyConnectionQueue.getINSTANCE().startQueue(this);

    }

    protected void Alert(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void longAlert(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


}
