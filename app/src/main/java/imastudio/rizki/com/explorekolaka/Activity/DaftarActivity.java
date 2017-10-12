package imastudio.rizki.com.explorekolaka.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import imastudio.rizki.com.explorekolaka.BaseActivity;
import imastudio.rizki.com.explorekolaka.Helper.KolakaHelper;
import imastudio.rizki.com.explorekolaka.LoginActivity;
import imastudio.rizki.com.explorekolaka.R;

/**
 * Created by MAC on 9/29/17.
 */

public class DaftarActivity extends BaseActivity {
    private MaterialEditText txtNama, txtEmail, txtPassword,txtConfirmPass, txtAlamat, txtPhone;
    private Button btnSignUp;
    private TextView btnSignIn;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_daftar);
        setupView();
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(btnAnimasi);
                if(!KolakaHelper.isOnline(c)){
                    KolakaHelper.alertMessageNoInternet(c);
                }else{
                    simpanAction();

                }
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(btnAnimasi);
                startActivity(new Intent(c, LoginActivity.class));
                finish();
            }
        });
    }
    private void setupView() {
        txtNama = (MaterialEditText)findViewById(R.id.etDaftarNama);
        txtEmail = (MaterialEditText)findViewById(R.id.etDaftarEmail);
        txtPassword = (MaterialEditText)findViewById(R.id.etDaftarPassword);
        txtConfirmPass = (MaterialEditText)findViewById(R.id.etDaftarKonPassword);
        txtAlamat = (MaterialEditText)findViewById(R.id.etDaftarAlamat);
        txtPhone = (MaterialEditText)findViewById(R.id.etDaftarPhone);
        btnSignIn = (TextView)findViewById(R.id.tvSigin);
        btnSignUp =(Button)findViewById(R.id.btnDaftarSignin);
    }

    private void simpanAction() {

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
//            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
//        } else {
        //
        txtEmail.setError(null);
        txtPassword.setError(null);
        txtNama.setError(null);
        txtPhone.setError(null);
        txtConfirmPass.setError(null);
        txtAlamat.setError(null);

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
        } else if (KolakaHelper.isEmpty(txtNama)) {
            txtNama.setError("Nam harus diisi");
            facusView = txtNama;
            cancel = true;
        } else if (KolakaHelper.isEmpty(txtPhone)) {
            txtPhone.setError("Phone harus diisi");
            facusView = txtPhone;
            cancel = true;
        } else if (KolakaHelper.isEmpty(txtAlamat)) {
            txtAlamat.setError("Alamat harus diisi");
            facusView = txtAlamat;
            cancel = true;
        } else if (KolakaHelper.isEmpty(txtPassword)) {
            txtPassword.setError("Password harus diisi");
            facusView = txtPassword;
            cancel = true;
        } else if (KolakaHelper.isEmpty(txtConfirmPass)) {
            txtConfirmPass.setError("Confirm harus diisi");
            facusView = txtConfirmPass;
            cancel = true;
        } else if (KolakaHelper.isCompare(txtPassword, txtConfirmPass)) {
            txtConfirmPass.setError("Confirm Password harus sesuai dengan password");
            facusView = txtConfirmPass;
            cancel = true;
        }

        if (cancel) {
            facusView.requestFocus();
        } else {
            // kirim data ke server
            String url = KolakaHelper.BASE_URL + "daftar";
            Map<String, String> parampa = new HashMap<>();
            parampa.put("email", txtEmail.getText().toString());
            parampa.put("usernama", txtNama.getText().toString());
            parampa.put("phone", txtPhone.getText().toString());
            parampa.put("password", txtPassword.getText().toString());
            parampa.put("alamat", txtAlamat.getText().toString());

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
                                        // NurHelper.pesan(c, pesan);
                                        if (result.equalsIgnoreCase("true")) {
                                            startActivity(new Intent(c, LoginActivity.class));
                                            finish();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        // NurHelper.pesan(c, "Error Convert json");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        // NurHelper.pesan(c, "Error parsing data");
                                    }
                                }
                            }
                        });

            } catch (Exception e) {
                KolakaHelper.pesan(c, "Error get data");
                e.printStackTrace();
            }

        }
//        }
    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                           int[] grantResults) {
//        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission is granted
//                simpanAction();
//            } else {
//                Toast.makeText(this, "Until you grant the permission, we canot register ", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

}
