package com.josedlpozo.bluetootharduino;

/**
 * Created by josedlpozo on 16/4/15.
 */
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class ReciclerViewAdapter extends RecyclerView.Adapter<ReciclerViewAdapter.ViewHolder>{


    private List<Bluetooth> mDataset;
    private Context context;
    private ViewHolder.ClickListener clickListener;


    public ReciclerViewAdapter(ViewHolder.ClickListener clickListener,Context c) {
        this.clickListener = clickListener;
        this.context = c;

        mDataset = new ArrayList<Bluetooth>();
    }

    public void add(Bluetooth i) {
        mDataset.add(i);
        notifyItemInserted(mDataset.indexOf(i));
    }
    public void remove(Bluetooth bt) {
        int position = mDataset.indexOf(bt);

        if(position != -1) {
            mDataset.remove(position);
            notifyItemRemoved(position);
        }
    }

    public List<Bluetooth> getBluetoothList(){
        return mDataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bluetooth, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Bluetooth item = mDataset.get(position);
        holder.imageView.setImageDrawable(item.getImageSrc());
        holder.mTextView.setText(item.getName());
        holder.setClickListener(clickListener);

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected ImageView imageView;
        protected TextView mTextView;
        private ClickListener clickListener;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.ivItem);
            mTextView = (TextView) v.findViewById(R.id.tvItem);
            itemView.setOnClickListener(this);
        }

        public interface ClickListener {
            public void onClick(View v, int position);
        }

        public void setClickListener(ClickListener clickListener) {
            this.clickListener = clickListener;
        }


        @Override
        public void onClick(View v) {
            Log.d("xxx", "POSITION " + getPosition());
            clickListener.onClick(v, getPosition());

        }
    }


}
