package com.example.mybeacon;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.minew.beaconplus.sdk.MTPeripheral;

import java.util.List;

public class MTMagazinAdapter extends RecyclerView.Adapter<MTMagazinAdapter.ViewHolder> {
    private List<MTPeripheral> LocalDataSet;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup,int viewType){
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycleviewitem,viewGroup,false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView textView;
        public ViewHolder(View view){
            super(view);
            textView=(TextView) view.findViewById(R.id.mytextView);
        }
        public void setDataAndUi(MTPeripheral mtPeripheral) {
            textView.setText(mtPeripheral.mMTFrameHandler.getMac());
        }
        public TextView getTextView(){
            return textView;
        }
    }
    public MTPeripheral getData(int position) {
        return LocalDataSet.get(position);
    }

    public interface OnItemClickListener{
        void onItemClick(View view,int position);
        void onItemLongClick(View view,int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener _mOnItemClickListener){

        this.mOnItemClickListener=_mOnItemClickListener;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder,final int position){
        ((ViewHolder) viewHolder).setDataAndUi(LocalDataSet.get(position));

        //viewHolder.getTextView().setText(LocalDataSet.get(position).mMTFrameHandler.getMac());
        if (mOnItemClickListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int pos = viewHolder.getLayoutPosition();
                    String _tt="position :"+pos;
                    Toast.makeText(v.getContext(), "tt", Toast.LENGTH_SHORT).show();
                    Log.v("TAG"," position :"+pos);
                    mOnItemClickListener.onItemClick(viewHolder.itemView, pos);
                }
            });
            viewHolder.getTextView().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = viewHolder.getLayoutPosition();
                    mOnItemClickListener.onItemLongClick(viewHolder.itemView, pos);
                    return false;
                }
            });
        }
    }







    @Override
    public int getItemCount(){
        if(LocalDataSet!=null)
            return LocalDataSet.size();
        return 0;
    }

    public void SetData(List<MTPeripheral> dataset){
        LocalDataSet=dataset;
        notifyDataSetChanged();
    }


}
