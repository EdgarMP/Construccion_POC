package edgarmarcopolo.com.construccion_poc;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Edgar on 4/10/2015.
 */
public class Encuesta implements Parcelable {

    public int encuestaId = 0;
    public String encuestaNombre = null;
    public String encuestaTiendaId = null;

    public Encuesta(){}

    public Encuesta(Parcel in){
        setEncuestaId(in.readInt());
        setEncuestaNombre(in.readString());
        setEncuestaTiendaId(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getEncuestaId());
        dest.writeString(getEncuestaNombre());
        dest.writeString(getEncuestaTiendaId());
    }

    public int getEncuestaId() {
        return encuestaId;
    }

    public void setEncuestaId(int encuestaId) {
        this.encuestaId = encuestaId;
    }

    public String getEncuestaNombre() {
        return encuestaNombre;
    }

    public void setEncuestaNombre(String encuestaNombre) {
        this.encuestaNombre = encuestaNombre;
    }

    public String getEncuestaTiendaId() {
        return encuestaTiendaId;
    }

    public void setEncuestaTiendaId(String encuestaTiendaId) {
        this.encuestaTiendaId = encuestaTiendaId;
    }

    //Object implementing the Parcelable.Creator interface.
    public static final Parcelable.Creator<Encuesta> CREATOR = new Parcelable.Creator<Encuesta>() {
        public Encuesta createFromParcel(Parcel in) {
            return new Encuesta(in);
        }
        public Encuesta[] newArray(int size) {
            return new Encuesta[size];
        }
    };
}
