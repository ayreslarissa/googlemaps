package ufam.scm.scmprojetofinal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.facebook.FacebookSdk;

import ufam.scm.scmprojetofinal.Cadastro.CadastroActivity;
import ufam.scm.scmprojetofinal.Login.LoginAppActivity;

public class MainActivity extends  AppCompatActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        final Button login_app = (Button) findViewById(R.id.app_login_button);
        final Button cadastro = (Button) findViewById(R.id.nao_sou_cadastrado);
        //final Button mapa = (Button) findViewById(R.id.maps);
       // final Button coment = (Button) findViewById(R.id.btn_coment);


        login_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginAppActivity.class);
                startActivity(intent);
            }
        });

        cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CadastroActivity.class);
                startActivity(intent);
            }
        });
      /*  mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
        coment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ComentariosActivity.class);
                startActivity(intent);
            }
        });*/

    }

}
