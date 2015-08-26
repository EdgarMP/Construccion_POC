package edgarmarcopolo.com.construccion_poc;

import java.util.ArrayList;

/**
 * Created by Edgar on 6/3/2015.
 */
public class PreguntaSencillaTexto {
    private int encuestaId = 0;
    private int preguntaId = 0;
    private String preguntaTipo = "";
    private String preguntaTexto = "";
    private String respuestaTexto = "";
    private boolean conFoto = false;
    private boolean isRequerida = true;
    private String subseccion = "";

    public String getRespuestaTexto(){
        return respuestaTexto;
    }

    public void setRespuestaTexto(String respuestaTexto){
        this.respuestaTexto=respuestaTexto;
    }

    public int getEncuestaId() {
        return encuestaId;
    }

    public void setEncuestaId(int encuestaId) {
        this.encuestaId = encuestaId;
    }

    public int getPreguntaId() {
        return preguntaId;
    }

    public void setPreguntaId(int preguntaId) {
        this.preguntaId = preguntaId;
    }

    public String getPreguntaTipo() {
        return preguntaTipo;
    }

    public void setPreguntaTipo(String preguntaTipo) {
        this.preguntaTipo = preguntaTipo;
    }

    public String getPreguntaTexto() {
        return preguntaTexto;
    }

    public void setPreguntaTexto(String preguntaTexto) {
        this.preguntaTexto = preguntaTexto;
    }

    public boolean getConFoto() {
        return conFoto;
    }

    public void setConFoto(boolean conFoto) {
        this.conFoto = conFoto;
    }

    public boolean getIsRequerida() {
        return isRequerida;
    }

    public void serIsRequerida(boolean isRequerida) {
        this.isRequerida = isRequerida;
    }

    public String getSubseccion(){ return subseccion;}

    public void setSubseccion(String subseccion){ this.subseccion = subseccion;}
}
