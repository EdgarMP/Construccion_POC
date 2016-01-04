package edgarmarcopolo.com.construccion_poc;

/**
 * Created by Edgar on 8/26/2015.
 */
public class Constantes {

    public static final String MY_PREFS_NAME = "appConstruccion";
    public static final String COORDENADAS_INICIO = "coordenadasInicio";
    public static final String COORDENADAS_FINAL = "coordenadasFinal";

    public static final String EVALUACION_CALIDAD = "http://yourjson.com/yj/serv/202";
    public static final String AUDITORIA_ESTRUCTURAL = "tp://yourjson.com/yj/serv/199";
    public static final String ROJAS_AMARILLAS = "http://yourjson.com/yj/serv/198";
    public static final String CHECKLIST_SUPERVISION = "http://yourjson.com/yj/serv/197";

    public static final String CR_TIENDA = "crTienda";
    public static final String CR_PLAZA = "crPlaza";
    public static final String ID_TIENDA = "idTienda";
    public static final String ID_TIENDA_ENCUESTA = "idTiendaEncuesta";

    public static final String USER_ID = "userId";

    public static final double WEAK_ACCURACY = 1000.0;
    public static final double STRONG_ACCURACY = 500.0;


    //Servlets
    //public static final String DATA_SERVLET = "http://cupones.oxxo.com/EncuestasApp/DatosServlet"; //http://192.168.1.216:8080/AppConsutruccion/DatosServlet"
    public static final String DATA_SERVLET = "http://fcpocweb.cloudapp.net/AppConsutruccion/DatosServlet";
    //public static final String IMAGES_SERVLET = "http://192.168.43.160:8080/AppConsutruccion/GetPictureFromClient";
    //public static final String IMAGES_SERVLET = "http://cupones.oxxo.com/EncuestasApp/GetPictureFromClient";
    public static final String IMAGES_SERVLET = "http://fcpocweb.cloudapp.net/AppConsutruccion/GetPictureFromClient";
    //public static final String ENCUESTAS_SERVLET = "http://cupones.oxxo.com/EncuestasApp/EncuestasServlet";

    public static final String ENCUESTAS_SERVLET = "http://fcpocweb.cloudapp.net/AppConsutruccion/EncuestasServlet";
    //public static final String ENCUESTAS_SERVLET = "http://yourjson.com/yj/serv/203";
    //public static final String PREGUNTAS_SERVLET = "http://cupones.oxxo.com/EncuestasApp/PreguntasServlet?encid="; //http://192.168.1.166/AppConstruccion/PreguntaServlet?encid="+mEncuesta
    public static final String PREGUNTAS_SERVLET = "http://fcpocweb.cloudapp.net/AppConsutruccion/PreguntasServlet?encid=";


    //Preguntas
    public static final String PREGUNTA_RADIO_BUTTON = "RadioButton";
    public static final String PREGUNTA_CHECKBOX = "CheckBox";
    public static final String PREGUNTA_SPINNER = "Spinner";
    public static final String PREGUNTA_SENCILLA_TEXT = "SencillaTexto";


    //Volley
    public  static final int TIMEOUT_MILLISEC = 20000;

}
