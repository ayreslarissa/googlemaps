package ufam.scm.scmprojetofinal.conn;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ufam.scm.scmprojetofinal.BaseActivity;
import ufam.scm.scmprojetofinal.interfaces.CustomVolleyCallbackInterface;
import ufam.scm.scmprojetofinal.services.CustomJsonObjectRequest;

/**
 * Created by Raphael on 20/10/2015.
 */
public class VolleyConnection extends BaseActivity {

    private CustomVolleyCallbackInterface mCustomVolleyCallbackInterface;
    private Map<String, String> params;
    private String mVOLLEYTAG;
    public String json = new String("");

    public VolleyConnection(Context cvci){
        VolleyConnectionQueue.getINSTANCE(); //inicia a fila de requisições
        mCustomVolleyCallbackInterface = (CustomVolleyCallbackInterface) cvci;
    }

    protected void setVolleyTag(String tag){
        Log.i("APP", "setVolleyTag(" + tag + ")");
        this.mVOLLEYTAG = tag;
    }

    public void canceRequest(){
        if(mVOLLEYTAG!=null){
            VolleyConnectionQueue.getINSTANCE().cancelRequest(this.mVOLLEYTAG);
        }
    }


    //METODO PARA ENVIO E RECEBIMENTO DE JSONOBJECTS
    public String callServerApiByJsonObjectRequest(final String url, String method, final String data, final String flag){


        Log.i("PHOTO_ACTIVITY", "ENTREI: callByJsonObjectRequest()");
        if(data!=null) {
            params = new HashMap<String, String>();
            params.put("data", data);
            params.put("method", method);
        }

        final String activityName = mCustomVolleyCallbackInterface.getClass().getSimpleName();
        Log.i("SEND MESSAGE", "[" + activityName + "] url: " + url + " data: " + data);

        CustomJsonObjectRequest request = new CustomJsonObjectRequest(Request.Method.POST,
                url,
                params,
                new Response.Listener<JSONObject>(){


                    @Override
                    public void onResponse(JSONObject response) {
                          mCustomVolleyCallbackInterface.deliveryResponse(response, flag);
                             try {
                                 json = new String(response.getString("mensagem"));

                             } catch (JSONException e) {
                                 e.printStackTrace();
                             }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //envia a mensagem de erro para a activity
                        mCustomVolleyCallbackInterface.deliveryError(error, flag);
                    }
                });

        request.setTag("tagPhotoActivity");
        this.setVolleyTag("tagPhotoActivity");
        //rq.add(request);
        VolleyConnectionQueue.getINSTANCE().addQueue(request);

        return json;
    }

}
