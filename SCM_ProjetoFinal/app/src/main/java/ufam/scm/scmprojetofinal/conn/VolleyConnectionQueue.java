package ufam.scm.scmprojetofinal.conn;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Raphael on 30/07/2015.
 * ESSA CLASSE CONTROLA A FILA DE REQUISIÇÕES HTTP, ELA DEVE SER CHAMADA NO ONCREATE DA BASEACTIVITY
 */
public class VolleyConnectionQueue {

    private static VolleyConnectionQueue INSTANCE;
    private RequestQueue rq;

    public static synchronized VolleyConnectionQueue getINSTANCE(){
        if(INSTANCE==null){
            INSTANCE = new VolleyConnectionQueue();
        }
        return INSTANCE;
    }

    public void startQueue(Context c){
        if(rq==null){
            this.rq = Volley.newRequestQueue(c);
        }
    }

    public void addQueue(Request request){
        this.rq.add(request);
    }

    public void cancelRequest(String tag){
        this.rq.cancelAll(tag);
    }

}