package edgarmarcopolo.com.construccion_poc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;

/**
 * Created by Edgar on 4/14/2015.
 */
public class AppUtils {

    public void showGPSDialog(final Activity activity, final Class<? extends Activity> toClass){
        //LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        //if (!locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER)) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        String gpsMessage = "El GPS tiene que estar encendido. ¿Desea ir a la configuración?";
        String yes = "Si";
        String no = "No";
        builder.setMessage(gpsMessage)
                .setCancelable(false)
                .setPositiveButton(yes, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        activity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(no, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        Toast.makeText(activity, "El GPS tiene que estar encendido", Toast.LENGTH_LONG).show();

                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
        // }

    }
}
