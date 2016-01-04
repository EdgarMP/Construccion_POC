package edgarmarcopolo.com.construccion_poc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class IdentificadorTiendaActivity extends Activity {

    private Context mContext;
    private Button entrarButton;
    //private TextView idTiendaText;
    private TextView idTiendaSenalGPS;

    private EditText idTiendaCrPlazaInput;
    private EditText idTiendaCrTiendaInput;

    private Spinner spinnerGPS;


    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private AppUtils mAppUtils = new AppUtils();
    private double mLatitude = 0.0;
    private double mLongitude = 0.0;
    private double mAccuracy = 0.0;
    private SharedPreferences.Editor editor = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identificador_tienda);

        entrarButton = (Button)findViewById(R.id.id_tienda_entrar_button);
        idTiendaSenalGPS = (TextView)findViewById(R.id.id_tienda_senal_gps);
        idTiendaCrPlazaInput = (EditText)findViewById(R.id.id_tienda_cr_plaza_input);
        idTiendaCrTiendaInput = (EditText)findViewById(R.id.id_tienda_cr_tienda_input);


        //Shared Preferences
        editor = getSharedPreferences(Constantes.MY_PREFS_NAME, MODE_PRIVATE).edit();

        // Acquire a reference to the system Location Manager
        mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        mContext = this;

        entrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(idTiendaCrPlazaInput.getText().toString().equals("") || idTiendaCrTiendaInput.getText().toString().equals("")){
                    //Show Dialog or Toast
                    Toast.makeText(IdentificadorTiendaActivity.this, "El Id no puede estar vacío", Toast.LENGTH_SHORT).show();
                }else{
                    if(mLocationManager!=null){
                        if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
//                            if((mLatitude != 0.0 && mLongitude != 0.0) && mAccuracy < Constantes.STRONG_ACCURACY){


                                Intent intentIdTienda = new Intent(IdentificadorTiendaActivity.this, EncuestasActivity.class);

                                String crPlaza = idTiendaCrPlazaInput.getText().toString();
                                String crTienda = idTiendaCrTiendaInput.getText().toString();
                                String idTienda = crPlaza+"_"+crTienda; //ID TIENDA

                                //Guardando a SharedPreferences las coordenadas de inicio de la encuesta y el id Tienda
                                editor.putString(Constantes.COORDENADAS_INICIO, ""+mLatitude+","+mLongitude);
                                Log.v("Coordenadas", ""+mLatitude+","+mLongitude);
                                editor.putString(Constantes.ID_TIENDA, idTienda);
                                editor.putString(Constantes.CR_TIENDA, crTienda);
                                editor.putString(Constantes.CR_PLAZA, crPlaza);
                                editor.commit();

                                startActivity(intentIdTienda);

//                            }else{
//                                Toast.makeText(IdentificadorTiendaActivity.this, "La señal del GPS es muy débil", Toast.LENGTH_SHORT).show();
//                            }
                        }else{
                            mAppUtils.showGPSDialog(IdentificadorTiendaActivity.this, IdentificadorTiendaActivity.class);
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onPause(){

        Log.v("onPause", "onPause");
        if(mLocationManager!=null && mLocationListener!=null){
            mLocationManager.removeUpdates(mLocationListener);
        }

        mLocationListener = null;
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.v("onResume", "onResume");
        startLocationServices();
    }

    public void startLocationServices(){

        // Define a listener that responds to location updates
        mLocationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the gps location provider.
                Log.v("Accuracy ", ""+location.getAccuracy());
                mAccuracy = location.getAccuracy();
                mLatitude = location.getLatitude();
                mLongitude = location.getLongitude();
                if(mAccuracy > Constantes.WEAK_ACCURACY){
                    idTiendaSenalGPS.setText("Baja");
                    idTiendaSenalGPS.setTextColor(Color.parseColor("#FF0000"));
                }else if(mAccuracy < Constantes.WEAK_ACCURACY && mAccuracy > Constantes.STRONG_ACCURACY){
                    idTiendaSenalGPS.setText("Debil");
                    idTiendaSenalGPS.setTextColor(Color.parseColor("#FFFF00"));
                }else if (mAccuracy < Constantes.STRONG_ACCURACY){
                    idTiendaSenalGPS.setText("Fuerte");
                    idTiendaSenalGPS.setTextColor(Color.parseColor("#40FF00"));
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {
                idTiendaSenalGPS.setText("Esperando señal del GPS.");
                idTiendaSenalGPS.setTextColor(Color.parseColor("#000000"));
            }

            @Override
            public void onProviderDisabled(String provider) {
                idTiendaSenalGPS.setText("No está el GPS encendido.");
                idTiendaSenalGPS.setTextColor(Color.parseColor("#000000"));
            }
        };

        // Register the listener with the Location Manager to receive location updates
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, mLocationListener);
    }
}
