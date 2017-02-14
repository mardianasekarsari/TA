package com.example.mardiana.alertsystemrumahpompa;

import android.widget.BaseAdapter;

/**
 * Created by Mardiana on 2/13/2017.
 */

public class RumahPompa{
    String nama, telepon, alamat;

    public RumahPompa() {
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
}
