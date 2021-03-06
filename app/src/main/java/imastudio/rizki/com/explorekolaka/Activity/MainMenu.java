package imastudio.rizki.com.explorekolaka.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

import imastudio.rizki.com.explorekolaka.Helper.Constant;
import imastudio.rizki.com.explorekolaka.Helper.KolakaHelper;
import imastudio.rizki.com.explorekolaka.Helper.No_Internet;
import imastudio.rizki.com.explorekolaka.Helper.SessionManager;
import imastudio.rizki.com.explorekolaka.LoginActivity;
import imastudio.rizki.com.explorekolaka.Maps.MapsWisata;
import imastudio.rizki.com.explorekolaka.SplashActivity;
import imastudio.rizki.com.explorekolaka.R;
import imastudio.rizki.com.explorekolaka.adapter.MenuMainAdapter;
import imastudio.rizki.com.explorekolaka.fragment.FragListInfo;
import imastudio.rizki.com.explorekolaka.model.ItemInfoModel;
import imastudio.rizki.com.explorekolaka.model.MenuItemModel;

public class MainMenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {



    private static final String TAG = SplashActivity.class.getSimpleName();
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 100;

    private MenuMainAdapter mGridAdapter;
    protected Context c ;
    protected AQuery aQuery;
    public static final AlphaAnimation btnAnimasi = new AlphaAnimation(1F, 0.5F);
    protected SessionManager sesi;
    private ArrayList<MenuItemModel> mGridData;

    private GridView mGridView;
    private ProgressBar mProgressBar;
    public ViewFlipper viewFlipper;
    private String imgMenu, idSlider,nmMenu, desMenu, hargaMenu, Nmresto, idMenu, statusOps;
    AQuery aq;
    private boolean mIsAppFirstLaunched = true;

    private ArrayList<ItemInfoModel> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        c =this;
        aq = new AQuery(c);

        sesi= new SessionManager(c);

        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        mGridView = (GridView)findViewById(R.id.gridView);
        viewFlipper = (ViewFlipper) findViewById(R.id.flipper);
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_left_in));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_left_out));
        viewFlipper.setFlipInterval(2000);
        viewFlipper.startFlipping();


        if(!KolakaHelper.isOnline(getApplicationContext())){
            startActivity(new Intent(getApplicationContext(), No_Internet.class));
            finish();
        }else{
            getDataMenu();
            new AsyncHttpTask().execute(KolakaHelper.BASE_URL + "get_menu");
        }

        mGridData = new ArrayList<>();
        mGridAdapter = new MenuMainAdapter(this,R.layout.home_item, mGridData);
        mGridView.setAdapter(mGridAdapter);
        data = new ArrayList<>();




        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                MenuItemModel item = (MenuItemModel) parent.getItemAtPosition(position);

                String nMenu = mGridData.get(position).getId_menu();

                if (nMenu.equalsIgnoreCase("8")){
                    Intent intent = new Intent(MainMenu.this, MapsWisata.class);
//
                intent.putExtra(

                        "id_menu", mGridData.get(position).getId_menu());


//                //Start details activity
                startActivity(intent);

                }else{

                    Bundle args = new Bundle();
                    args.putString(Constant.id_menu, mGridData.get(position).getId_menu());
                    FragListInfo restoranFragment = new FragListInfo();
                    restoranFragment.setArguments(args);
                    getFragmentManager().beginTransaction().replace(R.id.frameDisplay, restoranFragment).commit();
                }


//                GlobalHelper.id_menu = data.get(position).getId_menu();

////                Bundle args = new Bundle();
//                args.putString(Constant.ID_MENU, mGridData.get(position).getId_menu());

//                Intent intent = new Intent(MainMenu.this, DetailInfo.class);
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
//                        putExtra("id_menu", mGridData.get(position).getId_menu());
//
//
////                //Start details activity
//                startActivity(intent);
            }
        });

        mProgressBar.setVisibility(View.VISIBLE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
                MenuItemModel item;
                for (int i = 0; i < posts.length(); i++) {
                    JSONObject post = posts.optJSONObject(i);
                    String id = post.optString("id_menu");
                    String title = post.optString("nama_menu");


                    String gambar = post.optString("icon_menu");


                    item = new MenuItemModel();
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
                Toast.makeText(MainMenu.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
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
//                                            KolakaHelper.pesan(getApplicationContext(), pesan);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
//                                        KolakaHelper.pesan(getApplicationContext(), "Error parsing data");
                                    }
                                }
                            }
                        });

            } catch (Exception e) {
//                KolakaHelper.pesan(getApplicationContext(), "Error get data");
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action

            Intent a1 = new Intent(getApplicationContext(), MainMenu.class);
            startActivity(a1);
            finish();
        } else if (id == R.id.nav_gallery) {



        } else if (id == R.id.nav_send) {
            //ketika di klik tombol keluar
            //sesi logout
            sesi.logout();
            //pindah ke halaman Login
            startActivity(new Intent(c, LoginActivity.class));
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
