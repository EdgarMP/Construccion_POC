package edgarmarcopolo.com.construccion_poc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class DetalleEncuestaActivity extends Activity {


    private Encuesta mEncuesta = null;
    private LinearLayout mEncuestaLayout;
    private Button mEnviarButton;
    private Button mTomarFotoButton;
    private String tag_json_obj = "jobj_req";
    private ProgressDialog mDialog;
    private TextView mTiendaIdText;
    private boolean mFileExist = false;
    private String mNombreArchivo = null;
    private String mRespuestasPasadas = null;
    private boolean mRecuperarEncuesta = false;
    private String mCoordenadasInicio = null;
    private String mIdFotos = null;
    private SharedPreferences mPrefs = null;
    private String mUsuarioId = null;
    private RadioGroup mRadioGroupConstRem = null;

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private double mLatitude = 0.0;
    private double mLongitude = 0.0;
    private double mAccuracy = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_encuesta);

        // Acquire a reference to the system Location Manager
        mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Cargando...");
        mDialog.setCancelable(false);
        showProgressDialog();

        //Sacando el id de la encuesta
        Bundle b = getIntent().getExtras();
        mEncuesta = new Encuesta();
        mEncuesta = b.getParcelable("encuesta_a_mostrar");


        //Sacando las coordenadas de inicio guardadas.
        mPrefs = getSharedPreferences(Constantes.MY_PREFS_NAME, MODE_PRIVATE);
        mIdFotos = mPrefs.getString(Constantes.ID_TIENDA, null)+"_"+mEncuesta.getEncuestaId();
        mCoordenadasInicio = mPrefs.getString(Constantes.COORDENADAS_INICIO, null);
        mUsuarioId = mPrefs.getString(Constantes.USER_ID, null);
        Log.w("Coordenadas Inicio", " "+mCoordenadasInicio);
        Log.w("ID Fotos", mIdFotos);
        //UI
        mTiendaIdText = (TextView)findViewById(R.id.encuesta_detalle_id_tienda);
        mEncuestaLayout = (LinearLayout)findViewById(R.id.encuesta_detalle_layout);
        mEnviarButton = (Button)findViewById(R.id.encuesta_boton_enviar);
        mTomarFotoButton = (Button)findViewById(R.id.encuesta_boton_capturar_foto);
        mRadioGroupConstRem = (RadioGroup)findViewById(R.id.encuesta_detalle_const_rem);
        mEnviarButton.setEnabled(false);

        //Rojas y Amarillas
        if(mEncuesta.getEncuestaId()==3){
            mTomarFotoButton.setVisibility(View.VISIBLE);
            mTomarFotoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent camaraActivityIntent = new Intent(DetalleEncuestaActivity.this, CamaraActivity.class);
                    camaraActivityIntent.putExtra("preguntaId", 0);
                    camaraActivityIntent.putExtra("encuestaTiendaId", mEncuesta.getEncuestaTiendaId());
                    DetalleEncuestaActivity.this.startActivity(camaraActivityIntent);
                }
            });

        }

        mTiendaIdText.setText(mEncuesta.getEncuestaTiendaId());

        //mEncuesta.getEncuestaTiendaId() = Constantes.ID_TIENDA = mIdFotos.
        mNombreArchivo = mPrefs.getString(Constantes.ID_TIENDA_ENCUESTA, null)+".txt";


        Log.w("mNombreArchivo", mNombreArchivo);
        String tiendaEncuesta = mPrefs.getString(Constantes.ID_TIENDA_ENCUESTA, null);
        Log.w("ConsID_TIENDA_ENCUESTA", tiendaEncuesta);

        //Checa si existe un archivo de una encuesta anterior
        String[] savedFiles = fileList();
        for(int i =0; i<savedFiles.length; i++){
            Log.w("nombre de archivos",savedFiles[i]);
            if(mNombreArchivo.equals(savedFiles[i])){
                mFileExist = true;
            }
        }

        if(mFileExist){
            //Alert Dialog
            new AlertDialog.Builder(this)
                    .setTitle("Encuesta Encontrada")
                    .setMessage("Se ha encontrado una encuesta existente. Deseas recuperarla?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mRecuperarEncuesta = true;
                            getPreguntas(); //loadJSONFromAsset();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            getPreguntas();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }else{
            getPreguntas(); //loadJSONFromAsset();
        }

        mEnviarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hacer un HttpPost!
                mLocationManager.removeUpdates(mLocationListener);
                UploadFilesTask uploadFilesTask = new UploadFilesTask(DetalleEncuestaActivity.this);
                uploadFilesTask.execute();


            }
        });
    }

    @Override
    protected void onPause(){

        Log.w("onPause", "onPause");
        if(mLocationManager!=null && mLocationListener!=null){
            mLocationManager.removeUpdates(mLocationListener);
        }

        mLocationListener = null;
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.w("onResume", "onResume");
        startLocationServices();
    }

    private void getPreguntas(){
        Log.v("asdf", Constantes.PREGUNTAS_SERVLET + mEncuesta);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, Constantes.PREGUNTAS_SERVLET+mEncuesta.getEncuestaId(), null, new Response.Listener<JSONObject>() {
        //JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, Constantes.PREGUNTAS_SERVLET, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.w("EncuestaId", ""+mEncuesta);
                Log.w("asdf", jsonObject.toString());

                try {
                    int encuestaId = jsonObject.getInt("encuesta_id");
                    JSONArray listaPreguntas = jsonObject.getJSONArray("lista_preguntas");
                    for(int i=0; i<listaPreguntas.length(); i++){
                        JSONObject preguntaJsonObject = listaPreguntas.getJSONObject(i);
                        String preguntaTipo = preguntaJsonObject.getString("pregunta_tipo");

                        if(preguntaTipo.equals(Constantes.PREGUNTA_SPINNER) || preguntaTipo.equals(Constantes.PREGUNTA_CHECKBOX) || preguntaTipo.equals(Constantes.PREGUNTA_RADIO_BUTTON)){
                            PreguntaMultiple pregunta = new PreguntaMultiple();
                            pregunta.setEncuestaId(encuestaId);
                            pregunta.setPreguntaId(preguntaJsonObject.getInt("pregunta_id"));
                            pregunta.setPreguntaTexto(preguntaJsonObject.getString("pregunta_texto"));
                            pregunta.setPreguntaTipo(preguntaJsonObject.getString("pregunta_tipo"));
                            pregunta.setSubseccion(preguntaJsonObject.getString("pregunta_subseccion"));

                            JSONArray respuestasJSONArray = preguntaJsonObject.getJSONArray("respuestas");
                            ArrayList<Respuesta> respuestas = new ArrayList<Respuesta>();

                            for(int j=0; j<respuestasJSONArray.length(); j++){
                                JSONObject respuestaJSONObject = respuestasJSONArray.getJSONObject(j);


                                Respuesta respuestaObject = new Respuesta();
                                respuestaObject.setRespuestaId(respuestaJSONObject.getInt("respuesta_id"));
                                respuestaObject.setRespuestaTexto(respuestaJSONObject.getString("respuesta_texto"));
                                respuestas.add(respuestaObject);
                            }
                            pregunta.setPreguntaRespuesas(respuestas);
                            PreguntaMultipleView preguntaMultipleView = new PreguntaMultipleView(getApplicationContext(), DetalleEncuestaActivity.this, pregunta, mEncuesta);
                            mEncuestaLayout.addView(preguntaMultipleView);

                        }else if(preguntaTipo.equals(Constantes.PREGUNTA_SENCILLA_TEXT)){
                            PreguntaSencillaTexto pregunta = new PreguntaSencillaTexto();
                            pregunta.setEncuestaId(encuestaId);
                            pregunta.setPreguntaId(preguntaJsonObject.getInt("pregunta_id"));
                            pregunta.setPreguntaTexto(preguntaJsonObject.getString("pregunta_texto"));
                            pregunta.setPreguntaTipo(preguntaJsonObject.getString("pregunta_tipo"));
                            pregunta.setSubseccion(preguntaJsonObject.getString("pregunta_subseccion"));

                            PreguntaSencillaTextoView preguntaSencillaTextoView = new PreguntaSencillaTextoView(getApplicationContext(), DetalleEncuestaActivity.this, pregunta, mEncuesta.getEncuestaTiendaId());
                            mEncuestaLayout.addView(preguntaSencillaTextoView);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(mRecuperarEncuesta){
                    ReadStoredFileTask readStoredFileTask = new ReadStoredFileTask(DetalleEncuestaActivity.this);
                    readStoredFileTask.execute();
                }
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
                    Toast.makeText(DetalleEncuestaActivity.this, "No hay conexión a internet.", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(DetalleEncuestaActivity.this, "Ocurrió un error, vuelva a intentarlo de nuevo", Toast.LENGTH_LONG).show();
                }

                hideProgressDialog();
            }
        });

        //Retry Policy for Timeout. 5sec.
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(Constantes.TIMEOUT_MILLISEC, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

    @Override
    protected void onStop(){
        /*Al momento de que el usuario "salga" de la actividad se graba el archivo de texto para poder recuperar la encuesta */
        guardarArchivoDeTexto();
        super.onStop();
    }

    private void showProgressDialog() {
        if (!mDialog.isShowing())
            mDialog.show();
    }

    private void hideProgressDialog() {
        if (mDialog.isShowing())
            mDialog.dismiss();
    }

    public void guardarArchivoDeTexto() {
        Log.w("Empieza","guardarArchivoDeTexto");
        int encuestaLayoutCount = mEncuestaLayout.getChildCount(); //Deben ser el numero de preguntas.
        Log.w("Numero de Preguntas: ", ""+encuestaLayoutCount);
        //String FILENAME = mNombreArchivo;
        boolean guardaArchivo = false;
        FileOutputStream fos;
        try {
            fos = openFileOutput(mNombreArchivo, Context.MODE_PRIVATE);
            for (int i = 0; i < encuestaLayoutCount; i++) {

                if (mEncuestaLayout.getChildAt(i) instanceof PreguntaMultipleView) {
                    //Log.w("PreguntaMultiple", "view");
                    PreguntaMultipleView pmv = (PreguntaMultipleView) mEncuestaLayout.getChildAt(i);
                    if (pmv.getTipoPregunta().equals(Constantes.PREGUNTA_RADIO_BUTTON)) {
                        for (int j = 0; j < pmv.getRadioGroup().getChildCount(); j++) {
                            RadioButton rb = (RadioButton) pmv.getRadioGroup().getChildAt(j);

                            if (rb.isChecked()) {
                                guardaArchivo = true;
                                //Log.w("Respuestas Radios: ", rb.getText().toString());
                                String line = "preguntaId=" + pmv.getRadioGroup().getId() + ",respuestaTexto=" + rb.getText().toString() + "-";
                                Log.w("Write", line);
                                fos.write(line.getBytes());
                            }
                        }
                    } else if (pmv.getTipoPregunta().equals(Constantes.PREGUNTA_CHECKBOX)) {
                        LinearLayout linearLayout = (LinearLayout) pmv.findViewById(R.id.pregunta_multiple_respuestas);
                        for (int j = 0; j < linearLayout.getChildCount(); j++) {
                            CheckBox checkBox = (CheckBox) linearLayout.getChildAt(j);
                            if (checkBox.isChecked()) {
                                PreguntaMultiple preguntaMultiple = (PreguntaMultiple) checkBox.getTag();
                                for (int k = 0; k < preguntaMultiple.getPreguntaRespuesas().size(); k++) {
                                    Respuesta respuesta = preguntaMultiple.getPreguntaRespuesas().get(k);
                                    if (checkBox.getText().equals(respuesta.getRespuestaTexto())) {
                                        guardaArchivo = true;
                                        String line = "preguntaId=" + preguntaMultiple.getPreguntaId() + ",respuestaTexto=" + respuesta.getRespuestaTexto() + "-";
                                        Log.w("Write", line);
                                        fos.write(line.getBytes());
                                    }
                                }
                            }
                        }
                    } else if (pmv.getTipoPregunta().equals(Constantes.PREGUNTA_SPINNER)) {
                        LinearLayout linearLayout = (LinearLayout) pmv.findViewById(R.id.pregunta_multiple_respuestas);
                        for (int j = 0; j < linearLayout.getChildCount(); j++) {
                            Spinner spinner = (Spinner) linearLayout.getChildAt(j);
                            PreguntaMultiple preguntaMultiple = (PreguntaMultiple) spinner.getTag();
                            String respuestaTextoSpinner = spinner.getSelectedItem().toString();
                            for (int k = 0; k < preguntaMultiple.getPreguntaRespuesas().size(); k++) {
                                Respuesta respuesta = preguntaMultiple.getPreguntaRespuesas().get(k);
                                if (respuestaTextoSpinner.equals(respuesta.getRespuestaTexto())) {
                                    guardaArchivo = true;
                                    String line = "preguntaId=" + preguntaMultiple.getPreguntaId() + ",respuestaTexto=" + respuesta.getRespuestaTexto() + "-";
                                    Log.w("Write", line);
                                    fos.write(line.getBytes());
                                }
                            }
                        }
                    }
                } else if (mEncuestaLayout.getChildAt(i) instanceof PreguntaSencillaTextoView) {
                    Log.w("PreguntaSencilla", "view");
                    PreguntaSencillaTextoView preguntaSencillaTextoView = (PreguntaSencillaTextoView) mEncuestaLayout.getChildAt(i);
                    EditText editText = preguntaSencillaTextoView.getEditText();
                    if (editText.getText().toString().length() > 0) {
                        guardaArchivo = true;
                        PreguntaSencillaTexto preguntaSencillaTexto = (PreguntaSencillaTexto) editText.getTag();
                        String line = "preguntaId=" + preguntaSencillaTexto.getPreguntaId() + ",respuestaTexto=" + editText.getText().toString() + "-";
                        Log.w("Write", line);
                        fos.write(line.getBytes());
                    }
                }
            }


                fos.close();

            if(!guardaArchivo){
                deleteFile(mNombreArchivo);
            }else{
                Toast.makeText(this, "El archivo se guardó offline.", Toast.LENGTH_LONG).show();
            }



        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Exception: " + e.toString(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Exception: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void readStoredFile(String fileToRead){
        Log.w("readStoredFile","empieza");
        BufferedReader br = null;
        FileDescriptor fd = null;
        try {
            FileInputStream fis = openFileInput(fileToRead);
            fd = fis.getFD();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {

            br = new BufferedReader(new FileReader(fd));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            mRespuestasPasadas = sb.toString();
            Log.w("readStoredFile","Termina bien");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void llenarRespuestas(){
        if(mRespuestasPasadas != null){
            int encuestaLayoutCount = mEncuestaLayout.getChildCount();
            int primerId = ((PreguntaMultipleView) mEncuestaLayout.getChildAt(0)).getPreguntaId();
            String[] preguntaRespuesta = mRespuestasPasadas.split("-");
            for(int i=0; i<preguntaRespuesta.length; i++){
                String preguntaRespuesta2 = preguntaRespuesta[i]; //preguntaId=92,respuestaTexto=No Aplica
                String[] x = preguntaRespuesta2.split(",") ;
                String preguntaCompleta = x[0]; //preguntaId=92
                String respuestaCompleta = x[1]; //respuestaTexto=No Aplica
                String[] preguntaCompleta2 = preguntaCompleta.split("="); //preguntaId=92
                String[] respuestaCompleta2 = respuestaCompleta.split("=");
                int preguntaId = Integer.parseInt(preguntaCompleta2[1]); //92
                String respuestaTexto = respuestaCompleta2[1]; //No Aplica

                if(mEncuestaLayout.getChildAt(preguntaId - primerId) instanceof PreguntaMultipleView){
                    PreguntaMultipleView pmv = (PreguntaMultipleView) mEncuestaLayout.getChildAt(preguntaId - primerId);
                    if(pmv.getTipoPregunta().equals(Constantes.PREGUNTA_RADIO_BUTTON)){
                        for(int k=0; k<pmv.getRadioGroup().getChildCount(); k++){
                            RadioButton rb = (RadioButton)pmv.getRadioGroup().getChildAt(k);
                            if(pmv.getPreguntaId() == preguntaId && rb.getText().toString().equals(respuestaTexto)){
                                rb.setChecked(true);
                            }
                        }
                    }else if((pmv.getTipoPregunta().equals(Constantes.PREGUNTA_CHECKBOX))){
                        LinearLayout linearLayout = (LinearLayout) pmv.findViewById(R.id.pregunta_multiple_respuestas);
                        for(int j=0; j<linearLayout.getChildCount(); j++){
                            CheckBox checkBox = (CheckBox)linearLayout.getChildAt(j);
                            if(pmv.getPreguntaId() == preguntaId && checkBox.getText().toString().equals(respuestaTexto)){
                                checkBox.setChecked(true);
                            }
                        }
                    }else if((pmv.getTipoPregunta().equals(Constantes.PREGUNTA_SPINNER))){
                        LinearLayout linearLayout = (LinearLayout) pmv.findViewById(R.id.pregunta_multiple_respuestas);
                        for(int l=0; l<linearLayout.getChildCount(); l++){
                            Spinner spinner = (Spinner)linearLayout.getChildAt(l);
                            for(int y=0; y<spinner.getAdapter().getCount(); y++){
                                Log.w("Adapter Items" ,spinner.getAdapter().getItem(y).toString());
                                if(respuestaTexto.equals(spinner.getAdapter().getItem(y).toString())){
                                    spinner.setSelection(y);
                                }
                            }
                        }
                    }
                }else if(mEncuestaLayout.getChildAt(preguntaId - primerId) instanceof PreguntaSencillaTextoView){
                    Log.w("PreguntaSencilla","view");
                    PreguntaSencillaTextoView preguntaSencillaTextoView = (PreguntaSencillaTextoView) mEncuestaLayout.getChildAt(preguntaId - primerId);
                    EditText editText = preguntaSencillaTextoView.getEditText();
                    editText.setText(respuestaTexto);
                }
            }
        }else{
            Log.w("Llenar Respuestas", "mRespuestasPasadas NULL");
        }
    }

    public void uploadPictureToServer() throws ClientProtocolException, IOException {
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

        HttpPost httppost = new HttpPost(Constantes.IMAGES_SERVLET);

        //Listando las imagenes
        String root = Environment.getExternalStorageDirectory().toString();
        File folder = new File(root+"/saved_images");
        File[] listOfFiles = folder.listFiles();

        //Si hay imagenes que subir.
        if(listOfFiles != null){
            Log.v("Listoffiles", "NOT NULL");
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    System.out.println(file.getName());
                    String nombreArchivo = file.getName();
                    String idTiendaSubString = nombreArchivo.substring(0,11)+"_"+mEncuesta.getEncuestaId();

                    Log.w("UPLOAD", "idTiendaSubstring: "+idTiendaSubString);
                    Log.w("UPLOAD", "mIdFotos: "+mIdFotos);

                    if(idTiendaSubString.equals(mIdFotos)){
                        Log.w("Subiendo fotos", ":D");
                        MultipartEntity mpEntity = new MultipartEntity();
                        ContentBody cbFile = new FileBody(file, "image/jpeg");
                        //ContentBody cbFile = new FileBody(file, "text/plain");
                        mpEntity.addPart("userfile", cbFile);

                        httppost.setEntity(mpEntity);
                        System.out.println("executing request " + httppost.getRequestLine());
                        HttpResponse response = httpclient.execute(httppost);
                        HttpEntity resEntity = response.getEntity();

                        System.out.println(response.getStatusLine());
                        if (resEntity != null) {
                            System.out.println(EntityUtils.toString(resEntity));
                        }
                        if (resEntity != null) {
                            resEntity.consumeContent();
                        }
                    }
                }
            }
        }else{
            Log.v("Listoffiles", "NULL");
        }


        httpclient.getConnectionManager().shutdown();

    }

    public void sendData() throws IOException {

        InputStream inputStream = null;
        String result;

        // 1. create HttpClient
        HttpClient httpclient = new DefaultHttpClient();

        // 2. make POST request to the given URL
        HttpPost httpPost = new HttpPost(Constantes.DATA_SERVLET);

        String json;

        SharedPreferences mPrefs = getSharedPreferences(Constantes.MY_PREFS_NAME, MODE_PRIVATE);
        String crTienda = mPrefs.getString(Constantes.CR_TIENDA, null);
        String crPlaza = mPrefs.getString(Constantes.CR_PLAZA, null);
        String tipoRemConst = "";
        for(int i=0; i<mRadioGroupConstRem.getChildCount(); i++){
            RadioButton rb = (RadioButton)mRadioGroupConstRem.getChildAt(i);
            if(rb.isChecked()){
                tipoRemConst = rb.getText().toString();
            }

        }

        // 3. build jsonObject
        int encuestaLayoutCount = mEncuestaLayout.getChildCount(); //Deben ser el numero de preguntas.
        JSONObject preguntasContestadas = new JSONObject();

        try {
            preguntasContestadas.put("encuesta_id", mEncuesta.getEncuestaId());
            preguntasContestadas.put("coordenadas_inicio", mCoordenadasInicio);
            preguntasContestadas.put("coordenadas_finales", mLatitude+","+mLongitude);
            preguntasContestadas.put("usuario_id", mUsuarioId);
            preguntasContestadas.put("cr_tienda", crTienda);
            preguntasContestadas.put("cr_plaza", crPlaza);
            preguntasContestadas.put("tipo", tipoRemConst);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray listaPreguntaRespuesta = new JSONArray();

        for(int i=0; i<encuestaLayoutCount; i++){

            if(mEncuestaLayout.getChildAt(i) instanceof PreguntaMultipleView){
                //Log.w("PreguntaMultiple","view");
                PreguntaMultipleView pmv = (PreguntaMultipleView) mEncuestaLayout.getChildAt(i);

                if(pmv.getTipoPregunta().equals(Constantes.PREGUNTA_RADIO_BUTTON)){

                    for(int j=0; j<pmv.getRadioGroup().getChildCount(); j++){
                        PreguntaMultiple preguntaMultiple = (PreguntaMultiple)pmv.getRadioGroup().getTag();

                        RadioButton rb = (RadioButton)pmv.getRadioGroup().getChildAt(j);

                        if(rb.isChecked()){
                            JSONObject preguntacontestada = new JSONObject();
                            try {

                                preguntacontestada.put("respuesta_id", rb.getId()-pmv.getRadioGroup().getId());
                                preguntacontestada.put("pregunta_id", pmv.getRadioGroup().getId());
                                preguntacontestada.put("pregunta_tipo", preguntaMultiple.getPreguntaTipo());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String line = "preguntaId="+pmv.getRadioGroup().getId()+",respuestaTexto="+rb.getText().toString()+"-";
                            Log.w("Write",line);
                            listaPreguntaRespuesta.put(preguntacontestada);
                            preguntacontestada=null;
                        }
                    }

                }else if(pmv.getTipoPregunta().equals(Constantes.PREGUNTA_CHECKBOX)){
                    LinearLayout linearLayout = (LinearLayout) pmv.findViewById(R.id.pregunta_multiple_respuestas);
                    for(int j=0; j<linearLayout.getChildCount(); j++){
                        CheckBox checkBox = (CheckBox)linearLayout.getChildAt(j);
                        if(checkBox.isChecked()){
                            JSONObject preguntacontestada = new JSONObject();
                            PreguntaMultiple preguntaMultiple = (PreguntaMultiple)checkBox.getTag();
                            try {
                                for(int k=0; k<preguntaMultiple.getPreguntaRespuesas().size(); k++){
                                    Respuesta respuesta = preguntaMultiple.getPreguntaRespuesas().get(k);
                                    if(checkBox.getText().equals(respuesta.getRespuestaTexto())){
                                        preguntacontestada.put("respuesta_id", respuesta.getRespuestaId());
                                    }
                                }
                                preguntacontestada.put("pregunta_id", preguntaMultiple.getPreguntaId());
                                preguntacontestada.put("pregunta_tipo", preguntaMultiple.getPreguntaTipo());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            listaPreguntaRespuesta.put(preguntacontestada);
                            preguntacontestada=null;
                        }
                    }

                }else if(pmv.getTipoPregunta().equals(Constantes.PREGUNTA_SPINNER)){
                    LinearLayout linearLayout = (LinearLayout) pmv.findViewById(R.id.pregunta_multiple_respuestas);
                    for(int j=0; j<linearLayout.getChildCount(); j++){
                        Spinner spinner = (Spinner)linearLayout.getChildAt(j);
                        //if(spinner.isSelected()){
                        JSONObject preguntacontestada = new JSONObject();
                        PreguntaMultiple preguntaMultiple = (PreguntaMultiple)spinner.getTag();
                        try {
                            String respuestaTextoSpinner = spinner.getSelectedItem().toString();
                            for(int k=0; k<preguntaMultiple.getPreguntaRespuesas().size(); k++){
                                Respuesta respuesta = preguntaMultiple.getPreguntaRespuesas().get(k);
                                if(respuestaTextoSpinner.equals(respuesta.getRespuestaTexto())){
                                    preguntacontestada.put("respuesta_id", respuesta.getRespuestaId());
                                }
                            }

                            preguntacontestada.put("pregunta_id", preguntaMultiple.getPreguntaId());
                            preguntacontestada.put("pregunta_tipo", preguntaMultiple.getPreguntaTipo());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        listaPreguntaRespuesta.put(preguntacontestada);
                        preguntacontestada=null;
                        //}
                    }
                }
            }else if(mEncuestaLayout.getChildAt(i) instanceof PreguntaSencillaTextoView){
                Log.w("PreguntaSencilla","view");
                PreguntaSencillaTextoView preguntaSencillaTextoView = (PreguntaSencillaTextoView) mEncuestaLayout.getChildAt(i);
                EditText editText = preguntaSencillaTextoView.getEditText();
                JSONObject preguntacontestada = new JSONObject();
                if(editText.getText().toString().length()>0){
                    PreguntaSencillaTexto preguntaSencillaTexto = (PreguntaSencillaTexto)editText.getTag();

                    try {
                        preguntacontestada.put("pregunta_id", preguntaSencillaTexto.getPreguntaId());
                        preguntacontestada.put("pregunta_tipo", preguntaSencillaTexto.getPreguntaTipo());
                        preguntacontestada.put("respuesta_id", 0);
                        preguntacontestada.put("respuesta_contestada", editText.getText().toString());
                        listaPreguntaRespuesta.put(preguntacontestada);
                        preguntacontestada=null;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                preguntasContestadas.put("lista_preguntas_respuestas", listaPreguntaRespuesta);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // 4. convert JSONObject to JSON to String
        json = preguntasContestadas.toString();
        Log.w("JSON Send Data", json);

        // 5. set json to StringEntity
        StringEntity se = null;
        try {
            se = new StringEntity(json);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 6. set httpPost Entity
        httpPost.setEntity(se);

        // 7. Set some headers to inform server about the type of the content
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        // 8. Execute POST request to the given URL
          try {
            HttpResponse httpResponse = httpclient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void deleteLocalImages(){

        //Listando las imagenes
        String root = Environment.getExternalStorageDirectory().toString();
        File folder = new File(root+"/saved_images");
        File[] listOfFiles = folder.listFiles();
        if(listOfFiles!=null){
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    System.out.println(file.getName());
                    String nombreArchivo = file.getName();
                    String idTiendaSubString = nombreArchivo.substring(0,11)+"_"+mEncuesta.getEncuestaId();
                    Log.w("mIdFotos", mIdFotos);
                    Log.w("idTiendaSubString", idTiendaSubString);
                    if(idTiendaSubString.equals(mIdFotos)){
                        Log.w("Borrando fotos", ":(");
                        file.delete();
                    }
                }
            }
        }
    }

    private class UploadFilesTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog;

        public UploadFilesTask(Activity activity){
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Subiendo imágenes ...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... progress) {

        }

        @Override
        protected void onPostExecute(Void result) {
            //showDialog("Downloaded " + result + " bytes");

            if(mNombreArchivo!=null){
                deleteFile(mNombreArchivo);
            }

            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }

            Toast.makeText(DetalleEncuestaActivity.this, "Las imágenes se han subido exitosamente", Toast.LENGTH_LONG).show();

            DeleteLocalFilesTask deleteLocalFilesTask = new DeleteLocalFilesTask(DetalleEncuestaActivity.this);
            deleteLocalFilesTask.execute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                uploadPictureToServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class ReadStoredFileTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog;
        //Constructor
        public ReadStoredFileTask(Activity activity){
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Leyendo archivo local...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.w("OnPostExecute", "Llenar Respuestas");

            llenarRespuestas();

            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            readStoredFile(mNombreArchivo);
            return null;
        }
    }

    private class DeleteLocalFilesTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog;

        public DeleteLocalFilesTask(Activity activity){
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Borrando imágenes en el dispositivo...");
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... progress) {

        }

        @Override
        protected void onPostExecute(Void result) {
            //showDialog("Downloaded " + result + " bytes");



            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }

            Toast.makeText(DetalleEncuestaActivity.this, "Las imágenes se han borrado exitosamente", Toast.LENGTH_LONG).show();

            SendDataTask sendDataTask = new SendDataTask(DetalleEncuestaActivity.this);
            sendDataTask.execute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            deleteLocalImages();
            return null;
        }
    }

    private class SendDataTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog;

        public SendDataTask(Activity activity){
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Enviando datos de la encuesta...");
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... progress) {

        }

        @Override
        protected void onPostExecute(Void result) {

            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }

            Toast.makeText(DetalleEncuestaActivity.this, "Se han subido los datos exitosamente", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(DetalleEncuestaActivity.this, IdentificadorTiendaActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                sendData();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void startLocationServices(){

        // Define a listener that responds to location updates
        mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                Log.w("Accuracy ", ""+location.getAccuracy());
                mAccuracy = location.getAccuracy();
                mLatitude = location.getLatitude();
                mLongitude = location.getLongitude();
                if((mLatitude != 0.0 && mLongitude != 0.0) && mAccuracy < Constantes.STRONG_ACCURACY){
                    mEnviarButton.setEnabled(true);
                }

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {
                mEnviarButton.setText("Enviar");
            }
            public void onProviderDisabled(String provider) {
                mEnviarButton.setText("Prender GPS");
            }
        };
        // Register the listener with the Location Manager to receive location updates
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, mLocationListener);
    }
}