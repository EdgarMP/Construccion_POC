package edgarmarcopolo.com.construccion_poc;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Edgar on 4/10/2015.
 */
public class EncuestaAdapter extends BaseAdapter{

    private     static LayoutInflater inflater=null;
    private Activity mActivity = null;
    private ArrayList<Encuesta> mListaEncuesta = null;
    private Encuesta mEncuesta = null;

    public EncuestaAdapter(Activity activity, ArrayList<Encuesta> listaEncuesta){
        this.mActivity = activity;
        this.mListaEncuesta = listaEncuesta;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mListaEncuesta.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){
            vi = inflater.inflate(R.layout.encuesta_item, null);

            holder = new ViewHolder();
            holder.nombreEncuesta = (TextView) vi.findViewById(R.id.item_nombre_encuesta);

            vi.setTag( holder );
        }else{
            holder=(ViewHolder)vi.getTag();
        }

        if(mListaEncuesta!=null && mListaEncuesta.size()<=0){
            //No hay datos.
        }else{
            //Si hay datos.
            mEncuesta = mListaEncuesta.get(position);
            holder.nombreEncuesta.setText(mEncuesta.getEncuestaNombre());
        }
        vi.setOnClickListener(new OnItemClickListener(position));
        return vi;
    }

    static class ViewHolder{
        public TextView nombreEncuesta;
    }

    private class OnItemClickListener implements View.OnClickListener{

        private int mPosition;
        OnItemClickListener(int position){
            mPosition=position;
        }

        @Override
        public void onClick(View v) {
            EncuestasActivity encuestasActivity = (EncuestasActivity)mActivity;
            encuestasActivity.onTituloClick(mPosition);
        }
    }
}
