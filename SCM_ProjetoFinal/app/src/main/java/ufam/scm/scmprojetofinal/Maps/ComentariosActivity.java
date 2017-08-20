package ufam.scm.scmprojetofinal.Maps;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import ufam.scm.scmprojetofinal.BaseActivity;
import ufam.scm.scmprojetofinal.R;
import ufam.scm.scmprojetofinal.conn.ServerInfo;
import ufam.scm.scmprojetofinal.conn.VolleyConnection;
import ufam.scm.scmprojetofinal.interfaces.CustomVolleyCallbackInterface;

public class ComentariosActivity extends BaseActivity implements CustomVolleyCallbackInterface {
    private static final String PREF_NAME = "LoginActivityPreferences";
    private EditText et_comentarios;
    private Button enviar;
    private VolleyConnection mVolleyConnection;
    private String mensagem;
    private  String dados;
    private  String json;
    Bundle params;
    Intent intent;
    String dado = new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);

        mVolleyConnection = new VolleyConnection(this);

        et_comentarios = (EditText) findViewById(R.id.comentarios);
        enviar = (Button) findViewById(R.id.btn_comentar);

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = getIntent();
                params = intent.getExtras();

                if (params != null) {

                    dado = params.getString("mensagem").toString();

                    Log.d("RESPOSTA: ", "O EMAIL DA PESSOA EH " + dado);

                } if (params == null) {
                    Log.d("RESPOSTA: ", "NAO PEGUEI O EMAIL");
                }


                //Mensagem a ser enviada para o servidor
                mensagem = dado.toString().trim() + ", " + et_comentarios.getText().toString().trim() + ", " + 47;

                dados = mensagem;

                try {

                    json = mVolleyConnection.callServerApiByJsonObjectRequest(ServerInfo.SERVER_URL, "inserirComentario", dados, "comentarios");

                    Log.d("MENSAGEM COMENTARIO", et_comentarios.getText().toString());
                    Log.d("MENSAGEM JSON", json.toString());

                    if (json.equalsIgnoreCase("OK")) {

                        Bundle params = new Bundle();
                        String resposta = et_comentarios.getText().toString();
                        params.putString("comentarios", resposta);

                        Intent intent = new Intent(ComentariosActivity.this, MapsActivity.class);
                        intent.putExtras(params);
                        startActivity(intent);

                        Toast.makeText(getApplicationContext(), "Comentario: " + et_comentarios.getText().toString() , Toast.LENGTH_SHORT).show();
                        Log.d("MENSAGEM COMENTARIO", et_comentarios.getText().toString());
                        Log.d("MENSAGEM CR", resposta);
                        
                    }else{
                        Toast.makeText(getApplicationContext(), "Comentario falhou! ", Toast.LENGTH_SHORT).show();
                        Log.d("MENSAGEM COMENTARIO2", et_comentarios.getText().toString());

                    }

                } catch (Exception e) {
                    Log.i("RESPOSTA EXCEPTION", e.toString());

                }
            }
        });



    }
    public void signOut(View view){
        SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear().commit();

        finish();


    }

    @Override
    public void deliveryResponse(JSONArray response, String flag) {

    }

    @Override
    public void deliveryResponse(JSONObject response, String flag) {

    }

    @Override
    public void deliveryError(VolleyError error, String flag) {

    }
}
