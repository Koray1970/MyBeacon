package com.example.mybeacon;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.minew.beaconplus.sdk.MTPeripheral;

import java.util.List;

public class MTMagazinAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public List<MTPeripheral> mData;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        viewHolder = new ViewHolder(View.inflate(parent.getContext(), R.layout.recycleviewitem, null));
        return viewHolder;
    }

    public MTPeripheral getData(int position) {
        return mData.get(position);
    }

    public interface OnClickListener {

        /*void OnItemClickCheck(View view, int position);*/
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    private OnClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).setDataAndUi(mData.get(position),position);
       /* holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getLayoutPosition();
                mOnItemClickListener.onClick(holder.itemView, pos);
            }
        });*/

            /*holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemLongClick(holder.itemView, pos);
                    return false;
                }
            });*/

    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public void setData(List<MTPeripheral> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        MaterialButton data;

        public ViewHolder(View itemView) {
            super(itemView);
            data = (MaterialButton) itemView.findViewById(R.id.mytextView);
        }

        public void setDataAndUi(MTPeripheral mtPeripheral,int position) {
            data.setText(mtPeripheral.mMTFrameHandler.getMac());
            data.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(v, position);
                }
            });
        }
    }


}
