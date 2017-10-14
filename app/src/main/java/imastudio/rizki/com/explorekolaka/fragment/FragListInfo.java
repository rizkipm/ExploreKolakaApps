package imastudio.rizki.com.explorekolaka.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import imastudio.rizki.com.explorekolaka.Activity.DetailInfo;
import imastudio.rizki.com.explorekolaka.Helper.Constant;
import imastudio.rizki.com.explorekolaka.Helper.KolakaHelper;
import imastudio.rizki.com.explorekolaka.Helper.No_Internet;
import imastudio.rizki.com.explorekolaka.R;
import imastudio.rizki.com.explorekolaka.model.ItemInfoModel;
import imastudio.rizki.com.explorekolaka.model.MenuItemModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragListInfo extends Fragment {


    private ListView lvData;
    private ArrayList<ItemInfoModel> data;
    String idWilayah, status_ops;
    private RestoranAdapter adapter;



    AQuery aq;

    public FragListInfo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.data_info, container, false);
        data = new ArrayList<>();
        lvData = (ListView) v.findViewById(R.id.lvRestoranFragment);
        Bundle bundle = getArguments();
        idWilayah = bundle.getString(Constant.id_menu);
        aq = new AQuery(getActivity());
        if (!KolakaHelper.isOnline(getActivity())) {
            startActivity(new Intent(getActivity(), No_Internet.class));
            getActivity().finish();

        } else {

            getDataInfo();

        }
        return v;
    }



    private void getDataInfo() {
        data.clear();
        //ambil data dari server
        String url = KolakaHelper.BASE_URL + "get_menuByID";
        Map<String, String> parampa = new HashMap<>();

        parampa.put("id_menu", idWilayah);
        //menambahkan progres dialog loading
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setInverseBackgroundForced(false);
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.setMessage("Loading..");
        try {
            //mencari url dan parameter yang dikirimkan
            KolakaHelper.pre("Url : " + url + ", params " + parampa.toString());
            //koneksi ke server meggunakan aquery
            aq.progress(progressDialog).ajax(url, parampa, String.class,
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
                                            JSONObject object = jsonArray.getJSONObject(a);
                                            //ambil data perbooking dan masukkan ke kelas object model
                                            ItemInfoModel b = new ItemInfoModel();
                                            b.setJudul_info(object.getString("judul_info"));
                                            b.setId_info(object.getString("id_info"));
                                            b.setId_menu(object.getString("id_menu"));
                                            b.setDesk_info(object.getString("desk_info"));
                                            b.setGambar_info(object.getString("gambar_info"));


                                            //memasukkan data kedalam model booking
                                            data.add(b);
                                            //masukkan data arraylist kedalam custom adapter
                                            adapter = new RestoranAdapter(getActivity(), data);
                                            lvData.setAdapter(adapter);

                                        }
                                    } else {
                                        //  NurHelper.pesan(getActivity(), pesan);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    // NurHelper.pesan(getActivity(), "Error parsing data");
                                }
                            }
                        }
                    });

        } catch (Exception e) {
            //  NurHelper.pesan(getActivity(), "Error get data");
            e.printStackTrace();
        }
    }

    private class RestoranAdapter extends BaseAdapter {
        private Activity c;
        private ArrayList<ItemInfoModel> datas;
        private LayoutInflater inflater = null;

        public RestoranAdapter(Activity c, ArrayList<ItemInfoModel> data) {
            this.c = c;
            datas = new ArrayList<>();
            this.datas = data;
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder {
            TextView restoAlamat, restoNama;
            ImageView restoImg, restoInfo;

        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder = null;
            if (v == null) {
                inflater = (LayoutInflater) c.getSystemService(c.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.item_data, null);
                holder = new ViewHolder();
                holder.restoNama = (TextView) v.findViewById(R.id.txtRestoFragmentJudul);
                holder.restoAlamat = (TextView) v.findViewById(R.id.txtRestoFragmentAlamat);
                holder.restoImg = (ImageView) v.findViewById(R.id.ivRestoFragLogo);

                v.setTag(holder);

            } else {
                holder = (ViewHolder) v.getTag();
            }
            //masukkan data booking
            final ItemInfoModel b = datas.get(position);
            //holder.tvTanggal.setText(NurHelper.tglJamToInd(b.getBookingTgl()));
//            holder.restoInfo.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent a = new Intent(getActivity(), DetailInfo.class);
////                    a.putExtra(Constant.id_menu, datas.get(position).getId_menu());
////                    a.putExtra(Constant., datas.get(position).getLogoResto());
////                    a.putExtra(Constant.NAMA_RESTO, datas.get(position).getNmResto());
////                    a.putExtra(Constant.ID_WILAYAH, idWilayah);
////                    a.putExtra(Constant.ALAMAT_RESTO, datas.get(position).getAlamatResto());
//                    startActivity(a);
//                }
//            });

            final String nlaiMenu = b.getId_menu();
            holder.restoNama.setText(b.getJudul_info());
            holder.restoNama.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (nlaiMenu.equalsIgnoreCase("2")){

                    }else{
                        Intent intent = new Intent(getActivity(), DetailInfo.class);
//                //Pass the image title and url to DetailsActivity
                        intent.putExtra("id_info", b.getId_info());

//                //Start details activity
                        startActivity(intent);
                    }


                }
            });


            Picasso.with(getContext()).load(KolakaHelper.BASE_URL_IMAGE+b.getGambar_info()).placeholder(R.drawable.no_image).
                    into(holder.restoImg);
            holder.restoImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (nlaiMenu.equalsIgnoreCase("2")){

                    }else{
                        Intent intent = new Intent(getActivity(), DetailInfo.class);
//                //Pass the image title and url to DetailsActivity
                        intent.putExtra("id_info", b.getId_info());

//                //Start details activity
                        startActivity(intent);
                    }

                }
            });
//
            return v;
        }
    }

//    public void callParentMethod(){
//        getActivity().onBackPressed();
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//
//        if(isRemoving()){
//            // onBackPressed()
//            Toast.makeText(getActivity(), "Silahkan Lakukan Chekout Terlebih Dahulu Untuk belanja di Rumah Makan lainnya",
//                    Toast.LENGTH_SHORT).show();
//        }
//
//    }
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        if(isRemoving()){
//            // onBackPressed()
//        }
//    }

}
