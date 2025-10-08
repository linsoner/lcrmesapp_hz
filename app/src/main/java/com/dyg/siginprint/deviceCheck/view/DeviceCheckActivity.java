package com.dyg.siginprint.deviceCheck.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.dingyg.qrcodelib.common.QrManager;
import com.dyg.siginprint.HRecycleView.HRecyclerView;
import com.dyg.siginprint.HRecycleView.adapter.HRecycleViewAdapter;
import com.dyg.siginprint.HRecycleView.data.HRecycleViewData;
import com.dyg.siginprint.R;
import com.dyg.siginprint.base.model.Tokens;
import com.dyg.siginprint.base.tools.DoubleClickU;
import com.dyg.siginprint.base.tools.LogUtils;
import com.dyg.siginprint.base.tools.ToastUtil;
import com.dyg.siginprint.base.view.BaseActivity;
import com.dyg.siginprint.deviceCheck.model.DeviceCheckBean;
import com.dyg.siginprint.deviceCheck.presenter.DeviceCheckPresenter;
import com.dyg.siginprint.deviceCheck.presenter.IDeviceCheck;
import com.dyg.siginprint.login.model.LoginBean;
import com.dyg.siginprint.purchase.view.PurchaseInReturnActivity;
import com.dyg.siginprint.wiget.dialog.InputDialog;
import com.dyg.siginprint.wiget.dialog.ListDialog;
import com.dyg.siginprint.wiget.dialog.model.BaseDataModel;
import com.dyg.siginprint.wiget.textview.ClearEditText;
import com.dyg.siginprint.wiget.textview.MyEditText;
import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.OnClick;

import static com.dyg.siginprint.HRecycleView.utils.utils.getHeadWidthList;
import static com.dyg.siginprint.HRecycleView.utils.utils.getIdealWidthList;
import static com.dyg.siginprint.HRecycleView.utils.utils.getMaxWidthList;
import static com.dyg.siginprint.HRecycleView.utils.utils.getSetMaxWidthList;

public class DeviceCheckActivity extends BaseActivity<DeviceCheckPresenter> implements IDeviceCheck , ClearEditText.Listener {

    private ArrayList<HRecycleViewData> dataArrayList = new ArrayList<>();

    private ArrayList<Integer> mMoveViewListID = new ArrayList<>();

    private ArrayList<String> mHeadTitleLst = new ArrayList<>();

    private ArrayList<Integer> headWidthList = new ArrayList<>();

    private ArrayList<Integer> itemWidthList = new ArrayList<>();

    private ArrayList<Integer> maxWidth = new ArrayList<>();

    private ArrayList<Integer> setMaxWidth = new ArrayList<>();

    private ArrayList<Integer> idealWidthList = new ArrayList<>();

    private TextView tv;

    private HRecyclerView jit_hrecyclerview;
    private HRecycleViewAdapter adapter;
    //上面为表格配置

    private ClearEditText qr_deviceSNEt;
    private TextView deviceNameTv;
    private TextView selectWeekTv;
    private MyEditText remarkTv;//备注

    private Activity mActivity;
    private String userCode;

    private String weekName;
    private String weekCode;

    private int CODE_SCAN_QRCODE = 102;

    private List<DeviceCheckBean> scanFinishList = new ArrayList<>();//已扫描解析的条码
    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_device_check;
    }

    @Override
    protected void initPresenter() {
       presenter = new DeviceCheckPresenter(DeviceCheckActivity.this,this);
        initTitle(true, "设备保养", "", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FinishView();
            }
        },null);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        LoginBean loginBean = Hawk.get(Tokens.LoginBean, null);
        if (loginBean == null) finish();
        else {
            userCode = loginBean.getAccount();
            mActivity = DeviceCheckActivity.this;
            tv = findViewById(R.id.tv);
            qr_deviceSNEt = findViewById(R.id.qr_deviceSNEt);
            deviceNameTv = findViewById(R.id.deviceNameTv);
            selectWeekTv = findViewById(R.id.selectWeekTv);
            remarkTv = findViewById(R.id.remarkTv);

            qr_deviceSNEt.initEditText(0, "", 0, 1, true, this);
        }

        //初始化表格
        jit_hrecyclerview = findViewById(R.id.jit_hrecyclerview);

        initHeadList();

        initHR();

        jit_hrecyclerview.setHeaderListData(mHeadTitleLst);

        jit_hrecyclerview.setViewWidth(maxWidth);

        adapter = new HRecycleViewAdapter(R.layout.item_hrecycleview, dataArrayList, mMoveViewListID, maxWidth);
        adapter.onItemClickListener = new HRecycleViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int row,int column) {
                LogUtils.e("点击行==" + row + "点击列==" + column);
                if(column != 0) return;
                InputDialog inputDialog = new InputDialog(mActivity, new InputDialog.Callback() {
                    @Override
                    public void finish(Boolean isConfirm, String inputContent) {
                       scanFinishList.get(row).setItemValue(inputContent);
                       dataArrayList.get(row).getHRecycleViewData().set(0,inputContent);
                       refreshHrecycler();
                    }
                });
                inputDialog.show("修改第"+ (row + 1) +"行记录值",scanFinishList.get(row).getItemValue());
            }
        };
        jit_hrecyclerview.setAdapter(adapter);
    }

    private void initHR(){
        //3.1 获取标题栏宽度
        headWidthList = getHeadWidthList(tv, mHeadTitleLst);
        //3.2 获取每列文本的最大宽度
        //这里不采用计算的高度，直接写死
        // itemWidthList = getItemWidthList(tv, dataArrayList);
        itemWidthList = new ArrayList<>(Arrays.asList(100,350,100,300,100,100,100,400));
        //3.3 得出每列的最大宽度
        maxWidth = getMaxWidthList(headWidthList, itemWidthList);
        //3.4 获取控件设定的最大宽度
        setMaxWidth = getSetMaxWidthList(DeviceCheckActivity.this, R.layout.item_hrecycleview, mMoveViewListID);
        //3.5 比较3.3和3.4得到的数据，得出最适合的宽度
        idealWidthList = getIdealWidthList(maxWidth, setMaxWidth);

    }

    private void initHeadList() {

        //选择要显示的固定控件
        mMoveViewListID.add(R.id.head0);
        //选择要显示的滑动控件
        mMoveViewListID.add(R.id.item0);
        mMoveViewListID.add(R.id.item1);
        mMoveViewListID.add(R.id.item2);
        mMoveViewListID.add(R.id.item3);
        mMoveViewListID.add(R.id.item4);
        mMoveViewListID.add(R.id.item5);

        dataArrayList.clear();
        //设置表头数据
        mHeadTitleLst.add("记录值");
        mHeadTitleLst.add("项目");
        mHeadTitleLst.add("排序");
        mHeadTitleLst.add("规格值");
        mHeadTitleLst.add("下限");
        mHeadTitleLst.add("上限");
        mHeadTitleLst.add("备注");
    }

    //刷新细表和数量
    private void refreshHrecycler(){
        adapter.setData(dataArrayList);
    }

    //清空界面 是否清空保养周期 clearWeek
    private void clearView(boolean clearWeek){
        qr_deviceSNEt.setTextCt("",false);
        qr_deviceSNEt.setTextHint("");
        deviceNameTv.setText("");
        remarkTv.setText("");
        if(clearWeek){
            selectWeekTv.setText("");
            weekCode = "";
            weekName = "";
        }
        scanFinishList.clear();
        dataArrayList.clear();
        refreshHrecycler();
    }

    @Override
    public void baseInfoSuccess(List dataList, int code) {
        closeLoadingDialog();
        ListDialog dialog = new ListDialog(mActivity, new ListDialog.Callback() {
            @Override
            public void selectFinish(Object obj, int position) {
                BaseDataModel bean = (BaseDataModel) obj;
                if(code == -89){
                    if(weekCode != bean.getId()){
                        //切换周期的时候清空列表数据
                        scanFinishList.clear();
                        dataArrayList.clear();
                        refreshHrecycler();
                    }
                    weekName = bean.getName();
                    weekCode = bean.getId();
                    selectWeekTv.setText(bean.getName());
                    if(qr_deviceSNEt.getTextHint() != null && !qr_deviceSNEt.getTextHint().equals("")){
                        showLoadingDialog();
                        presenter.requestDeviceProjects("FormMachine",qr_deviceSNEt.getTextHint(),weekCode);
                    }
                }
            }
        });
        dialog.show(dataList,"选择周期" , "name");
        dialog.setGravityBottom();
    }

    @Override
    public void requestFail(int stautsCode, int code, String msg) {
        closeLoadingDialog();
        ToastUtil.show(mActivity,msg,false);
    }

    @Override
    public void qrcodeAnalysisSuccess(JSONObject object) {
        closeLoadingDialog();
        try {
            qr_deviceSNEt.setTextHint(object.getString("machine"));
            deviceNameTv.setText(object.getString("name"));
            if(weekCode != null && !weekCode.equals("")){
                showLoadingDialog();
                presenter.requestDeviceProjects("FormMachine",qr_deviceSNEt.getTextHint(),weekCode);
            }
        }catch (JSONException e){

        }
    }

    @Override
    public void getProjectsSuccess(List<DeviceCheckBean> lists) {
        closeLoadingDialog();
        scanFinishList.clear();
        dataArrayList.clear();
        scanFinishList.addAll(lists);
        for(int i = 0 ; i < lists.size() ; i++){
            DeviceCheckBean mModel = lists.get(i);
            ArrayList<String> itemData = new ArrayList<>();
            itemData.clear();
            itemData.add(mModel.getItemValue());//记录
            itemData.add(mModel.getItemName());//项目
            itemData.add(mModel.getsNo() + "");//排序
            itemData.add(mModel.getSpecValue());//空间
            itemData.add(String.valueOf(mModel.getMin()));//下限
            itemData.add(String.valueOf(mModel.getMax()));//上限
            itemData.add(mModel.getRemark());//备注
            dataArrayList.add(new HRecycleViewData(itemData));
        }
        refreshHrecycler();
    }

    @Override
    public void saveQrCodeSuccess(String msg) {
        closeLoadingDialog();
        ToastUtil.show(mActivity,msg,false);
        clearView(false);
    }

    @Override
    public void changed(int etId, String result) {

    }

    @Override
    public void confirm(int etId, String result) {
      if(etId == 1){
          //条码解析
          qr_deviceSNEt.setTextCt("",false);
          //设置焦点
          new Handler().postDelayed(new Runnable() {
              @Override
              public void run() {
                  qr_deviceSNEt.requestEditFoucs();
              }
          }, 300);
          showLoadingDialog();
          presenter.requestAnalysisQRCode("FormMachine",result);
      }
    }

    @Override
    public void clear(int etId) {

    }

    @Override
    public void scan(int etId) {
        if(etId == 1){
            QrManager.getInstance().openCamera(mActivity, CODE_SCAN_QRCODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        QrManager.getInstance().onActivityResult(mActivity, requestCode, resultCode, data, new QrManager.Callback() {
            @Override
            public void resultSuccess(String barcode) {
                 if(QrManager.getInstance().getRequestCode() == CODE_SCAN_QRCODE){
                    confirm(1, barcode);
                }
            }

            @Override
            public void resultFail(String msg) {
                ToastUtil.show(mActivity, msg, false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FinishView();
    }

    @OnClick({R.id.selectWeekId,R.id.tv_clearing,R.id.tv_save})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.selectWeekId:{
                showLoadingDialog();
                presenter.requestBaseInfo("FormPur",-89);
            }
            break;
            case R.id.tv_clearing:{
                //清空 用于清除主表和细表内容，清除暂存，界面恢复到更进入界面的状态。
                if(!DoubleClickU.isFastDoubleClick(R.id.tv_clearing)){
                    clearView(true);
                }
            }
            break;
            case R.id.tv_save:{
                //保存
                if(!DoubleClickU.isFastDoubleClick(R.id.tv_save)){
                    if(scanFinishList.size() == 0){
                        ToastUtil.show(mActivity,"暂无数据，保存无效！",true);
                        return;
                    }
                    if(qr_deviceSNEt.getTextHint() == null || qr_deviceSNEt.getTextHint().equals("")){
                        ToastUtil.show(mActivity,"请扫描设备编号!",true);
                        return;
                    }
                    if(weekCode == null || weekCode.equals("")){
                        ToastUtil.show(mActivity,"请先选择周期!",true);
                        return;
                    }
                    //采购单号
                    JSONObject joData = new JSONObject();
                    try {
                        joData.put("Machine" , qr_deviceSNEt.getTextHint());
                        joData.put("ItemType", weekCode);
                        joData.put("Dtls" ,  new JSONArray(new Gson().toJson(scanFinishList)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    showLoadingDialog();
                    presenter.requestSaveScanResulte("FormMachine",joData);
                }
            }
            break;
        }
    }

    //退出界面前的逻辑
    private void FinishView() {
        finish();
    }
}
