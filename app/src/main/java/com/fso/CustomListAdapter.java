package com.fso;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hoint on 16/06/2017.
 */

public class CustomListAdapter extends BaseAdapter {

    private List<Location> listData;
    private LayoutInflater layoutInflater;
    private Context context;

    public CustomListAdapter(Context aContext,  List<Location> listData) {
        this.context = aContext;
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.listview_item, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView_info);
            holder.countryNameView = (TextView) convertView.findViewById(R.id.textView_address);
            holder.populationView = (TextView) convertView.findViewById(R.id.textView_time);
            convertView.setTag(holder);


        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Location mLocation = this.listData.get(position);
        holder.countryNameView.setText(mLocation.Addredss);
        holder.populationView.setText("Thời gian cập nhật: " + mLocation.Time);
        Log.d("Status1:  ", mLocation.Image);
        if(mLocation.Image.equals("")){
            holder.imageView.setImageResource(R.drawable.logo);
        }
        else {
            new DownloadImageTask(holder.imageView).execute(mLocation.Image);
        }


        return convertView;
    }

    public void updateResults(ArrayList<Location> results) {
        this.listData = results;
        //Triggers the list update
        notifyDataSetChanged();
    }

    static class ViewHolder {
        ImageView imageView;
        TextView countryNameView;
        TextView populationView;
    }
}
