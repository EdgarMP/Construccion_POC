package edgarmarcopolo.com.construccion_poc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.toolbox.ImageRequest;

import java.util.ArrayList;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Created by Edgar on 4/13/2015.
 */


public class PreguntaMultipleView extends LinearLayout {

    private RadioGroup mRadioGroup;
    private int mPreguntaId = 0;
    private String mTipoPregunta = null;
    private Encuesta mEncuesta = null;

    public PreguntaMultipleView(Context context) {
        super(context);
    }

    public PreguntaMultipleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PreguntaMultipleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PreguntaMultipleView(Context context, final Activity activity, final PreguntaMultiple preguntaMultiple, final Encuesta encuesta){
        super(context);
        this.mEncuesta = encuesta;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.pregunta_multiple_view, this);

        TextView preguntaTituloTextView = (TextView)findViewById(R.id.pregunta_multiple_titulo);
        TextView preguntTituloSubseccion = (TextView)findViewById(R.id.pregunta_multiple_subseccion);
        ImageButton preguntaBotonFoto = (ImageButton)findViewById(R.id.pregunta_multiple_foto);
        LinearLayout respuestasLinearLayout = (LinearLayout)findViewById(R.id.pregunta_multiple_respuestas);

        preguntaTituloTextView.setText(preguntaMultiple.getPreguntaTexto());
        preguntTituloSubseccion.setText(preguntaMultiple.getSubseccion());
        this.mPreguntaId = preguntaMultiple.getPreguntaId();
        this.mTipoPregunta = preguntaMultiple.getPreguntaTipo();

        //Log.v("PreguntaMultipleView", "Pregunta Id: "+this.mPreguntaId);

        if(preguntaMultiple.getPreguntaTipo().equals(Constantes.PREGUNTA_RADIO_BUTTON)){
            mRadioGroup = new RadioGroup(context); //create the RadioGroup
            mRadioGroup.setId(preguntaMultiple.getPreguntaId());
            mRadioGroup.setOrientation(RadioGroup.VERTICAL);//or RadioGroup.VERTICAL
            mRadioGroup.setTag(preguntaMultiple);
            //Creando las respuestas multiples
            for(int i=0; i<preguntaMultiple.getPreguntaRespuesas().size(); i++){
                RadioButton rdbtn = new RadioButton(context);

                rdbtn.setId(preguntaMultiple.getPreguntaId()+preguntaMultiple.getPreguntaRespuesas().get(i).getRespuestaId());
                rdbtn.setText(preguntaMultiple.getPreguntaRespuesas().get(i).getRespuestaTexto());
                rdbtn.setTextColor(Color.BLACK);
                mRadioGroup.addView(rdbtn);
            }



            preguntaBotonFoto.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    Intent camaraActivityIntent = new Intent(activity, CamaraActivity.class);
                    camaraActivityIntent.putExtra("preguntaId", preguntaMultiple.getPreguntaId());
                    camaraActivityIntent.putExtra("encuesta_a_mostrar", encuesta);
                    activity.startActivity(camaraActivityIntent);

                }

            });

            respuestasLinearLayout.addView(mRadioGroup);
        }else if(preguntaMultiple.getPreguntaTipo().equals(Constantes.PREGUNTA_CHECKBOX)){
            for(int i=0; i<preguntaMultiple.getPreguntaRespuesas().size(); i++){
                CheckBox chkbox = new CheckBox(context);

                chkbox.setTag(preguntaMultiple);
                //chkbox.setId(preguntaMultiple.getPreguntaId()+preguntaMultiple.getPreguntaRespuesas().get(i).getRespuestaId());
                chkbox.setText(preguntaMultiple.getPreguntaRespuesas().get(i).getRespuestaTexto());
                chkbox.setTextColor(Color.BLACK);

                respuestasLinearLayout.addView(chkbox);
            }
        }else if(preguntaMultiple.getPreguntaTipo().equals(Constantes.PREGUNTA_SPINNER)){

            ArrayList<String> spinnerArray = new ArrayList<String>();
            for(int i=0; i<preguntaMultiple.getPreguntaRespuesas().size(); i++){
                spinnerArray.add(preguntaMultiple.getPreguntaRespuesas().get(i).getRespuestaTexto());
            }

            Spinner spinner = new Spinner(context);
            spinner.setTag(preguntaMultiple);
            //spinner.setId(preguntaMultiple.getPreguntaId()+preguntaMultiple.getPreguntaRespuesas().get(preguntaMultiple.getPreguntaId()).getRespuestaId());
            //ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, spinnerArray);
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, R.layout.spinner_text_view, R.id.some_text_view, spinnerArray);
            spinner.setAdapter(spinnerArrayAdapter);
            respuestasLinearLayout.addView(spinner);
        }

    }

    public RadioGroup getRadioGroup(){
        if(mRadioGroup!=null){
            return mRadioGroup;
        }else{
            return null;
        }
    }

    public String getTipoPregunta(){
        if(mTipoPregunta!=null){
            return mTipoPregunta;
        }else{
            return null;
        }
    }

    public int getPreguntaId(){
        if(mPreguntaId != 0){
            return mPreguntaId;
        }else{
            return 0;
        }
    }
}
