package com.example.mardiana.alertsystemrumahpompa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Mardiana on 2/13/2017.
 */

public class RumahPompaAdapter extends BaseAdapter {
    private static ArrayList<RumahPompa> listRumahPompa;

    private LayoutInflater mInflater;

    public RumahPompaAdapter(Context DataRmhpompaFragment, ArrayList<RumahPompa> results){
        listRumahPompa = results;
        mInflater = LayoutInflater.from(DataRmhpompaFragment);
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
            holder.txtphone = (TextView) convertView.findViewById(R.id.tv_teleponrumahpompa);


            convertView.setTag(holder);
        } else {
            holder = (RumahPompaAdapter.ViewHolder) convertView.getTag();
        }

        holder.txtname.setText(listRumahPompa.get(position).getNama());
        holder.txtphone.setText(listRumahPompa.get(position).getTelepon());


        return convertView;
    }

    static class ViewHolder{
        TextView txtname, txtphone, txtrumahpompa;
    }
}
