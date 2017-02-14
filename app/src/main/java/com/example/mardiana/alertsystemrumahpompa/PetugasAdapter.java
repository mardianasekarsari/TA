package com.example.mardiana.alertsystemrumahpompa;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Mardiana on 2/13/2017.
 */

public class PetugasAdapter extends BaseAdapter {
    private static ArrayList<Petugas> listPetugas;

    private LayoutInflater mInflater;

    public PetugasAdapter(Context DataPetugasFragment, ArrayList<Petugas> results){
        listPetugas = results;
        mInflater = LayoutInflater.from(DataPetugasFragment);
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

        return convertView;
    }

    static class ViewHolder{
        TextView txtname, txtphone, txtrumahpompa;
    }
}
