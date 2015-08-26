package edgarmarcopolo.com.construccion_poc;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Edgar on 6/4/2015.
 */
public class PreguntaSencillaTextoView extends LinearLayout{

    private int mPreguntaId = 0;
    private String mTipoPregunta = "";
    private EditText mRespuestaEditText;
    private String mPreguntaTexto = "";


    public PreguntaSencillaTextoView(Context context) {
        super(context);
    }

    public PreguntaSencillaTextoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PreguntaSencillaTextoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PreguntaSencillaTextoView(Context context, final Activity activity, final PreguntaSencillaTexto preguntaSencillaTexto, final String encuestaTiendaId){
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.pregunta_sencilla_view, this);

        TextView preguntaTituloTextView = (TextView)findViewById(R.id.pregunta_sencilla_titulo);
        LinearLayout respuestaLinearLayout = (LinearLayout)findViewById(R.id.pregunta_sencilla_respuesta);

        mRespuestaEditText = (EditText)findViewById(R.id.pregunta_sencilla_respuesta_edittext);
        mRespuestaEditText.setTextColor(Color.parseColor("#ff000000"));
        mRespuestaEditText.setHintTextColor(Color.parseColor("#ff888888"));
        this.mPreguntaId = preguntaSencillaTexto.getPreguntaId();
        this.mTipoPregunta = preguntaSencillaTexto.getPreguntaTipo();
        this.mPreguntaTexto = preguntaSencillaTexto.getPreguntaTexto();

        preguntaTituloTextView.setText(mPreguntaTexto);

        mRespuestaEditText.setTag(preguntaSencillaTexto);
    }

    public String getEditTextString(){
        return mRespuestaEditText.getText().toString();
    }

    public EditText getEditText(){
        return this.mRespuestaEditText;
    }

}
