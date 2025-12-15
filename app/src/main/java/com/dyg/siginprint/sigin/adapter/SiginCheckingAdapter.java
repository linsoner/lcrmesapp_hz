package com.dyg.siginprint.sigin.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dyg.siginprint.R;
import com.dyg.siginprint.base.adapter.BaseAdapter;
import com.dyg.siginprint.sigin.model.BarcodeBean;

import java.util.List;


/**
 * author : DC-DingYG
 * e-mail : dingyg012655@126.com
 * time : 2021/07/07
 * desc : *
 */
public class SiginCheckingAdapter extends BaseAdapter<SiginCheckingAdapter.ViewHolder, BarcodeBean> {

    private List<BarcodeBean> bbList;

    public SiginCheckingAdapter(Context context) {
        super(context);
    }

    @Override
    public void notifyDataSetChanged(List dataList) {
        this.bbList = dataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.adapter_sigin_barcode, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return bbList == null ? 0 : bbList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvCustomerPn;//客户料号
        public TextView tvQty;//数量
        public TextView tvLotNo;//批号
        public TextView tvDateCode;//周期
        public TextView tvPn;//公司料号

        public ViewHolder(View itemView) {
            super(itemView);
            tvCustomerPn = itemView.findViewById(R.id.tv_customer_pn);
            tvQty = itemView.findViewById(R.id.tv_qty);
            tvLotNo = itemView.findViewById(R.id.tv_lot_no);
            tvDateCode = itemView.findViewById(R.id.tv_date_code);
            tvPn = itemView.findViewById(R.id.tv_pn);
        }

        public void setData(int position){
            BarcodeBean bean = bbList.get(position);
            tvCustomerPn.setText(bean.getCustomerPn());
            tvQty.setText(bean.getQty() + "");
            tvLotNo.setText(bean.getLotNo());
            tvDateCode.setText(bean.getDateCode());
            tvPn.setText(bean.getPn());
        }
    }
}
