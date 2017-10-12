package imastudio.rizki.com.explorekolaka;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import imastudio.rizki.com.explorekolaka.Activity.DaftarActivity;
import imastudio.rizki.com.explorekolaka.Activity.MainMenu;
import imastudio.rizki.com.explorekolaka.Helper.KolakaHelper;

/**
 * Created by MAC on 9/29/17.
 */

public class LoginActivity extends BaseActivity {


        private MaterialEditText txtEmail, txtPass;
        private Button btnLogin;
        private TextView btnSignUp, forgotPass;
        private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_login);
            setupView();
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(btnAnimasi);
                    if(!KolakaHelper.isOnline(c)){
                        KolakaHelper.alertMessageNoInternet(c);
                    }else{
                        actionLogin();
                    }

                }
            });
            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(btnAnimasi);
                    startActivity(new Intent(c, DaftarActivity.class));
                    finish();
                }
            });
//            forgotPass.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    v.startAnimation(btnAnimasi);
//                    startActivity(new Intent(c, ForgotPass.class));
//
//                }
//            });
        }

        private void actionLogin() {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission
                    (Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_REQUEST_READ_CONTACTS);
                //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
            } else {
                // Android version is lesser than 6.0 or the permission is already granted.


                txtEmail.setError(null);
                txtPass.setError(null);

                boolean cancel = false;
                View facusView = null;
                //lakukan pengecekan pada setiap fields
                if (KolakaHelper.isEmpty(txtEmail)) {
                    txtEmail.setError("Email harus diisi");
                    facusView = txtEmail;
                    cancel = true;
                } else if (!KolakaHelper.isEmailValid(txtEmail)) {
                    txtEmail.setError("Format Email tidak valid");
                    facusView = txtEmail;
                    cancel = true;
                } else if (KolakaHelper.isEmpty(txtPass)) {
                    txtPass.setError("Pass harus diisi");
                    facusView = txtEmail;
                    cancel = true;
                }

                if (cancel) {
                    facusView.requestFocus();
                } else {
                    // kirim data ke server
                    String url = KolakaHelper.BASE_URL + "login";
                    String dd = KolakaHelper.getDeviceUUID(c);
                    Map<String, String> parampa = new HashMap<>();
                    parampa.put("t_email", txtEmail.getText().toString());
                    parampa.put("t_password", txtPass.getText().toString());
                    parampa.put("device", dd);

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
                                                String pesan = json.getString("msg");
                                                KolakaHelper.pesan(c, pesan);
                                                if (result.equalsIgnoreCase("true")) {
                                                    //membuat session
                                                    String token = json.getString("token");
                                                    sesi.cerateLoginSession(token);
                                                    JSONObject obj = json.getJSONObject("data");
                                                    sesi.setNama(obj.getString("usernama"));
                                                    sesi.setEmail(obj.getString("email"));
                                                    sesi.setPhone(obj.getString("no_hp"));
                                                    sesi.setIduser(obj.getString("id_user"));
                                                    sesi.setAlamat(obj.getString("alamat"));
                                                    //setelah data selesai disimpan, arahkan ke halamanyang diinginkan
                                                    startActivity(new Intent(c, MainMenu.class));
                                                    finish();
                                                    // NurHelper.pesan(c, pesan);
                                                } else {
                                                    //NurHelper.pesan(c, pesan);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                KolakaHelper.pesan(c, "Error parsing data");
                                            }
                                        }
                                    }
                                });

                    } catch (Exception e) {
                        // NurHelper.pesan(c, "Error get data");
                        e.printStackTrace();
                    }
                }
            }

        }
        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                               int[] grantResults) {
            if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted
//                actionLogin();
                    actionLogin();
                    //return true;
                } else {
                    Toast.makeText(this, "Until you grant the permission, we canot login", Toast.LENGTH_SHORT).show();
                }
            }
        }

        private void setupView() {
            txtEmail = (MaterialEditText)findViewById(R.id.etLoginEmail);
            txtPass = (MaterialEditText)findViewById(R.id.etLoginPassword);
            btnLogin=(Button)findViewById(R.id.btnLoginSignin);
            btnSignUp = (TextView)findViewById(R.id.btnLoginSignUp);
//            forgotPass = (TextView)findViewById(R.id.txtForgotPass);
        }



}
