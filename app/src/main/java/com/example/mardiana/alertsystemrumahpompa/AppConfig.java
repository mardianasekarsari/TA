package com.example.mardiana.alertsystemrumahpompa;

/**
 * Created by Mardiana on 2/16/2017.
 */

public class AppConfig {
    public static String PETUGAS = "PETUGAS";
    public static String ADMIN = "ADMIN";
    public static String PENGAWAS = "PENGAWAS";
    // This class contains all the constant values that we use across the app.

    // Web service url
    private static String url = "http://192.168.1.101/rumahpompa-server/";
    public static String URL_USER = url + "user/";
    public static String URL_RUMAHPOMPA = url + "rumah-pompa/";
    public static String URL_ROLE = url + "role/";
    public static String URL_DATA = url + "data/";
    public static String URL_LOGIN = url + "login";
    public static String URL_APIKEY = url + "apikey/";


    //public static String URL_LOGIN = url + "user/login";
    /*public static String URL_REGISTER = url + "user/store";
    public static String URL_EDITTOKEN = url + "user/editToken";
    public static String URL_DELETETOKEN = url + "user/deleteToken";
    public static String URL_EDITPROFIL = url + "user/editProfil";
    public static String URL_EDITUSER = url + "user/edit";
    public static String URL_GETUSER = url + "user/user";
    public static String URL_GETUSERBYUSERNAME = url + "user/getbyUsername";
    public static String URL_DELETEUSER = url + "user/delete";
    public static String URL_CHANGEPASSWORD = url + "user/changePassword";*/

   /* public static String URL_GETRUMAHPOMPA = url + "rumah_pompa/rumahpompa";
    public static String URL_STORERUMAHPOMPA = url + "rumah_pompa/store";
    public static String URL_EDITRUMAHPOMPA = url + "rumah_pompa/edit";
    public static String URL_DELETERUMAHPOMPA = url + "rumah_pompa/delete";
    public static String URL_GETRUMAHPOMPABYID = url + "rumah_pompa/getrumahpompabyId";*/

    public static String URL_GETRUMAHPOMPABYNAME = url + "rumah_pompa/getrumahpompabyName";
    public static String URL_GETRUMAHPOMPABYSTATUS = url + "rumah_pompa/getrumahpompabyStatus";

    public static String URL_GETUSERRUMAHPOMPA = url + "rumah_pompa/getUserRumahpompa";

    /*public static String URL_GETROLE = url + "role/role";
    public static String URL_GETROLEBYUSERNAME = url + "role/getByUsername";

    public static String URL_GETDATA = url + "data/getbyId";
    public static String URL_GETLASTDATA = url + "data/getalllastdata";*/

    public static String URL_ALERT = url + "data/alert";

    // Shared Pref
    public static final String PREF_FIREBASE = "firebase_regid";
    public static final String PREF_APIKEY = "pref_apikey";


    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;
    public static final String PUSH_NOTIFICATION = "pushNotification";

    //Notification Form
    public static String EDIT_SUCCESS = "Data Berhasil Diubah";
    public static String EDIT_FAILED = "Data Gagal Diubah";
    public static String STORE_SUCCESS = "Data Berhasil Ditambah";
    public static String STORE_FAILED = "Data Gagal Ditambah";
    public static String DELETE_SUCCESS = "Data Berhasil Dihapus";
    public static String DELETE_FAILED = "Data Gagal Dihapus";
    public static String NODATA = "Data Tidak Tersedia";
}
