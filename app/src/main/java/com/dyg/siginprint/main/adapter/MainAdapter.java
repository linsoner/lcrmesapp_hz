package com.dyg.siginprint.main.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.DrawableUtils;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dyg.siginprint.CutInventory.view.CutInventoryActivity;
import com.dyg.siginprint.R;
import com.dyg.siginprint.baogong.BaogongActivity;
import com.dyg.siginprint.base.adapter.BaseAdapter;
import com.dyg.siginprint.base.tools.DoubleClickU;
import com.dyg.siginprint.checkScan.CheckScanActivity;
import com.dyg.siginprint.checkScan.CheckScanProActivity;
import com.dyg.siginprint.deviceCheck.view.DeviceCheckActivity;
import com.dyg.siginprint.kaigong.KaigongActivity;
import com.dyg.siginprint.main.model.FunItemBean;
import com.dyg.siginprint.materialOtherIn.view.MaterialOtherInActivity;
import com.dyg.siginprint.materialTranser.view.MaterialTransferActivity;
import com.dyg.siginprint.productsOtherIn.view.ProductsOtherInActivity;
import com.dyg.siginprint.productsTransfer.view.productsTransferActivity;
import com.dyg.siginprint.purchase.view.PurchaseInReturnActivity;
import com.dyg.siginprint.sigin.view.SiginCheckingActivity;
import com.dyg.siginprint.stockbillSplit.view.StockbillSplitActivity;

import java.util.List;

public class MainAdapter extends BaseAdapter<MainAdapter.ViewHolder,FunItemBean> {

    private List<FunItemBean> dataLists;
    private Context myContent;

    public MainAdapter(Context context){
        super(context);
        myContent = context;
    }

    @Override
    public void notifyDataSetChanged(List dataList) {
        this.dataLists = dataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.adpter_main,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.setData(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FunItemBean itemBean = dataLists.get(position);
                if(!DoubleClickU.isFastDoubleClick(R.layout.adpter_main)) {
                    if(itemBean.getIdent().equals("1") || itemBean.getIdent().equals("11")){
                        //采购进退货
                        Intent intent = new Intent(myContent, PurchaseInReturnActivity.class);
                        myContent.startActivity(intent);
                    }
                    else if(itemBean.getIdent().equals("2")){
                        //扫码核对
                        Intent intent = new Intent(myContent, CheckScanActivity.class);
                        intent.putExtra("viewId", itemBean.getIdent());
                        myContent.startActivity(intent);
                    }
                    else if(itemBean.getIdent().equals("3")){
                        //成品调拨
                        Intent intent = new Intent(myContent, productsTransferActivity.class);
                        myContent.startActivity(intent);
                    }
                    else if(itemBean.getIdent().equals("4")){
                        //客户标签核对
                        Intent intent = new Intent(myContent, SiginCheckingActivity.class);
                        myContent.startActivity(intent);
                    }
                    else if(itemBean.getIdent().equals("5") || itemBean.getIdent().equals("10")){
                        //拆标签
                        Intent intent = new Intent(myContent, StockbillSplitActivity.class);
                        myContent.startActivity(intent);
                    }
                    else if(itemBean.getIdent().equals("6")){
                        //分切出入库
                        Intent intent = new Intent(myContent, CutInventoryActivity.class);
                        myContent.startActivity(intent);
                    }
                    else if(itemBean.getIdent().equals("7")){
                        //设备保养
                        Intent intent = new Intent(myContent, DeviceCheckActivity.class);
                        myContent.startActivity(intent);
                    }
                    else if(itemBean.getIdent().equals("8")){
                        //成品扫码核对
                        Intent intent = new Intent(myContent, CheckScanActivity.class);
                        intent.putExtra("viewId", itemBean.getIdent());
                        myContent.startActivity(intent);
                    }
                    else if(itemBean.getIdent().equals("9")){
                        //生产扫码核对
                        Intent intent = new Intent(myContent, CheckScanProActivity.class);
                        intent.putExtra("viewId", "9");
                        myContent.startActivity(intent);
                    }
                    else if(itemBean.getIdent().equals("12")){
                        //成品其他入库
                        Intent intent = new Intent(myContent, ProductsOtherInActivity.class);
                        intent.putExtra("viewId", itemBean.getIdent());
                        myContent.startActivity(intent);
                    }
                    else if(itemBean.getIdent().equals("13")){
                        //成品其他出库
                        Intent intent = new Intent(myContent, ProductsOtherInActivity.class);
                        intent.putExtra("viewId", itemBean.getIdent());
                        myContent.startActivity(intent);
                    }
                    else if(itemBean.getIdent().equals("14")){
                        //材料其他入库
                        Intent intent = new Intent(myContent, MaterialOtherInActivity.class);
                        intent.putExtra("viewId", itemBean.getIdent());
                        myContent.startActivity(intent);
                    }
                    else if(itemBean.getIdent().equals("15")){
                        //材料其他出库
                        Intent intent = new Intent(myContent, MaterialOtherInActivity.class);
                        intent.putExtra("viewId", itemBean.getIdent());
                        myContent.startActivity(intent);
                    }
                    else if(itemBean.getIdent().equals("16")){
                        //材料调拨
                        Intent intent = new Intent(myContent, MaterialTransferActivity.class);
                        intent.putExtra("viewId", itemBean.getIdent());
                        myContent.startActivity(intent);
                    }
                    else if(itemBean.getIdent().equals("17")){
                        //开工
                        Intent intent = new Intent(myContent, KaigongActivity.class);
                        intent.putExtra("viewId", itemBean.getIdent());
                        myContent.startActivity(intent);
                    }
                    else if(itemBean.getIdent().equals("18")){
                        //报工
                        Intent intent = new Intent(myContent, BaogongActivity.class);
                        intent.putExtra("viewId", itemBean.getIdent());
                        myContent.startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataLists == null ? 0 : dataLists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView titleTV;
        public ImageView iconImg;
        public ViewHolder(View itemView){
             super(itemView);
             titleTV = itemView.findViewById(R.id.titleTv);
             iconImg = itemView.findViewById(R.id.imageIcon);
        }

        public void setData(int position){
             FunItemBean itemBean = dataLists.get(position);
             titleTV.setText(itemBean.getTitle());
             iconImg.setImageResource(itemBean.getIconName());
        }
    }
}
