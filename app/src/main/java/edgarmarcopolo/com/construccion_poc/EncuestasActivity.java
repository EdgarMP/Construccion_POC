package edgarmarcopolo.com.construccion_poc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.Request.Method;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class EncuestasActivity extends ActionBarActivity {

    private ArrayList<Encuesta> mListaEncuestas = null;
    private ListView mListViewEncuestas;
    private TextView mEmptyViewEncuestas;
    private EncuestaAdapter mEncuestaAdapter;
    // These tags will be used to cancel the requests
    private String tag_json_obj = "jobj_req";
    private ProgressDialog mDialog;
    private String mTiendaId = "";
    private TextView mTiendaIdText;
    private SharedPreferences mPrefs = null;
    private SharedPreferences.Editor editor = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //SharedPreferences
        editor = getSharedPreferences(Constantes.MY_PREFS_NAME, MODE_PRIVATE).edit();
        mPrefs = getSharedPreferences(Constantes.MY_PREFS_NAME, MODE_PRIVATE);
        mTiendaId = mPrefs.getString(Constantes.ID_TIENDA, null);

        //UI
        mTiendaIdText = (TextView)findViewById(R.id.encuesta_id_tienda_text);
        mListViewEncuestas = (ListView)findViewById(R.id.list_view_encuestas);
        mEmptyViewEncuestas = (TextView)findViewById(R.id.text_view_encuestas_empty);
        mTiendaIdText.setText(mTiendaId);

        //Desplegando el Dialogo de Cargando...
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Cargando...");
        mDialog.setCancelable(false);

        showProgressDialog();

        mListaEncuestas = new ArrayList<Encuesta>();
        //loadJSONFromAsset();
        getEncuestas();
        mListViewEncuestas.setEmptyView(mEmptyViewEncuestas);

    }

    //Peticion por web service usando Volley.
    private void getEncuestas(){
        Log.v("asdf","Datos Encuesta");
        //JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET, "http://yourjson.com/yj/serv/167", null, new Response.Listener<JSONObject>() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET, Constantes.ENCUESTAS_SERVLET, null, new Response.Listener<JSONObject>() {
        //JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET, "http://192.168.1.166:8080/AppConsutruccion/EncuestasServlet", null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("asdf", jsonObject.toString());
                try {
                    JSONArray listaEncuestas = jsonObject.getJSONArray("lista_encuestas");

                    if(listaEncuestas!=null){
                        for(int i=0; i<listaEncuestas.length(); i++){
                            JSONObject encuestaJSON = listaEncuestas.getJSONObject(i);

                            //Creating Encuesta object.
                            Encuesta encuesta = new Encuesta();
                            encuesta.setEncuestaId(encuestaJSON.getInt("encuesta_id"));
                            encuesta.setEncuestaNombre(encuestaJSON.getString("nombre_encuesta"));
                            encuesta.setEncuestaTiendaId(mTiendaId);

                            mListaEncuestas.add(encuesta);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mEncuestaAdapter =new EncuestaAdapter(EncuestasActivity.this, mListaEncuestas);
                mListViewEncuestas.setAdapter(mEncuestaAdapter);
                mTiendaIdText.setVisibility(View.VISIBLE);
                hideProgressDialog();


            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", "Error: " + error.getMessage());
                boolean noConnection = true;
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    // HTTP Status Code: 401 Unauthorized
                    Log.e("Volley", "Error. HTTP Status Code:"+networkResponse.statusCode);
                }

                if (error instanceof TimeoutError) {
                    Log.e("Volley", "TimeoutError");
                }else if(error instanceof NoConnectionError){
                    Log.e("Volley", "NoConnectionError");
                    noConnection = false;
                } else if (error instanceof AuthFailureError) {
                    Log.e("Volley", "AuthFailureError");
                } else if (error instanceof ServerError) {
                    Log.e("Volley", "ServerError");
                } else if (error instanceof NetworkError) {
                    Log.e("Volley", "NetworkError");
                } else if (error instanceof ParseError) {
                    Log.e("Volley", "ParseError");
                }

                if(!noConnection){
                    Toast.makeText(EncuestasActivity.this, "No hay conexión a internet.", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(EncuestasActivity.this, "Ocurrió un error, vuelva a intentarlo de nuevo", Toast.LENGTH_LONG).show();
                }

                hideProgressDialog();
            }
        });

        //Retry Policy for Timeout. 5sec.
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(Constantes.TIMEOUT_MILLISEC, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void onTituloClick(int position){
        Log.v("onTituloClick","Position: "+position);
        Encuesta encuesta = new Encuesta();
        encuesta = mListaEncuestas.get(position);

        editor.putString(Constantes.ID_TIENDA_ENCUESTA, mTiendaId+"_"+encuesta.getEncuestaId());
        editor.commit();

        Intent intentDetalleFoodActivity = new Intent(EncuestasActivity.this, DetalleEncuestaActivity.class);
        intentDetalleFoodActivity.putExtra("encuesta_a_mostrar", encuesta);
        startActivity(intentDetalleFoodActivity);
    }

    private void showProgressDialog() {
        if (!mDialog.isShowing())
            mDialog.show();
    }

    private void hideProgressDialog() {
        if (mDialog.isShowing())
            mDialog.dismiss();
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = getAssets().open("encuestas.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

            JSONObject jsonObject = new JSONObject(json);

            JSONArray listaEncuestas = jsonObject.getJSONArray("lista_encuestas");

            if(listaEncuestas!=null){
                for(int i=0; i<listaEncuestas.length(); i++){
                    JSONObject encuestaJSON = listaEncuestas.getJSONObject(i);

                    //Creating Encuesta object.
                    Encuesta encuesta = new Encuesta();
                    encuesta.setEncuestaId(encuestaJSON.getInt("encuesta_id"));
                    encuesta.setEncuestaNombre(encuestaJSON.getString("nombre_encuesta"));
                    encuesta.setEncuestaTiendaId(mTiendaId);

                    mListaEncuestas.add(encuesta);
                }
            }

            mEncuestaAdapter =new EncuestaAdapter(EncuestasActivity.this, mListaEncuestas);
            mListViewEncuestas.setAdapter(mEncuestaAdapter);
            mTiendaIdText.setVisibility(View.VISIBLE);
            hideProgressDialog();

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;

    }
}
