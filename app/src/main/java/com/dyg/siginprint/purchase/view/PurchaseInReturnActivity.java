package com.dyg.siginprint.purchase.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.dingyg.qrcodelib.common.QrManager;
import com.dyg.siginprint.HRecycleView.HRecyclerView;
import com.dyg.siginprint.HRecycleView.adapter.HRecycleViewAdapter;
import com.dyg.siginprint.HRecycleView.data.HRecycleViewData;
import com.dyg.siginprint.R;
import com.dyg.siginprint.base.model.Tokens;
import com.dyg.siginprint.base.tools.DoubleClickU;
import com.dyg.siginprint.base.tools.HawkKeys;
import com.dyg.siginprint.base.tools.ToastUtil;
import com.dyg.siginprint.base.view.BaseActivity;
import com.dyg.siginprint.login.model.LoginBean;
import com.dyg.siginprint.purchase.model.PuechaseModel;
import com.dyg.siginprint.purchase.model.TemPurchaseBean;
import com.dyg.siginprint.purchase.presenter.IPurchaseView;
import com.dyg.siginprint.purchase.presenter.PurchasePresenter;
import com.dyg.siginprint.update.ActivityUtils;
import com.dyg.siginprint.update.CustomDialog;
import com.dyg.siginprint.wiget.dialog.ListDialog;
import com.dyg.siginprint.wiget.dialog.model.BaseDataModel;
import com.dyg.siginprint.wiget.textview.ClearEditText;
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

public class PurchaseInReturnActivity extends BaseActivity<PurchasePresenter> implements IPurchaseView, ClearEditText.Listener {

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

    //组件
    private TextView SupplierTv;//供应商
    private TextView StorageTv;//仓库
    private ImageView ivCheckImg;//是否退货
    private ClearEditText storageLocationEt;//库位
    private ClearEditText purBillNoEt;//采购单号
    private ClearEditText qrCodeEt;//条码
    private TextView totalBoxTv;//总箱数
    private TextView numSumTv;//数量合计

    private int CODE_SCAN_BILLNO = 100;//采购单
    private int CODE_SCAN_STORAGELocation = 101;//库位
    private int CODE_SCAN_QRCODE = 102;
    //供应商
    private String SupplierCode;
    private String SupplierName;
    //仓库
    private String storageName;
    private String storageCode;

    private Activity mActivity;
    private String userCode;
    boolean ifNeedTemStorage = false;//是否需要提示暂存
    private List<PuechaseModel> scanFinishList = new ArrayList<>();//已扫描解析的条码

    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_purchase_retutn;
    }

    @Override
    protected void initPresenter() {
       presenter = new PurchasePresenter(PurchaseInReturnActivity.this,this);
       initTitle(true, "采购进退货", "", new View.OnClickListener() {
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
            mActivity = PurchaseInReturnActivity.this;
            tv = findViewById(R.id.tv);
            SupplierTv = findViewById(R.id.SupplierTv);
            StorageTv = findViewById(R.id.StorageTv);
            ivCheckImg = findViewById(R.id.iv_check);
            storageLocationEt = findViewById(R.id.storageLocationEt);
            purBillNoEt = findViewById(R.id.purBillNoEt);
            totalBoxTv = findViewById(R.id.totalBoxTv);
            numSumTv = findViewById(R.id.totalBoxTv);
            qrCodeEt = findViewById(R.id.qr_codeEt);
            storageLocationEt.initEditText(0, "", 0, 1, true, this);
            purBillNoEt.initEditText(0, "", 0, 2, true, this);
            qrCodeEt.initEditText(0, "", 0, 3, true, this);

            //初始化表格
            jit_hrecyclerview = findViewById(R.id.jit_hrecyclerview);

            initHeadList();

            initHR();

            jit_hrecyclerview.setHeaderListData(mHeadTitleLst);

            jit_hrecyclerview.setViewWidth(maxWidth);

            adapter = new HRecycleViewAdapter(R.layout.item_hrecycleview, dataArrayList, mMoveViewListID, maxWidth);

            jit_hrecyclerview.setAdapter(adapter);

            //需要在表格初始化之后，使用缓存数据
            //取缓存数据
            storageCode = Hawk.get(userCode + HawkKeys.HAWK_pur_storageCode , null);
            storageName = Hawk.get(userCode + HawkKeys.HAWK_pur_storageName , null);
            StorageTv.setText(storageName);
            TemPurchaseBean temPurchaseBean = Hawk.get(userCode + HawkKeys.HAWK_pur_temBean);
            if(temPurchaseBean != null){
                SupplierName = temPurchaseBean.getSupplierName();
                SupplierCode = temPurchaseBean.getSupplierCode();
                storageLocationEt.setTextCt(temPurchaseBean.getLoc(),true);
                totalBoxTv.setText(temPurchaseBean.getScanFinishList().size() + "");
                SupplierTv.setText(temPurchaseBean.getSupplierName());
                purBillNoEt.setTextCt(temPurchaseBean.getSourceNo(),false);
                checkChange(temPurchaseBean.getSalesReturn() == 1 ? 0 : 1);
                for(int i = temPurchaseBean.getScanFinishList().size() - 1 ; i >= 0  ;i--){
                    scanSuccessAddList(temPurchaseBean.getScanFinishList().get(i));
                }
                refreshHrecycler();
            }
        }
    }



    private void initHR(){
        //3.1 获取标题栏宽度
        headWidthList = getHeadWidthList(tv, mHeadTitleLst);
        //3.2 获取每列文本的最大宽度
        //这里不采用计算的高度，直接写死
       // itemWidthList = getItemWidthList(tv, dataArrayList);
        itemWidthList = new ArrayList<>(Arrays.asList(300,150,100,300,100,100,100,100,400,400));
        //3.3 得出每列的最大宽度
        maxWidth = getMaxWidthList(headWidthList, itemWidthList);
        //3.4 获取控件设定的最大宽度
        setMaxWidth = getSetMaxWidthList(PurchaseInReturnActivity.this, R.layout.item_hrecycleview, mMoveViewListID);
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
        mMoveViewListID.add(R.id.item6);
        mMoveViewListID.add(R.id.item7);
        mMoveViewListID.add(R.id.item8);

        dataArrayList.clear();
        //设置表头数据
        mHeadTitleLst.add("料号");
        mHeadTitleLst.add("数量");
        mHeadTitleLst.add("单位");
        mHeadTitleLst.add("批号");
        mHeadTitleLst.add("宽度");
        mHeadTitleLst.add("容量");
        mHeadTitleLst.add("仓库");
        mHeadTitleLst.add("库位");
        mHeadTitleLst.add("备注");
        mHeadTitleLst.add("流水号");
    }

    //二维码解析成功之后逻辑处理
    private void scanSuccessAddList(PuechaseModel mModel){
        //新扫的放在最前面
        scanFinishList.add(0,mModel);
        ArrayList<String> itemData = new ArrayList<>();
        itemData.clear();
        itemData.add(mModel.getPn());//料号
        itemData.add(String.valueOf(mModel.getQty()));//数量
        itemData.add(mModel.getUnit());//单位
        itemData.add(mModel.getLotNo());//批号
        itemData.add(String.valueOf(mModel.getWidth()));//宽度
        itemData.add(String.valueOf(mModel.getCap()));//容量
        itemData.add(mModel.getStorage());//仓库
        itemData.add(mModel.getLoc());//库位
        itemData.add(mModel.getRemark());//备注
        itemData.add(mModel.getSn());//流水号
        dataArrayList.add(0,new HRecycleViewData(itemData));
    }

    //刷新细表和数量
    private void refreshHrecycler(){
        adapter.setData(dataArrayList);
        //计算总数量
        numSumTv.setText(clcaQtyCount());
        totalBoxTv.setText(scanFinishList.size() + "");
    }

    //计算总数量
    private String clcaQtyCount(){
        double qty = 0;
        for(int i = 0 ; i < scanFinishList.size() ; i++){
            qty = qty + scanFinishList.get(i).getQty();
        }
        return qty == 0 ? "" : String.format("%.4f",qty);
    }

    //清空界面 clearDefuatCache 是否需要清理默认缓存，这里的仓库是默认最后一次的结果
    private void clearView(boolean clearDefuatCache){
        SupplierCode = "";
        SupplierName = "";
        SupplierTv.setText("");
        checkChange(1);
        storageLocationEt.setTextCt("",false);
        scanFinishList.clear();
        dataArrayList.clear();
        if(clearDefuatCache){
            storageName = "";
            storageCode = "";
            Hawk.delete(userCode + HawkKeys.HAWK_pur_storageName);
            Hawk.delete(userCode + HawkKeys.HAWK_pur_storageCode);
            StorageTv.setText("");
        }
        Hawk.delete(userCode + HawkKeys.HAWK_pur_temBean);

        refreshHrecycler();
    }

    /**
     * @param currentCheckCode 当前选中
     */
    private void checkChange(int currentCheckCode) {
        if(currentCheckCode == 1){
            ivCheckImg.setTag(0);
            ivCheckImg.setImageResource(R.mipmap.unchecked);
        }else {
            ivCheckImg.setTag(1);
            ivCheckImg.setImageResource(R.mipmap.checked);
        }
    }

    private int getCheckCode() {
        int checkedCode = 0;
        try {
            checkedCode = (int) ivCheckImg.getTag();
        }catch (Exception e){}
        return checkedCode;
    }

    @OnClick({R.id.providerLayoutId,R.id.StorageLayout,R.id.ll_check,R.id.tv_temporary_storage,R.id.tv_clearing,R.id.tv_save,R.id.tv_deleteRow})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.providerLayoutId:{
//                //供应商
                showLoadingDialog();
                presenter.requestBaseInfo("FormPur",2);
            }
            break;
            case R.id.StorageLayout:{
                //仓库
                showLoadingDialog();
                presenter.requestBaseInfo("FormPur",-23);
            }
            break;
            case R.id.ll_check:{
                checkChange(getCheckCode());
            }
            break;
            case R.id.tv_temporary_storage:{
                //暂存
                if(scanFinishList.size() == 0){
                    ToastUtil.show(mActivity,"暂无数据，暂存无效！",true);
                    return;
                }
                showLoadingDialog();
                TemPurchaseBean bean = new TemPurchaseBean();
                bean.setSupplierName(SupplierName);
                bean.setSupplierCode(SupplierCode);
                bean.setSalesReturn(getCheckCode());
                bean.setLoc(storageLocationEt.getTextCt());
                bean.setScanFinishList(scanFinishList);
                bean.setSourceNo(purBillNoEt.getTextCt());
                Hawk.put(userCode + HawkKeys.HAWK_pur_temBean , bean);
                ifNeedTemStorage = false;
                ToastUtil.showToast(mActivity, "已暂存！");
                closeLoadingDialog();
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
                    //采购单号
                    JSONObject joData = new JSONObject();
                    try {
                        joData.put("isReturn", getCheckCode());
                        joData.put("loc" , storageLocationEt.getTextCt());
                        joData.put("supplierCode" , SupplierCode);
                        joData.put("storage" , storageCode);
                        joData.put("sourceNo", purBillNoEt.getTextCt());
                        joData.put("Labels" ,  new JSONArray(new Gson().toJson(scanFinishList)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    showLoadingDialog();
                    presenter.requestScanResulte( getCheckCode() == 1 ? "FormPurReturn" : "FormPur",joData);
                }
            }
            break;
            case R.id.tv_deleteRow : {
                if(scanFinishList.size() > 0){
                    scanFinishList.remove(0);
                    dataArrayList.remove(0);
                    refreshHrecycler();
                }
            }
            break;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FinishView();
    }

    /*
    * TextEditListener
    * */
    @Override
    public void changed(int etId, String result) {

    }

    @Override
    public void confirm(int etId, String result) {
        if(etId == 1){
            //库位
            storageLocationEt.setTextCt(result,false);
        }else if(etId == 2){
            purBillNoEt.setTextCt(result,false);
        }else if(etId == 3){
            //条码解析
            qrCodeEt.setTextCt("",false);
            //设置焦点
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    qrCodeEt.requestEditFoucs();
                }
            }, 300);
            //供应商和仓库必填
            if(SupplierCode == null || SupplierCode.equals("") || storageCode == null || storageCode.equals("")){
                ToastUtil.show(mActivity,"请先选择供应商和仓库",true);
                return;
            }
            //采购单号
            JSONObject joData = new JSONObject();
            try {
                joData.put("qrCode", result);
                joData.put("isReturn", getCheckCode());
                joData.put("loc" , storageLocationEt.getTextCt());
                joData.put("supplierCode" , SupplierCode);
                joData.put("storage" , storageCode);
                joData.put("sourceNo", purBillNoEt.getTextCt());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            showLoadingDialog();
            presenter.requestAnalysisQRCode( getCheckCode() == 1 ? "FormPurReturn" :"FormPur",joData);
        }
    }

    @Override
    public void clear(int etId) {

    }

    @Override
    public void scan(int etId) {
        if(etId == 1){
            //仓位
            QrManager.getInstance().openCamera(mActivity, CODE_SCAN_STORAGELocation);
        }else if(etId == 2){
            //采购单号
            QrManager.getInstance().openCamera(mActivity, CODE_SCAN_BILLNO);
        }else if(etId == 3){
            //供应商和仓库必填
            if(SupplierCode == null || SupplierCode.equals("") || storageCode == null || storageCode.equals("")){
                ToastUtil.show(mActivity,"请先选择供应商和仓库",true);
                return;
            }
            QrManager.getInstance().openCamera(mActivity, CODE_SCAN_QRCODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            QrManager.getInstance().onActivityResult(mActivity, requestCode, resultCode, data, new QrManager.Callback() {
                @Override
                public void resultSuccess(String barcode) {
                    if(QrManager.getInstance().getRequestCode() == CODE_SCAN_BILLNO) {
                        confirm(2, barcode);
                    }else if(QrManager.getInstance().getRequestCode() == CODE_SCAN_STORAGELocation){
                        confirm(1,barcode);
                    }else if(QrManager.getInstance().getRequestCode() == CODE_SCAN_QRCODE){
                        confirm(3, barcode);
                    }
                }

                @Override
                public void resultFail(String msg) {
                    ToastUtil.show(mActivity, msg, false);
                }
            });
    }

    /*
    *
    * */
    @Override
    public void baseInfoSuccess(List list , final int code) {
        closeLoadingDialog();
        ListDialog dialog = new ListDialog(mActivity, new ListDialog.Callback() {
            @Override
            public void selectFinish(Object obj, int position) {
                BaseDataModel bean = (BaseDataModel) obj;
                if(code == 2){
                    SupplierName = bean.getName();
                    SupplierCode = bean.getCode();
                    SupplierTv.setText(bean.getName());
                }else if(code == -23){
                    storageName = bean.getName();
                    storageCode = bean.getCode();
                    StorageTv.setText(bean.getName());
                    //仓库缓存
                    Hawk.put(userCode + HawkKeys.HAWK_pur_storageCode,storageCode);
                    Hawk.put(userCode + HawkKeys.HAWK_pur_storageName,storageName);
                }
            }
        });
        dialog.show(list,code == 2 ? "选择供应商" : code == -23 ? "选择仓库" : "选择" , "name");
        dialog.setGravityBottom();
    }

    @Override
    public void qrcodeAnalysisSuccess(PuechaseModel qrModel) {
        closeLoadingDialog();
        //判断流水号是否有重复，重复丢弃
        for(int i = 0 ; i < scanFinishList.size() ; i++){
            if(qrModel.getSn().equals(scanFinishList.get(i).getSn())){
                ToastUtil.show(mActivity,"流水号重复，流水号为[" + qrModel.getSn() + "]",true);
                qrCodeEt.setTextCt("",true);
                return;
            }
        }
        ifNeedTemStorage = true;
        scanSuccessAddList(qrModel);
        refreshHrecycler();
    }

    @Override
    public void saveQrCodeSuccess(String msg) {
        closeLoadingDialog();
        ToastUtil.show(mActivity,msg,false);
        clearView(false);
    }

    @Override
    public void requestFail(int stautsCode, int code, String msg) {
        closeLoadingDialog();
        ToastUtil.show(mActivity,msg,false);
    }

    Dialog backDialog = null;
    //退出界面前的逻辑
    private void FinishView() {
        if (scanFinishList.size() > 0 && ifNeedTemStorage) {
            if (ActivityUtils.isRunning(mActivity)) {
                backDialog = CustomDialog.createCancleDialog(mActivity, "扫码结果未暂存，确定退出吗？", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        backDialog.dismiss();
                        finish();
                    }
                });
                backDialog.show();
            }
        }else {
            finish();
        }
    }
}
