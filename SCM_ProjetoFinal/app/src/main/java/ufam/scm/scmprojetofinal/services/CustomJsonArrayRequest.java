package ufam.scm.scmprojetofinal.services;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class CustomJsonArrayRequest extends Request<JSONArray> {
	private Listener<JSONArray> response;
	private Map<String, String> params;
	
	
	public CustomJsonArrayRequest(int method, String url, Map<String, String> params, Listener<JSONArray> response, ErrorListener listener) {
		super(method, url, listener);
		this.params = params;
		this.response = response;
		// TODO Auto-generated constructor stub
	}
	public CustomJsonArrayRequest(String url, Map<String, String> params, Listener<JSONArray> response, ErrorListener listener) {
		super(Method.GET, url, listener);
		this.params = params;
		this.response = response;
		// TODO Auto-generated constructor stub
	}
	
	public Map<String, String> getParams() throws AuthFailureError {
		return params;
	}
	
	public Map<String, String> getHeaders() throws AuthFailureError {
		HashMap<String, String> header = new HashMap<String, String>();
		header.put("apiKey", "Essa e minha API KEY: json array");
		
		return(header);
	}
	
	public Priority getPriority(){
		return(Priority.NORMAL);
	}
	
	@Override
	protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
		try {
			String js = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			Log.i("APP", js);
			return(Response.success(new JSONArray(js), HttpHeaderParser.parseCacheHeaders(response)));
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		catch (JSONException e) {
			e.printStackTrace();
			/*if(e.getMessage().contains("{\"id\":\"not_found\"}")){

				HashMap<String,String> jsErr = new HashMap<String,String>();
				jsErr.put("id","not_found");
				HashMap<String,String> nulo = new HashMap<String,String>();
				//nulo.put("0","0");

				ArrayList<HashMap> ja = new ArrayList<HashMap>();
				ja.add(jsErr);
				//ja.add(nulo);

				return(Response.success(new JSONArray(ja), HttpHeaderParser.parseCacheHeaders(response)));

			}*/
		}
		return null;
	}

	public Listener getJa(){
		return this.response;
	}

	
	@Override
	protected void deliverResponse(JSONArray response) {
		this.response.onResponse(response);
	}

}
