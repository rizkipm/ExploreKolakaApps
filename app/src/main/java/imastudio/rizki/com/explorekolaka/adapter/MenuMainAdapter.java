package imastudio.rizki.com.explorekolaka.adapter;

/**
 * Created by imastudio on 1/18/16.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import imastudio.rizki.com.explorekolaka.R;
import imastudio.rizki.com.explorekolaka.model.ItemInfo;
import imastudio.rizki.com.explorekolaka.model.MenuItem;


public class MenuMainAdapter extends ArrayAdapter<MenuItem> {
    private Context mContext;
    private int layoutResourceId;
    private ArrayList<MenuItem> mGridData = new ArrayList<MenuItem>();

    public MenuMainAdapter(Context mContext, int layoutResourceId, ArrayList<MenuItem> mGridData) {
        super(mContext, layoutResourceId, mGridData);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mGridData = mGridData;
    }


    /**
     * Updates grid data and refresh grid items.
     * @param mGridData
     */
    public void setGridData(ArrayList<MenuItem> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.titleTextView = (TextView) row.findViewById(R.id.grid_item_title);
            holder.titleharga = (TextView) row.findViewById(R.id.harga);
            holder.hrgPromo = (TextView) row.findViewById(R.id.harga_promo);
//            holder.diskon = (TextView) row.findViewById(R.id.nmToko);
            holder.imageView = (ImageView) row.findViewById(R.id.grid_item_image);
//            holder.wilayah = (TextView)row.findViewById(R.id.lokasiTani);
            holder.btnBeliSkrg = (Button)row.findViewById(R.id.btnBeliSkrg);


            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        final MenuItem item = mGridData.get(position);
        holder.titleTextView.setText(Html.fromHtml(item.getNama_menu()));
//        holder.titleharga.setText("Rp. " + Html.fromHtml(item.getJudul_info()) + " /kg");
//        holder.hrgPromo.setText("Rp. " + Html.fromHtml(item.getHarga_promo()) + " /kg");
//        holder.titleharga.setPaintFlags(holder.titleharga.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
//        holder.diskon.setText(item.getId_user());
//        holder.hrgPromo.setText("Stok : " + item.getLat_wisata());
//        holder.wilayah.setText(item.getId_info());
//        holder.btnBeliSkrg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), DetailProduk.class);
//
////                //Pass the image title and url to DetailsActivity
//                intent.putExtra("id_produk", item.getId()).
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
//                getContext().startActivity(intent);
//            }
//        });

        Picasso.with(getContext())
                .load(item.getIcon_menu())
                .resize(150, 150)
                .centerCrop()
                .into(holder.imageView);
//        Glide.with(mContext).load(item.getImage()).into(holder.imageView);
        return row;
    }

    static class ViewHolder {
        TextView titleharga;
        TextView titleTextView, hrgPromo;
        ImageView imageView;
        TextView diskon;
        TextView wilayah;
        Button btnBeliSkrg;
    }
}
