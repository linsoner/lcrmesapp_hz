package com.dyg.siginprint.baogong;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.OnClick;

import static com.dyg.siginprint.HRecycleView.utils.utils.getHeadWidthList;
import static com.dyg.siginprint.HRecycleView.utils.utils.getIdealWidthList;
import static com.dyg.siginprint.HRecycleView.utils.utils.getMaxWidthList;
import static com.dyg.siginprint.HRecycleView.utils.utils.getSetMaxWidthList;

public class BaogongActivity extends BaseActivity<PurchasePresenter> implements IPurchaseView, ClearEditText.Listener {

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
    // private TextView billTypeTv;//来源单类型
    private ClearEditText sourceNoEt;//来源单号
    //private ClearEditText qrCodeEt;//条码

    private int CODE_SCAN_SOURCENO = 101;//来源单号
    private int CODE_SCAN_QRCODE = 102;//二维码

    private Activity mActivity;
    private String userCode;
    boolean ifNeedTemStorage = false;//是否需要提示暂存
    private List<PuechaseModel> scanFinishList = new ArrayList<>();//已扫描解析的条码

    private String billType;
    private String billTypeName;

    private ClearEditText goodQtyEt;//良品数
    private ClearEditText badQtyEt;//不良数
    private ClearEditText foilLengthEt;//正箔实际长度
    private ClearEditText dateCodeEt;//周期
    private ClearEditText timesEt;//周期

    private TextView procTv;//工序
    private String proc;
    private TextView workerTv;//作业员
    private String worker;
    private TextView qcTv;//机修
    private String qc;
    private TextView machanicTv;//品管
    private String machanic;
    private TextView machineTv;//机台
    private String machine;

    private TextView badReason1Tv;//不良项1
    private TextView badReason2Tv;//不良项2
    private TextView badReason3Tv;//不良项3
    private ClearEditText badQty1Et;//不良数1
    private ClearEditText badQty2Et;//不良数2
    private ClearEditText badQty3Et;//不良数3

    private EditText remarkEt;

    private String procCode; //工序代号
    private  String P030 = "P030"; //组立工序代号
    private  String P090 = "P090"; //套管工序代号
    private  String P110 = "P110"; //分选工序代号
    private  String P101 = "P101"; //二次分选工序代号
    private  String P050 = "P050"; //老化工序代号

    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_baogong;
    }

    @Override
    protected void initPresenter() {
        presenter = new PurchasePresenter(BaogongActivity.this,this);
        initTitle(true, "报工", "", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FinishView();
            }
        },null);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ToastUtil.show(mActivity, "焦点变化:QQQQ" , true);
        LoginBean loginBean = Hawk.get(Tokens.LoginBean, null);
        if (loginBean == null) finish();
        else {
            userCode = loginBean.getAccount();
            mActivity = BaogongActivity.this;
            tv = findViewById(R.id.tv);
            // billTypeTv = findViewById(R.id.billTypeTv);
            sourceNoEt = findViewById(R.id.sourceNoEt);

            // qrCodeEt = findViewById(R.id.qr_codeEt);
            sourceNoEt.initEditText(0, "", 0, 1, true, this);
            // qrCodeEt.initEditText(0, "", 0, 3, true, this);

            goodQtyEt = findViewById(R.id.goodQtyEt);
            goodQtyEt.setInputType(InputType.TYPE_CLASS_NUMBER);
            badQtyEt = findViewById(R.id.badQtyEt);
            badQtyEt.setInputType(InputType.TYPE_CLASS_NUMBER);

            badQty1Et = findViewById(R.id.badQty1Et);
            badQty1Et.initEditText(0, "不良数1", 0, 98,
                    false, this);
            badQty1Et.setInputType(InputType.TYPE_CLASS_NUMBER);
            badQty2Et = findViewById(R.id.badQty2Et);
            badQty2Et.initEditText(0, "不良数2", 0, 99,
                    false, this);
            badQty2Et.setInputType(InputType.TYPE_CLASS_NUMBER);
            badQty3Et = findViewById(R.id.badQty3Et);
            badQty3Et.initEditText(0, "不良数3", 0, 97,
                    false, this);
            badQty3Et.setInputType(InputType.TYPE_CLASS_NUMBER);
            badReason1Tv = findViewById(R.id.badReason1Tv);
            badReason2Tv = findViewById(R.id.badReason2Tv);
            badReason3Tv = findViewById(R.id.badReason3Tv);

            foilLengthEt = findViewById(R.id.foilLengthEt);
            foilLengthEt.setInputType(InputType.TYPE_CLASS_NUMBER);
            dateCodeEt = findViewById(R.id.dateCodeEt);
            dateCodeEt.setInputType(InputType.TYPE_CLASS_NUMBER);

            procTv = findViewById(R.id.procTv);
            workerTv = findViewById(R.id.workerTv);
            qcTv = findViewById(R.id.qcTv);
            machanicTv = findViewById(R.id.machanicTv);
            machineTv = findViewById(R.id.machineTv);
            timesEt = findViewById(R.id.timesEt);
            timesEt.setInputType(InputType.TYPE_CLASS_NUMBER);
            remarkEt = findViewById(R.id.remarkEt);

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
            billType = "M0021";
            billTypeName ="报工";
            // billTypeTv.setText(billTypeName);
        }
    }

    /**
     * 处理不良数添加到备注的逻辑
     * 只有在备注为空时才会自动添加不良数信息，并包含工序信息
     */
    private void handleBadQtyToRemark() {
        // 如果没有选择工序，也不添加
        if (TextUtils.isEmpty(proc)) {
            return;
        }
        String badQtyText = badQtyEt.getTextCt();
        if (TextUtils.isEmpty(badQtyText) || "0".equals(badQtyText)) {
            return;
        }

        try {
            if(proc.equals("钉卷")) {
                remarkEt.setText("露箔，跑纸，高低脚，偏芯，跑胶带，外径大，抽芯");
            }
            else if(proc.equals("组立")) {
                remarkEt.setText("弯脚，封口不良，偏芯，外径大");
            }
            else if(proc.equals("套管")) {
                remarkEt.setText("套管打折，露白，弯脚，裸品高低不一");
            }
            else if(proc.equals("老化")) {
                remarkEt.setText("漏电，重选，未老化，损失，阻抗，低容");
            }
        } catch (Exception e) {
            Log.e("BadQty", "设置备注错误: " + e.getMessage());
        }

        //Log.d("Debug", "=== handleBadQtyToRemark 结束 ===");
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
        setMaxWidth = getSetMaxWidthList(BaogongActivity.this, R.layout.item_hrecycleview, mMoveViewListID);
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
        mHeadTitleLst.add("客户");
        mHeadTitleLst.add("成型方式");
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
        itemData.add(mModel.getCustomerCode());//客户
        itemData.add(mModel.getMakeType());//成型方式
        itemData.add(mModel.getRemark());//备注
        itemData.add(mModel.getSn());//流水号
        dataArrayList.add(0,new HRecycleViewData(itemData));
    }

    //刷新细表和数量
    private void refreshHrecycler(){
        adapter.setData(dataArrayList);

    }

    //计算总数量
    private String clcaQtyCount(){
        return  "";
    }

    //清空界面 clearDefuatCache 是否需要清理默认缓存，这里的仓库是默认最后一次的结果
    private void clearView(boolean clearDefuatCache){
        sourceNoEt.setTextCt("",false);
        // qrCodeEt.setTextCt("",false);
        goodQtyEt.setTextCt("",false);
        badQtyEt.setTextCt("",false);
        dateCodeEt.setTextCt("",false);
        foilLengthEt.setTextCt("",false);
        remarkEt.setText("");
        machineTv.setText("");
        timesEt.setTextCt("1", false);
        scanFinishList.clear();
        dataArrayList.clear();
        if(clearDefuatCache){
            billType = "M0021";
            billTypeName = "报工";
            // billTypeTv.setText(billTypeName);
        }

        refreshHrecycler();
    }

    @OnClick({R.id.tv_clearing,R.id.tv_save,R.id.procLayoutId
    ,R.id.workerLayoutId,R.id.machanicLayoutId,R.id.machineLayoutId,R.id.qcLayoutId
            ,R.id.badReason1LayoutId, R.id.badReason1Tv,
            R.id.badReason2LayoutId, R.id.badReason2Tv,
            R.id.badReason3LayoutId, R.id.badReason3Tv
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.procTv:
            case R.id.procLayoutId:{
                //工序
                showLoadingDialog();
                presenter.requestBaseInfo("FormCheck", -1);
                break;
            }
            case R.id.badReason1LayoutId:
            case R.id.badReason1Tv:{
                //不良项目
                showLoadingDialog();
                presenter.requestBaseInfo("FormCheck", -83);
                break;
            }
            case R.id.badReason2LayoutId:
            case R.id.badReason2Tv:{
                //不良项目
                showLoadingDialog();
                presenter.requestBaseInfo("FormCheck", -82);
                break;
            }
            case R.id.badReason3LayoutId:
            case R.id.badReason3Tv:{
                //不良项目
                showLoadingDialog();
                presenter.requestBaseInfo("FormCheck", -81);
                break;
            }
            case R.id.workerTv:
            case R.id.workerLayoutId:{
                //作业员
                showLoadingDialog();
                presenter.requestBaseInfo("FormCheck", -86);
                break;
            }
            case R.id.machanicTv:
            case R.id.machanicLayoutId:{
                //机修
                showLoadingDialog();
                presenter.requestBaseInfo("FormCheck", -85);
                break;
            }
            case R.id.machineTv:
            case R.id.machineLayoutId:{
                //机台
                showLoadingDialog();
                presenter.requestBaseInfo("FormCheck", -87);
                break;
            }
            case R.id.qcTv:
            case R.id.qcLayoutId:{
                //品管
                showLoadingDialog();
                presenter.requestBaseInfo("FormCheck", -84);
                break;
            }
            case R.id.tv_temporary_storage:{
                //暂存
                break;
            }
            case R.id.tv_clearing:{
                //清空 用于清除主表和细表内容，清除暂存，界面恢复到更进入界面的状态。
                if(!DoubleClickU.isFastDoubleClick(R.id.tv_clearing)){
                    clearView(true);
                }
                break;
            }
            case R.id.tv_save:{
                //保存
                if(!DoubleClickU.isFastDoubleClick(R.id.tv_save)){
                    if(proc.isEmpty() || proc == null){
                        ToastUtil.show(mActivity,"请选择工序!");
                        return;
                    }
                    if(worker.isEmpty() || worker == null){
                        ToastUtil.show(mActivity,"请选择作业员!");
                        return;
                    }

                    if(!"包装".equals(proc) && !"外观".equals(proc) && !"清洗".equals(proc)
                            && (machine == null || machine.isEmpty())){
                        ToastUtil.show(mActivity, proc + "工序需要选择机台!");
                        return;
                    }

                    if(proc.equals("钉卷") &&  (foilLengthEt.getTextCt() == null || foilLengthEt.getTextCt().isEmpty()
                            || foilLengthEt.getTextCt() == "0")){
                        ToastUtil.show(mActivity,"请输入正箔长度!");
                        return;
                    }

                    double foilLength = 0;
                    if(!foilLengthEt.getTextCt().isEmpty() && foilLengthEt.getTextCt() != null){
                        try {
                            foilLength = Double.parseDouble(foilLengthEt.getTextCt());
                        }catch (Exception ex){
                        //ToastUtil.show(mActivity,"正箔长度必须为数值");
                        //return;
                            ex.printStackTrace();
                        }
                    }

                    double badqty = 0;
                    if(!badQtyEt.getTextCt().isEmpty() && badQtyEt.getTextCt() != null){
                        try {
                            badqty = Double.parseDouble(badQtyEt.getTextCt());
                        }catch (Exception ex){
                            //ToastUtil.show(mActivity,"不良数必须为数值");
                            //return;
                            ex.printStackTrace();
                        }
                    }

                    int badqty1 = 0;
                    if(!badQty1Et.getTextCt().isEmpty() && badQty1Et.getTextCt() != null){
                        try {
                            badqty1 = Integer.parseInt(badQty1Et.getTextCt());
                        }catch (Exception ex){
                            //ToastUtil.show(mActivity,"不良数1必须为数值");
                            //return;
                            ex.printStackTrace();
                        }
                    }
                    int badqty2 = 0;
                    if(!badQty2Et.getTextCt().isEmpty() && badQty2Et.getTextCt() != null){
                        try {
                            badqty2 = Integer.parseInt(badQty2Et.getTextCt());
                        }catch (Exception ex){
                            //ToastUtil.show(mActivity,"不良数1必须为数值");
                            //return;
                            ex.printStackTrace();
                        }
                    }
                    int badqty3 = 0;
                    if(!badQty3Et.getTextCt().isEmpty() && badQty3Et.getTextCt() != null){
                        try {
                            badqty3 = Integer.parseInt(badQty3Et.getTextCt());
                        }catch (Exception ex){
                            //ToastUtil.show(mActivity,"不良数1必须为数值");
                            //return;
                            ex.printStackTrace();
                        }
                    }

                    double goodqty = 0;
                    if(!goodQtyEt.getTextCt().isEmpty() && goodQtyEt.getTextCt() != null){
                        try {
                            goodqty = Double.parseDouble(goodQtyEt.getTextCt());
                        }catch (Exception ex){
                            //ToastUtil.show(mActivity,"良品数必须为数值");
                            //return;
                            ex.printStackTrace();
                        }
                    }

                    int times = 1;
                    if(!timesEt.getTextCt().isEmpty() && timesEt.getTextCt() != null){
                        try {
                            times = Integer.parseInt(timesEt.getTextCt());
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                    billType  ="M0021";
                    JSONObject joData = new JSONObject();
                    try {
                        joData.put("goodQty", goodqty);
                        joData.put("badQty", badqty);
                        joData.put("badQty1", badqty1);
                        joData.put("badQty2", badqty2);
                        joData.put("badQty3", badqty3);
                        joData.put("badReason1" , badReason1Tv.getText());
                        joData.put("badReason2" , badReason2Tv.getText());
                        joData.put("badReason3" , badReason3Tv.getText());
                        joData.put("billType", billType);
                        joData.put("sourceNo" , sourceNoEt.getTextCt());
                        joData.put("ProcessCode" , proc);
                        joData.put("machine" , machine);
                        joData.put("worker" , worker);
                        joData.put("qc" , qc);
                        joData.put("machanic" , machanic);
                        joData.put("dateCode" , dateCodeEt.getTextCt());
                        joData.put("foilLength" , foilLength);
                        joData.put("times" , times);
                        joData.put("remark" , remarkEt.getText());
                        joData.put("Labels" ,  new JSONArray(new Gson().toJson(scanFinishList)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    showLoadingDialog();
                    presenter.requestScanResulte("FormCheck",joData);
                }

                break;
            }

            default : break;
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
            //来源单号
            sourceNoEt.setTextCt(result,true);
            //设置焦点
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    goodQtyEt.requestEditFoucs();
                }
            }, 500);
        }else if(etId == 3){
            /*qrCodeEt.setTextCt("",false);
            //设置焦点
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    qrCodeEt.setTextCt("",false);
                    qrCodeEt.requestEditFoucs();
                }
            }, 300);
            //条码解析
            billType = "M0021";
            if(sourceNoEt.getTextCt() == "" || sourceNoEt.getTextCt() == null){
                ToastUtil.show(mActivity,"请输入来源单号!");
                return;
            }
            //采购单号
            JSONObject joData = new JSONObject();
            try {
                joData.put("qrCode", result);
                joData.put("billType", billType);
                joData.put("sourceNo" , sourceNoEt.getTextCt());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            showLoadingDialog();
            presenter.requestAnalysisQRCode("FormCheck",joData);*/
        }
    }

    @Override
    public void clear(int etId) {

    }

    @Override
    public void scan(int etId) {
        if(etId == 1){
            //来源单号
            QrManager.getInstance().openCamera(mActivity, CODE_SCAN_SOURCENO);
        }else if(etId == 3){
            //供应商和仓库必填
            if(billType == "" || billType == null){
                ToastUtil.show(mActivity,"请选择来源单据类型!");
                return;
            }
            if(sourceNoEt.getTextCt() == "" || sourceNoEt.getTextCt() == null){
                ToastUtil.show(mActivity,"请输入来源单号!");
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
                if(QrManager.getInstance().getRequestCode() == CODE_SCAN_SOURCENO) {
                    confirm(1, barcode);
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
                if(code == -99 || code == -98 || code == -93 || code == -92 || code == -91){
                    billTypeName = bean.getName();
                    billType = bean.getCode();
                    // billTypeTv.setText(bean.getName());
                }
                else if(code == -1) {
                    //工序
                    proc = bean.getName();
                    String procCode1 = bean.getCode();
                    if(procCode1 != procCode)
                        clearRelatedFields();
                    procCode = procCode1;
                    procTv.setText(bean.getName());

                    handleBadQtyToRemark();
                }
                else if(code == -87) {
                    //机台
                    machine = bean.getCode();
                    machineTv.setText(bean.getName());
                }
                else if(code == -86) {
                    //作业员
                    worker = bean.getName();
                    workerTv.setText(bean.getName());
                }
                else if(code == -85) {
                    //机修
                    machanic = bean.getName();
                    machanicTv.setText(bean.getName());
                }
                else if(code == -84) {
                    //品管
                    qc = bean.getName();
                    qcTv.setText(bean.getName());
                }
                else if(code == -83) {
                    //不良项目1
                    badReason1Tv.setText(bean.getName());
                }
                else if(code == -82) {
                    //不良项目2
                    badReason2Tv.setText(bean.getName());
                }
                else if(code == -81) {
                    //不良项目3
                    badReason3Tv.setText(bean.getName());
                }
            }
        });

        List<BaseDataModel> ls = list;
        if(code == -87 ||code == -86  ||code == -85 ||code == -84 || code == -83 || code == -82
                || code == -81)
            // 机台 作业员  机修  品管 不良项目2 不良项目2
            ls = filterDataByTypeCode(list, code);
        dialog.show(ls, code == -99 ? "选择来源单据类型" : "" , "name", viewId);
        dialog.setGravityBottom();
    }

    /**
     * 按工序过滤，将符合条件的记录排在前面，不符合条件的排在后面（保留所有数据）
     * @param dataList 原始数据列表
     * @param code 筛选编码
     * @return 重新排序后的列表（符合条件在前，不符合在后）
     */
    private List<BaseDataModel> filterDataByTypeCode(List<BaseDataModel> dataList, int code) {
        // 分离符合条件和不符合条件的列表
        List<BaseDataModel> matchedList = new ArrayList<>();
        List<BaseDataModel> unmatchedList = new ArrayList<>();

        // 空值处理
        if (TextUtils.isEmpty(procCode) || dataList == null || dataList.isEmpty()) {
            return dataList == null ? new ArrayList<>() : dataList;
        }

        for (BaseDataModel model : dataList) {
            String p = model.getTypeCode();
            boolean isMatched = false;
            if(code == -83 || code ==-82 || code ==-81)
            {
                //钉卷
                if(procCode.equals("P010")) {
                    if (p.equals("P17")) {
                        isMatched = true;
                    }
                }
                //组立
                else if(procCode.equals(P030)) {
                    if (p.equals("P20")) {
                        isMatched = true;
                    }
                }
                //套管
                else if(procCode.equals(P090)) {
                    if (p.equals("P21")) {
                        isMatched = true;
                    }
                }
                //老化
                else if(procCode.equals(P050) || procCode.equals(P110)) {
                    if (p.equals("P65")) {
                        isMatched = true;
                    }
                }
            }
            // 分选和老化的作业员，机修，品管是一样的
            else if((procCode.equals(P101) || procCode.equals(P110) || procCode.equals(P050)) &&
                    (code ==-86 || code ==-85 || code ==-84)) {
                if (p.equals(P050) || p.equals(P101) || p.equals(P110)) {
                    isMatched = true;
                }
            }

            // 分选和二次分析的机台一样的
            else if((procCode.equals(P101) || procCode.equals(P110)) && (code ==-87)) {
                if (p.equals(P101) || p.equals(P110)) {
                    isMatched = true;
                }
            }
            // 组立和套管的机修和品管是一样的
            else if((procCode.equals(P030) || procCode.equals(P090)) && (code ==-85 || code ==-84)) {
                if (p.equals(P030) || p.equals(P090)) {
                    isMatched = true;
                }
            }
            // 基础匹配规则
            else {
                if (procCode.equals(p)) {
                    isMatched = true;
                }
            }

            // 根据匹配结果分类
            if (isMatched) {
                matchedList.add(model);
            } else {
                unmatchedList.add(model);
            }
        }

        // 合并列表：符合条件的在前，不符合的在后
        List<BaseDataModel> resultList = new ArrayList<>();
        resultList.addAll(matchedList);
        if(code !=-87 && code != -83 && code != -82 && code != -81)
            resultList.addAll(unmatchedList);

        return resultList;
    }

    private void clearRelatedFields() {
        workerTv.setText("");
        worker = "";
        qcTv.setText("");
        qc = "";
        machanicTv.setText("");
        machanic = "";
        machineTv.setText("");
        machine = "";
    }

    @Override
    public void qrcodeAnalysisSuccess(PuechaseModel qrModel) {
        closeLoadingDialog();
        //判断流水号是否有重复，重复丢弃
        for(int i = 0 ; i < scanFinishList.size() ; i++){
            if(qrModel.getSn().equals(scanFinishList.get(i).getSn())){
                ToastUtil.show(mActivity,"流水号重复，流水号为[" + qrModel.getSn() + "]",true);
                // qrCodeEt.setTextCt("",true);
                return;
            }
        }
        // qrCodeEt.setTextCt("",true);
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
        if(code != 1001 && !msg.contains("保存成功") )
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
