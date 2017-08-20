package ufam.scm.scmprojetofinal.Cadastro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.VolleyError;
import com.facebook.Profile;

import org.json.JSONArray;
import org.json.JSONObject;

import ufam.scm.scmprojetofinal.BaseActivity;
import ufam.scm.scmprojetofinal.Maps.MapsActivity;
import ufam.scm.scmprojetofinal.R;
import ufam.scm.scmprojetofinal.conn.ServerInfo;
import ufam.scm.scmprojetofinal.conn.VolleyConnection;
import ufam.scm.scmprojetofinal.interfaces.CustomVolleyCallbackInterface;

public class CadastroFBActivity extends BaseActivity implements CustomVolleyCallbackInterface {

    private VolleyConnection mVolleyConnection;
    private String mensagem;
    private Profile profile;
    Intent intent;
    Bundle params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_fb);

        mVolleyConnection = new VolleyConnection(this);

        intent = getIntent();
        params = intent.getExtras();

        profile = Profile.getCurrentProfile();

        if (params != null) {

            mensagem = params.getString("infos").toString();

            Log.d("INFOS", mensagem);

        }
        if (params == null) {
            Log.i("RESPOSTA: ", "NAO PEGUEI O NOME");
        }


        String dados = mensagem;

        String json;

        try {

            json = mVolleyConnection.callServerApiByJsonObjectRequest(ServerInfo.SERVER_URL, "cadastroUsuario", dados, "cadastro");

            if (json.equalsIgnoreCase("OK")) {

                Intent intent = new Intent(CadastroFBActivity.this, MapsActivity.class);

                String resposta = mensagem;

                Log.i("RESPOSTA: ", "MEU NOME EH " + resposta + " E ESTOU " + json + ", SAINDO DO CADASTRO FB");
                startActivity(intent);

            }
        } catch (Exception e) {
            Log.i("RESPOSTA EXCEPTION", e.toString());

        }
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
