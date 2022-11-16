package com.example.mybeacon;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.minew.beaconplus.sdk.MTPeripheral;

import java.util.List;

public class MTMagazinAdapter extends RecyclerView.Adapter<MTMagazinAdapter.ViewHolder> {
    private List<MTPeripheral> LocalDataSet;
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView textView;
        public ViewHolder(View view){
            super(view);
            textView=(TextView) view.findViewById(R.id.mytextView);
        }
        public TextView getTextView(){
            return textView;
        }
    }
    public MTMagazinAdapter(){

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup,int viewType){
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycleviewitem,viewGroup,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ViewHolder viewHolder,final int position){
        viewHolder.getTextView().setText(LocalDataSet.get(position).mMTFrameHandler.getName());
    }
    @Override
    public int getItemCount(){
        return LocalDataSet.size();
    }

    public void SetData(List<MTPeripheral> dataset){
        LocalDataSet=dataset;
    }
}
