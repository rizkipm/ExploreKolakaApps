package imastudio.rizki.com.explorekolaka;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.bumptech.glide.Glide;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import imastudio.rizki.com.explorekolaka.Activity.MainMenu;
import imastudio.rizki.com.explorekolaka.Activity.MenuUtama;
import imastudio.rizki.com.explorekolaka.Helper.Constant;
import imastudio.rizki.com.explorekolaka.Helper.KolakaHelper;
import imastudio.rizki.com.explorekolaka.Helper.No_Internet;
import imastudio.rizki.com.explorekolaka.Helper.SessionManager;
import imastudio.rizki.com.explorekolaka.adapter.MenuMainAdapter;
import imastudio.rizki.com.explorekolaka.model.ItemInfo;
import imastudio.rizki.com.explorekolaka.model.MenuItem;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 100;

    private MenuMainAdapter mGridAdapter;
    protected Context c ;
    protected AQuery aQuery;
    public static final AlphaAnimation btnAnimasi = new AlphaAnimation(1F, 0.5F);
    protected SessionManager sesi;
    private ArrayList<MenuItem> mGridData;

    private GridView mGridView;
    private ProgressBar mProgressBar;
    public ViewFlipper viewFlipper;
    private String imgMenu, idSlider,nmMenu, desMenu, hargaMenu, Nmresto, idMenu, statusOps;
    AQuery aq;
    private boolean mIsAppFirstLaunched = true;

    private ArrayList<ItemInfo> data;


    ImageView btnBuah, btnSayur;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_activity);

        aQuery = new AQuery(c);
        c =this;
        sesi= new SessionManager(c);

        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        mGridView = (GridView)findViewById(R.id.gridView);
        viewFlipper = (ViewFlipper) findViewById(R.id.flipper);
//        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_left_in));
//        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_left_out));
//        viewFlipper.setFlipInterval(2000);
//        viewFlipper.startFlipping();

//        mGridData = new ArrayList<>();
//        if(!KolakaHelper.isOnline(getApplicationContext())){
//            startActivity(new Intent(getApplicationContext(), No_Internet.class));
//            finish();
//        }else{
//            getDataMenu();
//            new AsyncHttpTask().execute(KolakaHelper.BASE_URL + "get_menu");
//        }
//
//
//        data = new ArrayList<>();
//        mGridAdapter = new MenuMainAdapter(this,R.layout.home_item, mGridData);
//        mGridView.setAdapter(mGridAdapter);


//        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                ItemInfo item = (ItemInfo) parent.getItemAtPosition(position);
//
//                Bundle args = new Bundle();
//                args.putString(Constant.ID_PRODUK, mGridData.get(position).getId());
//
//                Intent intent = new Intent(MainActivityNew.this, DetailInfo.class);
//
//                ImageView imageView = (ImageView) v.findViewById(R.id.grid_item_image);
////
//
//                int[] screenLocation = new int[2];
//                imageView.getLocationOnScreen(screenLocation);
////
////                //Pass the image title and url to DetailsActivity
//                intent.putExtra("left", screenLocation[0]).
//                        putExtra("top", screenLocation[0]).
//                        putExtra("width", imageView.getWidth()).
//                        putExtra("height", imageView.getHeight()).
//                        putExtra("id_produk", item.getId()).
//                        putExtra("nama_produk", item.getTitle()).
//                        putExtra("harga", item.getHarga()).
//                        putExtra("harga_promo", item.getHarga_promo()).
//                        putExtra("desk_produk", item.getDesProduk()).
//                        putExtra("id_petani", item.getId_toko()).
//                        putExtra("id_kategori", item.getId_kategori()).
//                        putExtra("stok_produk", item.getStok()).
//
//                        putExtra("gbr_produk", item.getNamaGambar());
//
//
////                //Start details activity
//                startActivity(intent);
//            }
//        });


        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

              if (sesi.isLogin()){
                  startActivity(new Intent(c, MainMenu.class));
                  finish();

                }else {
                    startActivity(new Intent(c, LoginActivity.class));
                    finish();
                }
            }
        }, 2000L);
    }


    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            try {
                // Create Apache HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet(params[0]));
                int statusCode = httpResponse.getStatusLine().getStatusCode();

                // 200 represents HTTP OK
                if (statusCode == 200) {
                    String response = streamToString(httpResponse.getEntity().getContent());
                    parseResult(response);
                    result = 1; // Successful
                } else {
                    result = 0; //"Failed
                }
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }

            return result;
        }

        String streamToString(InputStream stream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
            String line;
            String result = "";
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }

            // Close stream
            if (null != stream) {
                stream.close();
            }
            return result;
        }

        private void parseResult(String result) {
            try {
                JSONObject response = new JSONObject(result);
                JSONArray posts = response.optJSONArray("data");
                MenuItem item;
                for (int i = 0; i < posts.length(); i++) {
                    JSONObject post = posts.optJSONObject(i);
                    String id = post.optString("id_menu");
                    String title = post.optString("nama_menu");


                    String gambar = post.optString("icon_menu");


                    item = new MenuItem();
                    item.setId_menu(id);
                    item.setNama_menu(title);


                    item.setIcon_menu(KolakaHelper.BASE_URL_IMAGE + gambar);

                    mGridData.add(item);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            // Download complete. Lets update UI
            if (result == 1) {
                mGridAdapter.setGridData(mGridData);
            } else {
                Toast.makeText(MainActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
            //Hide progressbar
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void getDataMenu(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }else {

            // clear data sebelumnya
            mGridData.clear();
            //ambil data dari server
            String url = KolakaHelper.BASE_URL + "get_Slider";
            Map<String, String> parampa = new HashMap<>();
            try {
                //mencari url dan parameter yang dikirimkan
                KolakaHelper.pre("Url : " + url + ", params " + parampa.toString());
                //koneksi ke server meggunakan aquery
                aq.ajax(url, parampa, String.class,
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
                                        String pesan = json.getString("msg");
                                        // NurHelper.pesan(getActivity(), pesan);
                                        if (result.equalsIgnoreCase("true")) {
                                            JSONArray jsonArray = json.getJSONArray("data");
                                            for (int a = 0; a < jsonArray.length(); a++) {
                                                HashMap<String, String> dataMap = new HashMap<>();
                                                JSONObject object = jsonArray.getJSONObject(a);
                                                idMenu = object.getString("id_info");
                                                nmMenu = object.getString("judul_info");
//                                            hargaMenu = object.getString("harga_menu");
//                                                desMenu = object.getString("desc_slider");
                                                imgMenu = object.getString("gambar_info");
//                                            Nmresto = object.getString("resto_nama");
//                                                Toast.makeText(getApplicationContext(), "ID : " + idMenu +"\nMenu : " +nmMenu + "\nGambar : "+imgMenu, Toast.LENGTH_SHORT).show();


                                                if ((a < Constant.VALUE_SLIDESHOW) && mIsAppFirstLaunched) {
                                                    createSlideshow(idMenu, imgMenu, nmMenu);
                                                }
                                            }
                                        } else {
                                            KolakaHelper.pesan(getApplicationContext(), pesan);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        KolakaHelper.pesan(getApplicationContext(), "Error parsing data");
                                    }
                                }
                            }
                        });

            } catch (Exception e) {
                KolakaHelper.pesan(getApplicationContext(), "Error get data");
                e.printStackTrace();
            }
        }
    }
    private void createSlideshow(String idMenu, String imgMenu,String nmMenu){
        View view = getLayoutInflater().inflate(R.layout.layout_slideshow, null);

        ImageView menuImg = (ImageView) view.findViewById(R.id.imgMenu);
        TextView namaMenu = (TextView) view.findViewById(R.id.tvNamaMenu);
        TextView judulResto = (TextView) view.findViewById(R.id.tvNamaResto);
        TextView deskripsiMenu = (TextView) view.findViewById(R.id.tvDesMenu);

        // menuImg.setId(Integer.parseInt(idMenu));
//        menuImg.setOnClickListener(this);

        // namaMenu.setText(NdotHelper.BASE_URL_IMAGE+imgMenu);

        // imageLoader.DisplayImage(NdotHelper.BASE_URL_IMAGE+ imgMenu, menuImg);
        Glide.with(c).load(KolakaHelper.BASE_URL_IMAGE+imgMenu).into(menuImg);


        // Add layout to flipper
        viewFlipper.addView(view);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSION_WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                getDataMenu();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the images", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
