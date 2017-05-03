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

public class UserAdapter extends BaseAdapter implements Filterable {
    private static ArrayList<User> listPetugas = null;
    public ArrayList<User> orig = null;

    private LayoutInflater mInflater;

    public UserAdapter(Context DataUserFragment, ArrayList<User> results){
        listPetugas = results;
        mInflater = LayoutInflater.from(DataUserFragment);
    }

    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<User> results = new ArrayList<User>();
                if (orig == null)
                    orig = listPetugas;
                if (constraint != null) {
                    if (orig != null && orig.size() > 0) {
                        for (final User g : orig) {
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
                listPetugas = (ArrayList<User>) results.values;
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
        return listPetugas.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return listPetugas.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.item_petugas, null);
            holder = new ViewHolder();
            holder.txtusername = (TextView) convertView.findViewById(R.id.tv_usernamepetugas);
            holder.txtname = (TextView) convertView.findViewById(R.id.tv_namapetugas);
            holder.txtphone = (TextView) convertView.findViewById(R.id.tv_hppetugas);
            holder.txtrumahpompa = (TextView) convertView.findViewById(R.id.tv_rmhpompapetugas);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtname.setText(listPetugas.get(position).getNama());
        holder.txtphone.setText(listPetugas.get(position).getNohp());
        holder.txtrumahpompa.setText(listPetugas.get(position).getRumahpompa());
        holder.txtusername.setText(listPetugas.get(position).getUsername());

        return convertView;
    }

    static class ViewHolder{
        TextView txtname, txtphone, txtrumahpompa, txtusername;
    }
}
