package imastudio.rizki.com.explorekolaka.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import imastudio.rizki.com.explorekolaka.Helper.Constant;
import imastudio.rizki.com.explorekolaka.Helper.KolakaHelper;
import imastudio.rizki.com.explorekolaka.Helper.SessionManager;
import imastudio.rizki.com.explorekolaka.R;
import imastudio.rizki.com.explorekolaka.model.ItemInfoModel;

/**
 * Created by rizkisyaputra on 10/14/17.
 */

public class DetailInfo extends AppCompatActivity {

    SessionManager sesi;
    Context c = this;
    protected AQuery aQuery;

    String mIdMenu;
    TextView txtJudul, txtDeks, txtLat, txtLong, txtNamaLokasi;
    ImageView imgInfo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_info);

        sesi= new SessionManager(c);
        aQuery = new AQuery(c);
        c =this;
        Intent a = getIntent();
        mIdMenu = a.getStringExtra("id_info");


        txtJudul = (TextView)findViewById(R.id.txt_judul);
        txtDeks = (TextView)findViewById(R.id.txtDeks);
        txtLat = (TextView)findViewById(R.id.txtLat);
        txtLong = (TextView)findViewById(R.id.txtLot);
        imgInfo = (ImageView)findViewById(R.id.imgInfo);
        txtNamaLokasi = (TextView)findViewById(R.id.txt_namalokasi);



        getDetailInfo();


    }

    private void getDetailInfo(){

        //ambil data dari server
        String url = KolakaHelper.BASE_URL + "get_infoByID";
        Map<String, String> parampa = new HashMap<>();
        //parampa.put("t_device", NurHelper.getDeviceUUID(c));
        //parampa.put("t_token", sesi.getToken());
        parampa.put("id_info", mIdMenu);
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
                                            ItemInfoModel b = new ItemInfoModel();
                                            b.setJudul_info(object.getString(Constant.judul_info));
                                            b.setDesk_info(object.getString(Constant.desk_info));
                                            b.setNama_daerah(object.getString(Constant.nama_daerah));
                                            b.setLat_wisata(object.getString(Constant.lat_wisata));
                                            b.setLot_wisata(object.getString(Constant.lot_wisata));
                                            b.setNama_daerah(object.getString(Constant.nama_daerah));
                                            b.setGambar_info(object.getString(Constant.gambar_info));

                                            String nImage = b.getGambar_info();
                                            String nLokasi = b.getNama_daerah();
                                            String nLat = b.getLat_wisata();
                                            String nLong = b.getLot_wisata();

                                            txtJudul.setText(b.getJudul_info());
                                            txtDeks.setText(b.getDesk_info());
                                            Glide.with(c).load(KolakaHelper.BASE_URL_IMAGE+nImage).into(imgInfo);




                                            if (nLokasi.equalsIgnoreCase("")){
                                                txtNamaLokasi.setVisibility(View.GONE);
                                            }else{
                                                txtNamaLokasi.setText("Lokasi : " + b.getNama_daerah());
                                            }

                                            if (nLat.equalsIgnoreCase("")){
                                                txtLat.setVisibility(View.GONE);
                                            }else{
                                                txtLat.setText("Latitude : "  + b.getLat_wisata());
                                            }

                                            if (nLong.equalsIgnoreCase("")){
                                                txtLat.setVisibility(View.GONE);
                                            }else{
                                                txtLong.setText("Longitude : " +  b.getLot_wisata());
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
    public boolean onCreateOptionsMenu(Menu menu) {




            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_detail, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (mIdMenu.equalsIgnoreCase("4")){
           item.setVisible(false);
        }else
        {
            item.setVisible(true);
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
