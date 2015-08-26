package edgarmarcopolo.com.construccion_poc;


import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends ActionBarActivity {

    private EditText usuarioEditText;
    private Button entrarButton;
    private SharedPreferences.Editor editor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //UI
        entrarButton = (Button)findViewById(R.id.login_entrar_button);
        usuarioEditText = (EditText)findViewById(R.id.usuarioId);

        //Shared Preferences
        editor = getSharedPreferences(Constantes.MY_PREFS_NAME, MODE_PRIVATE).edit();

        entrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Log.v("USUARIO Lenght", ""+usuarioEditText.getText().length());
                    Log.v("USUARIO String", ""+usuarioEditText.getText().toString());

                    if(usuarioEditText.getText().length()>0){
                        //Guardando a SharedPreferences las coordenadas de inicio de la encuesta y el id Tienda
                        editor.putString(Constantes.USER_ID, usuarioEditText.getText().toString());
                        editor.commit();
                        Intent intentLogin = new Intent(LoginActivity.this, IdentificadorTiendaActivity.class);
                        startActivity(intentLogin);
                    }else{
                        Toast.makeText(LoginActivity.this, "El usuario no puede estar vacío.", Toast.LENGTH_SHORT).show();
                    }
            }
        });

    }

}
