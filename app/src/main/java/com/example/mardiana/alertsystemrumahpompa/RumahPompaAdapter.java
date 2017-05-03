package com.example.mardiana.alertsystemrumahpompa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Mardiana on 2/13/2017.
 */

public class RumahPompaAdapter extends BaseAdapter implements Filterable {
    private static ArrayList<RumahPompa> listRumahPompa;
    public ArrayList<RumahPompa> orig;

    private LayoutInflater mInflater;

    public RumahPompaAdapter(Context DataRmhpompaFragment, ArrayList<RumahPompa> results){
        listRumahPompa = results;
        mInflater = LayoutInflater.from(DataRmhpompaFragment);
    }

    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<RumahPompa> results = new ArrayList<RumahPompa>();
                if (orig == null)
                    orig = listRumahPompa;
                if (constraint != null) {
                    if (orig != null && orig.size() > 0) {
                        for (final RumahPompa g : orig) {
                            if (g.getNama().toLowerCase()
                                    .contains(constraint.toString()))
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                listRumahPompa = (ArrayList<RumahPompa>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }



    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listRumahPompa.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return listRumahPompa.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        RumahPompaAdapter.ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.item_rumahpompa, null);
            holder = new ViewHolder();
            holder.txtname = (TextView) convertView.findViewById(R.id.tv_namarumahpompa);
            holder.txtalamat = (TextView) convertView.findViewById(R.id.tv_alamatrumahpompa);

            convertView.setTag(holder);
        } else {
            holder = (RumahPompaAdapter.ViewHolder) convertView.getTag();
        }

        holder.txtname.setText(listRumahPompa.get(position).getNama());
        holder.txtalamat.setText(listRumahPompa.get(position).getAlamat());


        return convertView;
    }

    static class ViewHolder{
        TextView txtname;
        TextView txtalamat;
    }
}
