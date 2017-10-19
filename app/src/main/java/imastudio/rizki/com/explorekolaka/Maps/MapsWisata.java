package imastudio.rizki.com.explorekolaka.Maps;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import imastudio.rizki.com.explorekolaka.Helper.Constant;
import imastudio.rizki.com.explorekolaka.Helper.KolakaHelper;
import imastudio.rizki.com.explorekolaka.Helper.SessionManager;
import imastudio.rizki.com.explorekolaka.LibsJSON.JSONParser;
import imastudio.rizki.com.explorekolaka.R;
import imastudio.rizki.com.explorekolaka.model.ItemInfoModel;
import imastudio.rizki.com.explorekolaka.model.ModelMapsWisata;

public class MapsWisata extends AppCompatActivity implements LocationListener {

    private GoogleMap googleMap;

    SessionManager sesi;
    Context c = this;
    protected AQuery aQuery;
    JSONArray string_json = null;

    double latitude;
    double longitude;
    String dataLat, dataLot;
    String mIdMenu;
    JSONParser jsonParser = new JSONParser();

    private ArrayList<ModelMapsWisata> data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_wisata);

        sesi = new SessionManager(c);
        aQuery = new AQuery(c);


        Intent a = getIntent();
        mIdMenu = a.getStringExtra("id_menu");

        data = new ArrayList<>();
        getDetailInfo();


        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        if (status != ConnectionResult.SUCCESS) {
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        } else {
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            googleMap = fm.getMap();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);

            googleMap.getUiSettings().setRotateGesturesEnabled(false);
            googleMap.getUiSettings().setScrollGesturesEnabled(false);
            googleMap.getUiSettings().setTiltGesturesEnabled(false);
            googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                onLocationChanged(location);
            }
//            locationManager.requestLocationUpdates(provider, 500000, 0, this);

//            new AmbilMaps().execute();

        }


    }

    private void getDetailInfo(){

        //ambil data dari server
        String url = KolakaHelper.BASE_URL + "get_allMenu";
        Map<String, String> parampa = new HashMap<>();
        //parampa.put("t_device", NurHelper.getDeviceUUID(c));
        //parampa.put("t_token", sesi.getToken());
//        parampa.put("id_menu", mIdMenu);
        //menambahkan progres dialog loading
        ProgressDialog progressDialog = new ProgressDialog(c);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setInverseBackgroundForced(false);
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.setMessage("Loading..");
        try {
            //mencari url dan parameter yang dikirimkan
            KolakaHelper.pre("Url : " + url + ", params " + parampa.toString());
            //koneksi ke server meggunakan aquery
            aQuery.progress(progressDialog).ajax(url, parampa, String.class,
                    new AjaxCallback<String>() {
                        @Override
                        public void callback(String url, String hasil, AjaxStatus status) {
                            //cek apakah hasilnya null atau tidak
                            if (hasil != null) {
                                KolakaHelper.pre("Respon : " + hasil);
                                //merubah string menjadi json
                                try {
                                    JSONObject json = new JSONObject(hasil);
                                    String result = json.getString("result");
//                                    String pesan = json.getString("msg");
                                    // NurHelper.pesan(c, pesan);
                                    if (result.equalsIgnoreCase("true")) {
                                        JSONArray jsonArray = json.getJSONArray("data");
                                        for (int a = 0; a < jsonArray.length(); a++) {
                                            JSONObject object = jsonArray.getJSONObject(a);
                                            //ambil data perbooking dan masukkan ke kelas object model
                                            ModelMapsWisata b = new ModelMapsWisata();
                                            b.setNamaWisata(object.getString(Constant.judul_info));
                                            b.setDeskWisata(object.getString(Constant.desk_info));
                                            b.setLutitude(object.getString(Constant.lat_wisata));
                                            b.setLogitude(object.getString(Constant.lot_wisata));






//
//                                            Double lat1 = Double.parseDouble(object.getDouble(Constant.lat_wisata));
//                                            Double lat2 = object.getDouble(Constant.lot_wisata);

//                                           b.setLogitude(object.getString(Double.parseDouble(Constant.lot_wisata)));
//                                           b.setLutitude(object.getDouble(Constant.lat_wisata));


//                                           String nLat1 = Double.toString(b.getLutitude());
//                                           String nLong1 = Double.toString(b.getLogitude());
//                                           Double lat1 = Double.parseDouble(nLat1);
//                                           Double lat2 = Double.parseDouble(nLong1);

                                            double a1 = Double.parseDouble(b.getLogitude());
                                            double a2 = Double.parseDouble(b.getLutitude());

                                            Toast.makeText(getApplicationContext(), "nilai : " + a1 + a2, Toast.LENGTH_LONG).show();



//                                            b.setLutitude(b.getLutitude());
//                                            b.setLogitude(b.getLogitude());
                                            b.setNamaWisata(b.getNamaWisata());
                                            b.setDeskWisata((String) b.getDeskWisata());
                                            //   s.setKota((String) c.get(TAG_kabupaten_kota));
//                    s.setProvinsi((String)c.get(TAG_Provinsi));
                                            data.add(b);



                                            dataLat = Double.toString(latitude);
                                            dataLot = Double.toString(longitude);


                                            List<Marker> markers = new ArrayList<Marker>();

                                            for (int i = 0; i < data.size(); i++) {
                                                ModelMapsWisata x = data.get(i);
                                                double a12 = Double.parseDouble(x.getLogitude());
                                                double a22 = Double.parseDouble(x.getLutitude());
//                                                Double a12 = Double.parseDouble(String.valueOf(x.getLogitude()));
//                                                Double a13 = Double.parseDouble(String.valueOf(x.getLutitude()));
                                                LatLng nLat = new LatLng(a22, a12);
                                                Marker marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(a22, a12))
                                                        .title("Nama Wisata : "  + x.getNamaWisata())
                                                        .snippet(x.getDeskWisata() + "," ).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_wisata))

                                                );
                                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nLat, 14));
//                googleMap.animateCamera(CameraUpdateFactory.zoomTo(14), 500000, null);
//                ); //...
                                                markers.add(marker);

                                                markers.size();
                                            }







//                                            Toast.makeText(getApplicationContext(), "msg = "+  ProdukMerk, Toast.LENGTH_LONG).show();
                                        }
                                    } else {
//
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        dataLat = Double.toString(latitude);
        dataLot = Double.toString(longitude);

        LatLng latLng = new LatLng(latitude, longitude);

        googleMap.addMarker(new MarkerOptions().position(latLng).title("Posisi Sekarang"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }





//    class AmbilMaps extends AsyncTask<String, String, String> {
//        private ArrayList<ModelMapsWisata> dataSekolah = new ArrayList<>();
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            List<Marker> markers = new ArrayList<Marker>();
//
//            for (int i = 0; i < dataSekolah.size(); i++) {
//                ModelMapsWisata x = dataSekolah.get(i);
//                Double a12 = x.getLogitude();
//                Double a13 = x.getLutitude();
//                LatLng nLat = new LatLng(a12, a13);
//                Marker marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(x.getLogitude(), x.getLutitude()))
//                        .title("Nama Pasar : " + "-" + x.getNamaWisata())
//                        .snippet(x.getDeskWisata() + "," ).icon(BitmapDescriptorFactory.fromResource(R.drawable.lokas))
//
//                );
//                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nLat, 14));
////                googleMap.animateCamera(CameraUpdateFactory.zoomTo(14), 500000, null);
////                ); //...
//                markers.add(marker);
//
//                markers.size();
//            }
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            dataLat = Double.toString(latitude);
//            dataLot = Double.toString(longitude);
//
////            LatLng latLng = new LatLng(latitude, longitude);
////            Toast.makeText(getApplicationContext(),"Latitude = "+ latitude + "Longitude = " +longitude,
////                    Toast.LENGTH_LONG).show();
//
//
//            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
////            params1.add(new BasicNameValuePair(TAG_LINTANG, dataLat));
////            params1.add(new BasicNameValuePair(TAG_BUJUR, dataLot));
//
//            JSONObject json = jsonParser.makeHttpRequest(
//                    KolakaHelper.BASE_URL + "get_allMenu", "GET", params1);
//            Log.i("Ini nilai json ", ">" + json);
//
//            try {
//                string_json = json.getJSONArray("data");
//
//                for (int i = 0; i < string_json.length(); i++) {
//
//                    ModelMapsWisata s = new ModelMapsWisata();
//
//                    JSONObject c = string_json.getJSONObject(i);
//                    Double lat1 = c.getDouble(Constant.lat_wisata);
//                    Double lat2 = c.getDouble(Constant.lot_wisata);
//
//                    s.setLutitude(lat1);
//                    s.setLogitude(lat2);
//                    s.setNamaWisata(c.getString(Constant.judul_info));
//                    s.setDeskWisata((String) c.get(Constant.desk_info));
//                    //   s.setKota((String) c.get(TAG_kabupaten_kota));
////                    s.setProvinsi((String)c.get(TAG_Provinsi));
//
//
//                    dataSekolah.add(s);
//
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//
//            return null;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//    }
}
