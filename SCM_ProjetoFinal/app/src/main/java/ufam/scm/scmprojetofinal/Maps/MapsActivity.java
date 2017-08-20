package ufam.scm.scmprojetofinal.Maps;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.simplealertdialog.SimpleAlertDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import ufam.scm.scmprojetofinal.BaseActivity;
import ufam.scm.scmprojetofinal.Login.LoginAppActivity;
import ufam.scm.scmprojetofinal.R;
import ufam.scm.scmprojetofinal.conn.ServerInfo;
import ufam.scm.scmprojetofinal.conn.VolleyConnection;
import ufam.scm.scmprojetofinal.interfaces.CustomVolleyCallbackInterface;

public class MapsActivity extends BaseActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback, LocationListener, CustomVolleyCallbackInterface, SimpleAlertDialog.SingleChoiceArrayItemProvider {

    boolean mIsLargeLayout;
    private static final int ERROR_DIALOG_REQUEST = 1 ;

    String dados;
    Bundle params;
    Bundle params_coments;
    Intent intent;
    String json;

    String dado = new String();
    String comentario;

    EditText coment;

    //map object
    GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;

    //Location object used for getting latitude and longitude
    Location mLastLocation;

    private ClusterManager<MyItem> mClusterManager;
    private Marker marker2;
    private VolleyConnection mVolleyConnection;
    private String mensagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        buildGoogleApiClient();

        mVolleyConnection = new VolleyConnection(this);

        mIsLargeLayout = getResources().getBoolean(R.bool.large_layout);


       //FLOATING ACTION BUTTON

       final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_maps);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);
        ImageView rlIcon1 = new ImageView(this);
        ImageView rlIcon2 = new ImageView(this);

        rlIcon1.setImageDrawable(getResources().getDrawable(R.drawable.ic_exit_to_app_black_24dp));
        rlIcon2.setImageDrawable(getResources().getDrawable(R.drawable.ic_mode_edit_black_24dp));


        // Build the menu with default options: light theme, 90 degrees, 72dp radius.
        // Set 4 default SubActionButtons
        final FloatingActionMenu rightLowerMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(rLSubBuilder.setContentView(rlIcon1).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon2).build())
                .attachTo(fab)
                .build();

        // Listen menu open and close events to animate the button content view
        rightLowerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees clockwise
                fab.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fab, pvhR);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees counter-clockwise
                fab.setRotation(45);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fab, pvhR);
                animation.start();
            }
        });

        rlIcon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();

            }
        });

        rlIcon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, ComentariosActivity.class);
                startActivity(intent);
            }
        });
        onConnected(savedInstanceState);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    private void gotoLocation(double lat,double lng,float zoom) {
        LatLng latLng=new LatLng(lat,lng);
        CameraUpdate update= CameraUpdateFactory.newLatLngZoom(latLng,zoom);
        mMap.moveCamera(update);
    }

    /*
    Checking the google play services is available
     */
    private boolean checkServices() {
        //returns a integer value
        int isAvailable= GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        //if connection is available
        if (isAvailable== ConnectionResult.SUCCESS){
            return true;
        }else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)){
            Dialog dialog=GooglePlayServicesUtil.getErrorDialog(isAvailable,this,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else {
            Toast.makeText(MapsActivity.this, "Cannot connnect to mapping Service", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /*
    Initializing the map
     */
    private boolean initMap() {


        if (mMap == null) {
            SupportMapFragment mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mMap=mapFragment.getMap();

            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);


        }
        return (mMap!=null);
    }


    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {

            //getting the latitude value
            final double latitudeValue = mLastLocation.getLatitude();
            //getting the longitude value
            final double longitudeValue = mLastLocation.getLongitude();

            if (checkServices()) {

                if (initMap()) {
                    //update the map with the current location
                    gotoLocation(latitudeValue, longitudeValue, 15);

                    // Other supported types include: MAP_TYPE_NORMAL,
                    // MAP_TYPE_TERRAIN, MAP_TYPE_HYBRID and MAP_TYPE_NONE

                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mMap.setMyLocationEnabled(true);

                    mMap.getProjection().toScreenLocation(new LatLng(latitudeValue, longitudeValue));

                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(final LatLng point) {

                            //TIPOS DE OCORRÊNCIA
                            final String[] items = new String[]{"Alagamento", "Assalto", "Árvore Caída", "Bueiro Aberto", "Buraco", "Poste Caído/Sem Luz"};

                            //CAIXA DE DIÁLOGO

                            final AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                            final Double latitude = point.latitude;
                            final Double longitude = point.longitude;
                            final int status = 1;
                            final String alagamento = "Alagamento";
                            final String assalto = "Assalto";
                            final String arvore = "Arvore";
                            final String bueiro = "Bueiro";
                            final String buraco = "Buraco";
                            final String poste = "Poste";

                            builder.setIcon(R.drawable.icon_maps3);

                                 builder.setTitle("Selecione a ocorrência: ")

                                            .setItems(items, new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int which) {

                                                    intent = getIntent();
                                                    params = intent.getExtras();

                                                    if (params != null) {

                                                        dado = params.getString("mensagem").toString();

                                                        Log.d("RESPOSTA: ", "O EMAIL DA PESSOA EH " + dado);

                                                    }
                                                    if (params == null) {
                                                        Log.d("RESPOSTA: ", "NAO PEGUEI O EMAIL");
                                                    }

                                                    if (which == 0) {

                                                        Toast.makeText(getApplicationContext(), "Alagamento", Toast.LENGTH_SHORT).show();

                                                                mensagem = latitude + ", " + longitude + ", obs, " + dado + ", " + status + ", " + alagamento;
                                                                final String dados = mensagem;

                                                                try {

                                                                    json = mVolleyConnection.callServerApiByJsonObjectRequest(ServerInfo.SERVER_URL, "sinalizarOcorrencia", dados, "maps");
                                                                    customAddMarker(new LatLng(point.latitude, point.longitude), "Alagamento", "Possível ocorrência de Alagamento", 0);

                                                                    if (json.equalsIgnoreCase("OK")) {
                                                                        Log.d("MENSAGEM", dados);


                                                                        Toast.makeText(getApplicationContext(), "Ocorrência registrada!", Toast.LENGTH_SHORT).show();

                                                                    } else {
                                                                     //   Toast.makeText(getApplicationContext(), "Ocorrência não registrada!", Toast.LENGTH_SHORT).show();
                                                                        Log.d("MENSAGEM", "NÃO FOI PRO BANCO");
                                                                        Log.d("MENSAGEM DADOS", dados);
                                                                    }

                                                                } catch (Exception e) {
                                                                    Log.i("RESPOSTA EXCEPTION", e.toString());

                                                                }

                                                                    dialog.dismiss();
                                                            }

                                                    if (which == 1) {
                                                        Toast.makeText(getApplicationContext(), "Assalto", Toast.LENGTH_SHORT).show();

                                                        mensagem = latitude + ", " + longitude + ", obs, " + dado + ", " + status + ", " + assalto;
                                                        final String dados = mensagem;
                                                        try {

                                                            json = mVolleyConnection.callServerApiByJsonObjectRequest(ServerInfo.SERVER_URL, "sinalizarOcorrencia", dados, "maps");
                                                            customAddMarker(new LatLng(point.latitude, point.longitude), "Assalto", "Possível Ocorrência de Assaltos", 1);
                                                            if (json.equalsIgnoreCase("OK")) {
                                                                Log.d("MENSAGEM", dados);

                                                                Toast.makeText(getApplicationContext(), "Ocorrência registrada!", Toast.LENGTH_SHORT).show();

                                                            } else {
                                                                //Toast.makeText(getApplicationContext(), "Ocorrência não registrada!", Toast.LENGTH_SHORT).show();

                                                                Log.d("MENSAGEM", "NÃO FOI PRO BANCO");
                                                                Log.d("MENSAGEM DADOS", dados);
                                                                Log.d("MENSAGEM DADO", dado);
                                                                Log.d("MENSAGEM C", comentario);
                                                            }

                                                        } catch (Exception e) {
                                                            Log.i("RESPOSTA EXCEPTION", e.toString());

                                                        }

                                                    }
                                                    if (which == 2) {
                                                        Toast.makeText(getApplicationContext(), "Árvore", Toast.LENGTH_SHORT).show();

                                                        mensagem = latitude + ", " + longitude + ", obs, " + dado + ", " + status + ", " + arvore;
                                                        final String dados = mensagem;
                                                        try {

                                                            json = mVolleyConnection.callServerApiByJsonObjectRequest(ServerInfo.SERVER_URL, "sinalizarOcorrencia", dados, "maps");
                                                            customAddMarker(new LatLng(point.latitude, point.longitude), "Árvore Caída", "Possível ocorrência de Árvore Caída", 2);
                                                            if (json.equalsIgnoreCase("OK")) {

                                                                Log.d("MENSAGEM", dados);



                                                                Toast.makeText(getApplicationContext(), "Ocorrência registrada!", Toast.LENGTH_SHORT).show();

                                                            } else {
                                                                //Toast.makeText(getApplicationContext(), "Ocorrência não registrada!", Toast.LENGTH_SHORT).show();
                                                                Log.d("MENSAGEM", "NÃO FOI PRO BANCO");
                                                                Log.d("MENSAGEM DADOS", dados);
                                                            }

                                                        } catch (Exception e) {
                                                            Log.i("RESPOSTA EXCEPTION", e.toString());

                                                        }

                                                    }
                                                    if (which == 3) {

                                                        Toast.makeText(getApplicationContext(), "Bueiro", Toast.LENGTH_SHORT).show();
                                                        mensagem = latitude + ", " + longitude + ", obs, " + dado + ", " + status + ", " + bueiro;

                                                        final String dados = mensagem;
                                                        try {

                                                            json = mVolleyConnection.callServerApiByJsonObjectRequest(ServerInfo.SERVER_URL, "sinalizarOcorrencia", dados, "maps");
                                                            customAddMarker(new LatLng(point.latitude, point.longitude), "Bueiro Aberto", "Possível ocorrência de bueiro aberto", 3);
                                                            if (json.equalsIgnoreCase("OK")) {

                                                                Log.d("MENSAGEM", dados);



                                                                Toast.makeText(getApplicationContext(), "Ocorrência registrada!", Toast.LENGTH_SHORT).show();

                                                            } else {
                                                                //Toast.makeText(getApplicationContext(), "Ocorrência não registrada!", Toast.LENGTH_SHORT).show();
                                                                Log.d("MENSAGEM", "NÃO FOI PRO BANCO");
                                                                Log.d("MENSAGEM DADOS", dados);
                                                            }

                                                        } catch (Exception e) {
                                                            Log.i("RESPOSTA EXCEPTION", e.toString());

                                                        }

                                                    }
                                                    if (which == 4) {

                                                        Toast.makeText(getApplicationContext(), "Buraco", Toast.LENGTH_SHORT).show();

                                                        mensagem = latitude + ", " + longitude + ", obs, " + dado + ", " + status + ", " + buraco;
                                                        final String dados = mensagem;

                                                        try {

                                                            json = mVolleyConnection.callServerApiByJsonObjectRequest(ServerInfo.SERVER_URL, "sinalizarOcorrencia", dados, "maps");
                                                            customAddMarker(new LatLng(point.latitude, point.longitude), "Buraco", "Possível ocorrência de Buraco", 4);
                                                            if (json.equalsIgnoreCase("OK")) {
                                                                Log.d("MENSAGEM", dados);



                                                                Toast.makeText(getApplicationContext(), "Ocorrência registrada!", Toast.LENGTH_SHORT).show();

                                                            } else {
                                                                //Toast.makeText(getApplicationContext(), "Ocorrência não registrada!", Toast.LENGTH_SHORT).show();
                                                                Log.d("MENSAGEM", "NÃO FOI PRO BANCO");
                                                                Log.d("MENSAGEM DADOS", dados);
                                                            }

                                                        } catch (Exception e) {
                                                            Log.i("RESPOSTA EXCEPTION", e.toString());

                                                        }
                                                    }
                                                    if (which == 5) {

                                                        Toast.makeText(getApplicationContext(), "Poste", Toast.LENGTH_SHORT).show();

                                                        mensagem = latitude + ", " + longitude + ", obs, " + dado + ", " + status + ", " + poste;
                                                        final String dados = mensagem;
                                                        try {

                                                            json = mVolleyConnection.callServerApiByJsonObjectRequest(ServerInfo.SERVER_URL, "sinalizarOcorrencia", dados, "maps");
                                                            customAddMarker(new LatLng(point.latitude, point.longitude), "Poste Caído/Sem Luz", "Possível ocorrência de Poste Caído/Sem Luz", 5);
                                                            if (json.equalsIgnoreCase("OK")) {

                                                                Log.d("MENSAGEM", dados);

                                                                Toast.makeText(getApplicationContext(), "Ocorrência registrada!", Toast.LENGTH_SHORT).show();

                                                            } else {
                                                              //  Toast.makeText(getApplicationContext(), "Ocorrência não registrada!", Toast.LENGTH_SHORT).show();
                                                                Log.d("MENSAGEM", "NÃO FOI PRO BANCO");
                                                                Log.d("MENSAGEM DADOS", dados);
                                                            }

                                                        } catch (Exception e) {
                                                            Log.i("RESPOSTA EXCEPTION", e.toString());

                                                        }
                                                    }
                                                }
                                            });

                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();

                                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                        @Override
                                        public boolean onMarkerClick(final Marker marker) {

                                            Geocoder gCoder = new Geocoder(MapsActivity.this);
                                            List<Address> addresses = null;
                                            try {
                                                addresses = gCoder.getFromLocation(point.latitude, point.longitude, 1);

                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            if (addresses != null && addresses.size() > 0) {

                                                new AlertDialogWrapper.Builder(MapsActivity.this)

                                                        .setTitle("Possível ocorrência de " + marker.getTitle()) //PEGA O TÍTULO DA OPÇÃO ESCOLHIDA

                                                        .setMessage("Endereço: " + addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getAddressLine(1) + ", " + addresses.get(0).getAddressLine(2))

                                                        .setNegativeButton("Sair", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                            }

                                                        }).setPositiveButton("Liberar ocorrência", new DialogInterface.OnClickListener() {

                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

                                                                mensagem = 6 + ", " + dados;
                                                                final String dados = mensagem;
                                                                try {

                                                                    json = mVolleyConnection.callServerApiByJsonObjectRequest(ServerInfo.SERVER_URL, "liberarOcorrencia", dados, "maps");
                                                                    marker.remove();
                                                                    if (json.equalsIgnoreCase("OK")) {
                                                                        //marker.remove();
                                                                        Toast.makeText(getApplicationContext(), "Ocorrência liberada!", Toast.LENGTH_SHORT).show();

                                                                    } else {
                                                                        Log.d("MENSAGEM", "NÃO LIBEROU A OCORRÊNCIA");
                                                                        //Toast.makeText(getApplicationContext(), "Ocorrência não liberada!", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                } catch (Exception e) {
                                                                    Log.i("RESPOSTA EXCEPTION", e.toString());
                                                                }
                                                            }
                                                        }

                                                ).

                                                        setNeutralButton("Adicionar Comentário", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface
                                                                                                dialog, int which) {
                                                                        Intent intent = new Intent(MapsActivity.this, ComentariosActivity.class);
                                                                        startActivity(intent);
                                                                    }
                                                                }

                                                        ).

                                                        show();
                                            }

                                            mMap.getMyLocation();


                                            return false;

                                        }
                                    });
                            }
                    });
                }
            }
        }
    }

    public void customAddMarker(LatLng latLng, String title, String snippet, int icon){
        MarkerOptions options = new MarkerOptions();
        options.position(latLng).title(title).snippet(snippet).draggable(true);
        if (icon == 0){
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_alagamento)); //DESENHO DO MARCADOR
        }
        if (icon == 1){
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_assalto)); //DESENHO DO MARCADOR
        }
        if (icon == 2){
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_arvore)); //DESENHO DO MARCADOR
        }
        if (icon == 3){
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bueiro)); //DESENHO DO MARCADOR
        }

        if (icon == 4){
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_buraco)); //DESENHO DO MARCADOR
        }
        if (icon == 5){
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_poste)); //DESENHO DO MARCADOR
        }
        marker2 = mMap.addMarker(options);
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
    public void onLocationChanged(Location location) {

    }



    @Override
    public CharSequence[] onCreateSingleChoiceArray(SimpleAlertDialog dialog, int requestCode) {
        return new CharSequence[0];
    }

    @Override
    public void onSingleChoiceArrayItemClick(SimpleAlertDialog dialog, int requestCode, int position) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("GettingLocation", "onConnectionFailed");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void setUpClusterer() {
        // Declare a variable for the cluster manager.


        // Position the map.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 10));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<MyItem>(this, mMap);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        // Add cluster items (markers) to the cluster manager.
        addItems();
    }

    private void addItems() {

        // Set some lat/lng coordinates to start with.
        double latitudeValue=mLastLocation.getLatitude();
        //getting the longitude value
        double longitudeValue=mLastLocation.getLongitude();

        // Add ten cluster items in close proximity, for purposes of this example.
        for (int i = 0; i < 10; i++) {
            double offset = i / 60d;
            latitudeValue = latitudeValue + offset;
            longitudeValue = longitudeValue + offset;
            MyItem offsetItem = new MyItem(latitudeValue, longitudeValue);
            mClusterManager.addItem(offsetItem);


        }
    }


    public  void logout(View view){
        SharedPreferences sharedpreferences = getSharedPreferences(LoginAppActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();
    }

    public void close(View view){
        finish();
    }


}
