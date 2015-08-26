package edgarmarcopolo.com.construccion_poc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class CamaraActivity extends Activity {
    private Camera mCamera;
    private CameraPreview mPreview;
    private int mPreguntaId = 0;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final String TAG = CamaraActivity.class.getSimpleName();
    private SharedPreferences mPrefs = null;
    private Button captureButton;
    private Encuesta mEncuesta;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camara);

        mPrefs = getSharedPreferences(Constantes.MY_PREFS_NAME, MODE_PRIVATE);
        mPreguntaId = getIntent().getIntExtra("preguntaId",0);


        Bundle b = getIntent().getExtras();
        mEncuesta = new Encuesta();
        mEncuesta = b.getParcelable("encuesta_a_mostrar");

        Log.v("Pregunta ID", ""+mPreguntaId);

        // Create an instance of Camera
        mCamera = getCameraInstance();
        if(mCamera == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("La cámara está siendo usada por otra aplicación")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> listSizes = mCamera.getParameters().getSupportedPictureSizes();

        int posicionTamano = (listSizes.size()/2)/2;
        Camera.Size size = listSizes.get(posicionTamano);
        params.setPictureSize(size.width, size.height);
        mCamera.setParameters(params);
        for(int i=0; i<listSizes.size(); i++){
            Log.v("Lista de Tamano:", ""+listSizes.get(i).height+" x "+listSizes.get(i).width);
        }
        Log.v("Tamano", ""+size.height+" X "+size.width);

        // Add a listener to the Capture button
        captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        mCamera.takePicture(null, null, mPicture);
                    }
                }
        );

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
            Log.v("CameraActivity", "Camera Open");
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            Log.v("CameraActivity", "Camera is not available");
        }
        return c; // returns null if camera is unavailable
    }

    /** Create a file Uri for saving an image or video */
    private Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        //File mediaStorageDir = new File(Environment.getExternalStorageDirectory(Environment.DIRECTORY_PICTURES), "MyCameraApp");
        String root = Environment.getExternalStorageDirectory().toString();
        File mediaStorageDir;

        mediaStorageDir = new File(root + "/saved_images");





        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        Log.v("Path", mediaStorageDir.getPath());
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){

            if(mPreguntaId==0){ //Encuesta Rojas y Amarillas, las fotos no estan ligadas a ninguna pregunta por lo tanto preguntaId=0
                Log.v("Saving photo", mediaStorageDir.getPath() + File.separator + mPrefs.getString(Constantes.ID_TIENDA_ENCUESTA, null) + "_" + "IMG_" + timeStamp + ".jpg");
                mediaFile = new File(mediaStorageDir.getPath() + File.separator + mPrefs.getString(Constantes.ID_TIENDA_ENCUESTA, null) + "_" + "IMG_" + timeStamp + ".jpg");
            }else{
                mediaFile = new File(mediaStorageDir.getPath() + File.separator + mPrefs.getString(Constantes.ID_TIENDA_ENCUESTA, null) + "_" + "IMG_" + mPreguntaId + ".jpg");
            }
        } else {
            return null;
        }

        MediaScannerConnection.scanFile(this, new String[] { mediaFile.toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });

        return mediaFile;
    }


    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {

            //Dar a elegir si quiere tomar otra foto o guardar el archivo.
            new AlertDialog.Builder(CamaraActivity.this)
                    .setTitle("Guardar Foto")
                    .setMessage("¿Deseas guardar la foto?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue
                            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                            if (pictureFile == null){
                                Log.d(TAG, "Error creating media file, check storage permissions: ");
                                return;
                            }

                            try {
                                FileOutputStream fos = new FileOutputStream(pictureFile);
                                fos.write(data);
                                fos.close();
                            } catch (FileNotFoundException e) {
                                Log.d(TAG, "File not found: " + e.getMessage());
                            } catch (IOException e) {
                                Log.d(TAG, "Error accessing file: " + e.getMessage());
                            }

                            captureButton.setEnabled(false);

                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                            //CamaraActivity.this.finish();
                            // Create our Preview view and set it as the content of our activity.
                            mPreview = null;

                            mPreview = new CameraPreview(CamaraActivity.this, mCamera);
                            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
                            preview.removeAllViews();
                            preview.addView(mPreview);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .show();


        }
    };

    @Override
    public void onStop(){
        super.onStop();

        mCamera.stopPreview();
        mPreview.getHolder().removeCallback(mPreview);
        mCamera.release();
        mCamera=null;
    }
}
