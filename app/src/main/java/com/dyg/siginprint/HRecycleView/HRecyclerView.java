package com.dyg.siginprint.HRecycleView;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.SimpleClickListener;
import com.dyg.siginprint.R;
import com.dyg.siginprint.HRecycleView.adapter.HRecycleViewAdapter;

import java.util.ArrayList;

public class HRecyclerView extends RelativeLayout {

    /**
     * data
     */
    //手指按下时的位置
    private float mStartX = 0;
    //滑动时和按下时的差值
    private int mMoveOffsetX = 0;
    //最大可滑动差值
    private int mFixX = 0;
    //触发拦截手势的最小值
    private int mTriggerMoveDis = 30;
    //可滑动的总宽度
    private int mRightTotalWidth = 0;
    private int mAllWidth = 0;
    //textView的宽度集合
    private ArrayList<Integer> mViewWidthList = new ArrayList<>();
    //首行标题
    private ArrayList<String> mViewTitle;
    //固定的行数，默认是1
    private int mFixedTextView = 1;
    //textview的宽度，不传值则默认100
    private int defaultWidth = 200;

    private int leftInterval = 10;

    private int rightInterval = 10;

    /**
     * View
     */
    //头部title布局
    private LinearLayout mRightMoveLayout;
    //展示数据时使用的RecycleView
    private RecyclerView mRecyclerView;
    //RecycleView的Adapter
    private HRecycleViewAdapter mAdapter;
    //需要滑动的View集合
    private ArrayList<View> mMoveViewList = new ArrayList();
    private Context context;

    public HRecyclerView(Context context) {
        this(context, null);
    }

    public HRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    private void initView() {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
//        linearLayout.addView(createLine(), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        linearLayout.addView(createHeadLayout());
        linearLayout.addView(createMoveRecyclerView());
        addView(linearLayout, new LinearLayout.LayoutParams(allWidth(), ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * 创建头部布局
     */
    private View createHeadLayout() {
        //整体布局属性
        LinearLayout headLayout = new LinearLayout(getContext());
        headLayout.setGravity(Gravity.LEFT);
        headLayout.setOrientation(LinearLayout.HORIZONTAL);
//        headLayout.setBackgroundResource(R.drawable.frame_hrecyclerview);
        //左边固定布局属性
        LinearLayout leftLayout = new LinearLayout(getContext());
        leftLayout.setOrientation(LinearLayout.HORIZONTAL);

        for (int i = 0; i < mFixedTextView; i++) {
            if(mViewWidthList == null || mViewWidthList.size() == 0){
                addListHeaderTextView(mViewTitle.get(i), defaultWidth, leftLayout);
            }else {
                addListHeaderTextView(mViewTitle.get(i), mViewWidthList.get(i), leftLayout);
            }
        }
        leftLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        headLayout.addView(leftLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        //右边滑动布局属性
        mRightMoveLayout = new LinearLayout(getContext());
        mRightMoveLayout.setGravity(Gravity.LEFT);
        mRightMoveLayout.setOrientation(LinearLayout.HORIZONTAL);
        for (int i = mFixedTextView; i < mViewTitle.size(); i++) {
            if(mViewWidthList == null || mViewWidthList.size() == 0){
                addListHeaderTextView(mViewTitle.get(i), defaultWidth, mRightMoveLayout);
            }else {
                addListHeaderTextView(mViewTitle.get(i), mViewWidthList.get(i), mRightMoveLayout);
            }
        }
        headLayout.addView(mRightMoveLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return headLayout;
    }

    /**
     * 创建数据展示布局
     */
    private View createMoveRecyclerView() {
        RelativeLayout linearLayout = new RelativeLayout(getContext());
        linearLayout.setGravity(Gravity.CENTER);
        mRecyclerView = new RecyclerView(getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        if (null != mAdapter) {
            mAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
            mRecyclerView.setAdapter(mAdapter);
            mMoveViewList = mAdapter.getMoveViewList();
        }
        linearLayout.addView(mRecyclerView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return linearLayout;
    }

    /**
     * 创建分割线
     * @return
     */
    public View createLine(){
        View view = new View(getContext());
        view.setBackgroundColor(Color.BLACK);
        return view;
    }

    /**
     * 设置adapter
     */
    public void setAdapter(HRecycleViewAdapter adapter) {
        this.mAdapter = adapter;
        initView();
    }

    /**
     * 设置表格宽度
     */
    public void setViewWidth(ArrayList<Integer> maxTextViewWidthList) {
        this.mViewWidthList = maxTextViewWidthList;
    }

    /**
     * 设置头部title单个布局
     */
    private TextView addListHeaderTextView(String headerName, int width, LinearLayout layout) {
        TextView textView = new TextView(getContext());
        textView.setText(headerName);
        textView.setPadding(leftInterval, 3, rightInterval, 3);
        textView.setWidth(width + leftInterval+rightInterval);
        textView.setMaxLines(1);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(16);
        textView.getPaint().setFakeBoldText(true);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));

        layout.addView(textView, width + leftInterval+rightInterval, ViewGroup.LayoutParams.WRAP_CONTENT);
        return textView;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                mFixX = mMoveOffsetX;
                //每次左右滑动都要更新Adapter中的mFixX的值
                mAdapter.setFixX(mFixX);
                break;
            case MotionEvent.ACTION_DOWN:
                mStartX = ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                int offsetX = (int) Math.abs(ev.getX() - mStartX);
                if (offsetX > mTriggerMoveDis) {//水平移动大于30触发拦截
                    return true;
                } else {
                    return false;
                }
            default:
                //throw new IllegalStateException("Unexpected value: " + ev.getAction());
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 右边可滑动的总宽度
     */
    private int rightTitleTotalWidth() {
        if (0 == mRightTotalWidth) {
            for (int i = mFixedTextView; i < mViewTitle.size(); i++) {
                if(mViewWidthList == null || mViewWidthList.size() == 0){
                    mRightTotalWidth = mRightTotalWidth + defaultWidth + leftInterval+rightInterval;
                }else {
                    mRightTotalWidth = mRightTotalWidth + mViewWidthList.get(i) + leftInterval+rightInterval;
                }

            }
            mRightTotalWidth = mRightTotalWidth + 1;
        }
        return mRightTotalWidth;
    }

    /**
     *
     */
    private int allWidth() {
        if (0 == mAllWidth) {
            for (int i = 0; i < mViewTitle.size(); i++) {
                if(mViewWidthList == null || mViewWidthList.size() == 0){
                    mAllWidth = mAllWidth + defaultWidth;
                }else {
                    mAllWidth = mAllWidth + mViewWidthList.get(i) ;
                }

            }
        }
        return mAllWidth + (leftInterval+rightInterval)*mViewTitle.size();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_MOVE:
                int offsetX = (int) Math.abs(event.getX() - mStartX);
                if (offsetX > 30) {
                    mMoveOffsetX = (int) (mStartX - event.getX() + mFixX);
                    if (0 > mMoveOffsetX) {
                        mMoveOffsetX = 0;
                    } else {
                        //当滑动大于最大宽度时，不在滑动（右边到头了）
                        if ((mRightMoveLayout.getWidth() + mMoveOffsetX) > rightTitleTotalWidth()) {
                            mMoveOffsetX = rightTitleTotalWidth() - mRightMoveLayout.getWidth();
                        }
                    }
                    //跟随手指向右滚动
                    mRightMoveLayout.scrollTo(mMoveOffsetX, 0);
                    if (null != mMoveViewList) {
                        for (int i = 0; i < mMoveViewList.size(); i++) {
                            //使每个item随着手指向右滚动
                            mMoveViewList.get(i).scrollTo(mMoveOffsetX, 0);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mFixX = mMoveOffsetX; //设置最大水平平移的宽度
                //每次左右滑动都要更新CommonAdapter中的mFixX的值
                mAdapter.setFixX(mFixX);
                break;
            default:
                //throw new IllegalStateException("Unexpected value: " + event.getAction());
        }
        return super.onTouchEvent(event);
    }

    /**
     * 列表头部数据
     */
    public void setHeaderListData(ArrayList<String> headerListData) {
        this.mViewTitle = headerListData;
    }

    /**
     * 设置固定表格的个数
     */
    public void setFixedTextView(int quantity){
        this.mFixedTextView = quantity;
    }

    /**
     * 设置最大宽度(输入单位为dp)
     */
    public void setDefaultWidth(int defaultWidth){
//        if(mViewWidthList != null && mViewWidthList.size() != 0){
//            int maxWidthpx = dip2px(getContext(), maxWidthdp);
//            for (int i = 0; i < mViewWidthList.size(); i++) {
//                if(maxWidthpx < mViewWidthList.get(i)){
//                    mViewWidthList.set(i, maxWidthpx);
//                }
//            }
//        }
//        int maxWidthpx = dip2px(getContext(), defaultWidth);
        this.defaultWidth = defaultWidth;
    }

    public void cleanView(){
        removeAllViews();
        this.mAllWidth = 0;
        this.mRightTotalWidth = 0;
    }

    /**
     * 设置长按背景变色，为防止错乱需要输入item的总数
     * @param itemCount item的总数
     */
    public void setItemViewCacheSize(int itemCount, SimpleClickListener listener){
        mRecyclerView.getRecycledViewPool().setMaxRecycledViews(0, itemCount);
        mRecyclerView.setItemViewCacheSize(itemCount);
        mRecyclerView.addOnItemTouchListener(listener);
    }
}
