package com.example.mardiana.alertsystemrumahpompa;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by user on 16/05/2017.
 */

public class LogAdapter extends BaseAdapter {

    private static ArrayList<Log> listLog;

    private LayoutInflater mInflater;

    public LogAdapter(Activity activity, ArrayList<Log> results) {
        super();
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listLog = results;
    }

    @Override
    public int getCount() {
        return listLog.size();
    }

    @Override
    public Object getItem(int position) {
        return listLog.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.item_log, null);
            holder = new ViewHolder();
            holder.txtnama = (TextView) convertView.findViewById(R.id.tv_logusername);
            holder.txtwaktu = (TextView) convertView.findViewById(R.id.tv_logwaktu);
            holder.txtlog = (TextView) convertView.findViewById(R.id.tv_log);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtnama.setText(listLog.get(position).getNama());
        holder.txtwaktu.setText(listLog.get(position).getWaktu());
        holder.txtlog.setText(listLog.get(position).getLog());

        return convertView;
    }

    static class ViewHolder{
        TextView txtnama, txtwaktu, txtlog;
    }
}
