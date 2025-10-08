package com.dyg.siginprint.HRecycleView.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dyg.siginprint.R;
import com.dyg.siginprint.HRecycleView.data.HRecycleViewData;

import java.util.ArrayList;
import java.util.List;

public class HRecycleViewAdapter extends BaseQuickAdapter<HRecycleViewData, BaseViewHolder> {

    /**
     * data
     */
    //滑动位置
    private int mFixX;
    //需要显示的控件ID的集合
    private ArrayList<Integer> mMoveViewIDList;
    //滑动的布局
    private ArrayList<View> mMoveViewList = new ArrayList<>();
    //数据
    private List<HRecycleViewData> data;
    //item中各控件的的宽度
    private ArrayList<Integer> maxTextViewWidth = new ArrayList<>();
    private int position = -1;
    public OnItemClickListener onItemClickListener;

    /**
     * 创建HRecycleViewAdapter实例
     * @param layoutResId item布局
     * @param data item数据
     * @param lists 不定参数，第一个是要显示的控件ID的集合(必传)，第二个是item中各控件的的宽度(可不传)
     */
    public HRecycleViewAdapter(int layoutResId, List<HRecycleViewData> data,ArrayList<Integer>...lists) {
        super(layoutResId, data);
        this.data = data;
        //控件ID的集合
        this.mMoveViewIDList = lists[0];
        //检测是否传入item宽地集合
        if(lists.length == 2){
            //有传入则直接赋值
            this.maxTextViewWidth = lists[1];
        }else {
            //没有传入默认100px
            for (int i = 0; i < lists[0].size(); i++) {
                this.maxTextViewWidth.add(200);
            }
        }
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, HRecycleViewData item) {
        LinearLayout moveLayout = holder.getView(R.id.item_view);
        if (mMoveViewIDList != null && mMoveViewIDList.size() != 0 && maxTextViewWidth != null && maxTextViewWidth.size() != 0 ) {
            for (int i = 0; i < mMoveViewIDList.size(); i++) {
                TextView textView = (TextView) holder.getView(mMoveViewIDList.get(i));
                textView.setText(item.getHRecycleViewData().get(i));
                textView.setPadding(10, 3, 10, 3);
                LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) textView.getLayoutParams();
                params.width = maxTextViewWidth.get(i) + 20;
                textView.setLayoutParams(params);
                textView.setVisibility(View.VISIBLE);
                int finalI = i;
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(onItemClickListener!=null){
                            onItemClickListener.onItemClick(holder.getAdapterPosition(),finalI);
                        }
                    }
                });
            }
        }
        //新增mFixX属性并设置初始化到滑动的相对位置
        moveLayout.scrollTo(mFixX, 0);
        mMoveViewList.add(moveLayout);

        RelativeLayout itemLayout = holder.getView(R.id.table);
        if(position == holder.getAdapterPosition()){
            int color = Color.argb(50, 50, 50, 50);
            itemLayout.setBackgroundColor(color);
        }else {
            itemLayout.setBackgroundResource(R.color.white);

        }

    }

    @NonNull
    @Override
    public List<HRecycleViewData> getData() {
        return data;
    }

    public void setData(List<HRecycleViewData> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void clearData() {
        data.clear();
    }

    public void setFixX(int fixX){
        mFixX=fixX;
    }

    public ArrayList<View> getMoveViewList(){
            return mMoveViewList;
    }

    public void setDefaultWidth(int defaultWidth){
        for (int i = 0; i < maxTextViewWidth.size(); i++) {
            this.maxTextViewWidth.set(i, defaultWidth);
        }
    }

    /**
     * 长按变色，再次长按取消变色
     */
    public void setBackground(int p){
        if(position == p){
            position = -1;
        }else {
            this.position = p;
        }
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int row , int column);
    }
}
