package ufam.scm.scmprojetofinal.Cadastro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import ufam.scm.scmprojetofinal.BaseActivity;
import ufam.scm.scmprojetofinal.Maps.MapsActivity;
import ufam.scm.scmprojetofinal.R;
import ufam.scm.scmprojetofinal.conn.ServerInfo;
import ufam.scm.scmprojetofinal.conn.VolleyConnection;
import ufam.scm.scmprojetofinal.interfaces.CustomVolleyCallbackInterface;

public class CadastroActivity extends BaseActivity implements CustomVolleyCallbackInterface {

    private VolleyConnection mVolleyConnection;
    private String mensagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        mVolleyConnection = new VolleyConnection(this);

        final EditText nome = (EditText) findViewById(R.id.nome_cadastro_app);
        final EditText email = (EditText) findViewById(R.id.email_cadastro_app);
        final EditText senha = (EditText) findViewById(R.id.password_cadastro_app);

        Button cadastrar = (Button) findViewById(R.id.cadastro_button);

        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mensagem = nome.getText().toString().trim() +", " + email.getText().toString().trim() + ", " + senha.getText().toString().trim();

                Log.d("MENSAGEM", mensagem);

                String dados = mensagem;

                String json;

                try {

                    json = mVolleyConnection.callServerApiByJsonObjectRequest(ServerInfo.SERVER_URL, "cadastroUsuario", dados, "cadastro");

                    if (json.equalsIgnoreCase("OK")) {

                        Bundle params = new Bundle();
                        String resposta = mensagem;
                        params.putString("mensagem", resposta);

                        Intent intent = new Intent(CadastroActivity.this, MapsActivity.class);
                        intent.putExtras(params);
                        Log.i("RESPOSTA: ", "MEU NOME EH " + resposta + " E ESTOU " + json + ", SAINDO DO CADASTRO ACTIVITY");
                        startActivity(intent);

                        // Nome que vai ser atribuido ao objeto do tipo SharedPreferences para recuperar posteriormente
                        final String MyPREFERENCES = "PrefsCadastroApp" ;

                        // Declaração de um objeto sharedpreferences da instância de SharedPreferences
                        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

                        // Cria um objeto chamado editor da instância de Editor a partir do método edit do objeto sharedpreferences
                        SharedPreferences.Editor editor = sharedpreferences.edit();

                        // Coloca uma chave chamada key do tipo String passando determinado valor de tipo String, nesse caso pode ser salvo o código do usuário, login, senha o que você quiser. Para cada atributo você cria mais métodos editor.putString(), editor.putInt() um abaixo do outro.
                        editor.putString("email", email.getText().toString());
                        editor.putString("senha", senha.getText().toString());

                        // Salva o que foi feito
                        editor.commit();

                    }
                }catch (Exception e ){
                    Log.i("RESPOSTA EXCEPTION", e.toString());

                }


            }
        });
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


    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
    }
}
