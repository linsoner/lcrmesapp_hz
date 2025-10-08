package com.dyg.siginprint.wiget.dialog.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dyg.siginprint.R;
import com.dyg.siginprint.base.adapter.BaseAdapter;
import com.dyg.siginprint.wiget.dialog.model.BaseDataModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ListDialogAdapter extends BaseAdapter<ListDialogAdapter.ViewHolder, BaseDataModel> {

    private Context context;
    private String analysisKey;
    private List dataList;
    private Callback callback;
    public ListDialogAdapter(Context context , String analysisKey , Callback callback){
        super(context);
        this.context = context;
        this.analysisKey = analysisKey;
        this.callback = callback;
    }

    @Override
    public void notifyDataSetChanged(List dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(mInflater.inflate(R.layout.view_dialog_list_cell,parent,false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(callback != null){
                    callback.cellAction(dataList.get(position),position);
                }
            }
        });
        holder.setData(position,analysisKey);
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView titleTv;
        public ViewHolder(View itemView){
            super(itemView);
            this.titleTv = itemView.findViewById(R.id.titleTv);
        }

        public void setData(int position , String analysisKey){
            if(analysisKey != null && !analysisKey.equals("")) {
                if(dataList.get(position) instanceof BaseDataModel){
                    BaseDataModel bean = (BaseDataModel)dataList.get(position);
                    titleTv.setText(bean.getName());
                }else {
                    try {
                        JSONObject obj = (JSONObject) dataList.get(position);
                        titleTv.setText(obj.getString(analysisKey));
                    } catch (JSONException e) {
                    }
                }
            } else {
                titleTv.setText(dataList.get(position).toString());
            }
        }
    }

    public interface Callback{
        void cellAction(Object obj,int position);
    }
}
