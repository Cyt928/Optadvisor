package utf8.optadvisor.util;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import utf8.optadvisor.R;
import utf8.optadvisor.domain.AllocationResponse;
import utf8.optadvisor.domain.response.ResponseMsg;
import utf8.optadvisor.fragment.AllocationSetting;

public class AllocationSettingPage extends LinearLayout {
    ImageButton ib11;
    ImageButton ib12;
    ImageButton ib13;
    ImageButton ib21;
    ImageButton ib22;
    ImageButton ib23;
    ImageButton ib31;
    ImageButton ib32;
    ImageButton ib33;
    Spinner time;
    LinearLayout linearLayout;
    EditText principle;
    EditText maxLoss;
    AllocationSettingSeekbar price;
    AllocationSettingSeekbar wave;
    private AlertDialog.Builder dialog;
    private ProgressDialog progressDialog;

    private char combination;
    private String date;
    private String m0;
    private String k;
    private String p1;
    private String p2;
    private String sigma1;
    private String sigma2;

    private AllocationSetting allocationSetting;
    private AllocationResponse responseAllocation;

    private static final int INFO_SUCCESS = 0;
    private static final int INFO_FAILURE = 1;
    private static final int MONTH_SUCCESS =2;
    private static final int MONTH_FAILURE=3;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage (Message msg) {//此方法在ui线程运行
            switch (msg.what) {
                case INFO_SUCCESS:
                    String info = (String) msg.obj;
                    AllocationSettingPage.this.responseAllocation=new Gson().fromJson(info,AllocationResponse.class);
                    System.out.println(info.substring(info.indexOf("profit2Probability")));
                    allocationSetting.setView(responseAllocation);
                    break;
                case INFO_FAILURE:
                    System.out.println("1fail");
                    break;
                case MONTH_SUCCESS:
                    String str = ((String) msg.obj).replace("\"","");
                    System.out.println(str);
                    String month=str.substring(str.indexOf("contractMonth")+15,str.lastIndexOf("]"));
                    System.out.println(month);
                    List<String> array=new ArrayList<>();
                    String[] monthlist=month.split(",");
                    for (String s:monthlist){
                        if (array.isEmpty()) {
                            array.add(s);
                            continue;
                        }
                        if (!array.contains(s))
                            array.add(s);
                    }
                    if (isInFive()){
                        array.remove(0);
                    }
                    time=(Spinner)findViewById(R.id.allocation_spr_validtime);
                    SpinnerAdapter adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_dropdown_item_1line,array);
                    time.setAdapter(adapter);
                    break;
                case MONTH_FAILURE:
                    System.out.println("2fail");
                    break;
            }
        }
    };

    public AllocationSettingPage(final Context context,final AllocationSetting allocationSetting) {
        super(context);
        inflate(context, R.layout.linearlayout_allocation_setting, this);
        this.allocationSetting=allocationSetting;

        initDialog();

        initProgessDialog();

        price=new AllocationSettingSeekbar(context,true,true,allocationSetting);
        wave=new AllocationSettingSeekbar(context,false,true,allocationSetting);

        ib11=(ImageButton)findViewById(R.id.allocation_ib_11);
        ib12=(ImageButton)findViewById(R.id.allocation_ib_12);
        ib13=(ImageButton)findViewById(R.id.allocation_ib_13);
        ib21=(ImageButton)findViewById(R.id.allocation_ib_21);
        ib22=(ImageButton)findViewById(R.id.allocation_ib_22);
        ib23=(ImageButton)findViewById(R.id.allocation_ib_23);
        ib31=(ImageButton)findViewById(R.id.allocation_ib_31);
        ib32=(ImageButton)findViewById(R.id.allocation_ib_32);
        ib33=(ImageButton)findViewById(R.id.allocation_ib_33);
        principle=(EditText)findViewById(R.id.allocation_et_principle);
        maxLoss=(EditText)findViewById(R.id.allocation_et_maxloss);
        linearLayout=(LinearLayout)findViewById(R.id.allocation_setting_ll);

        ib11.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ib11.setActivated(true);
                ib12.setActivated(false);
                ib13.setActivated(false);
                ib21.setActivated(false);
                ib22.setActivated(false);
                ib23.setActivated(false);
                ib31.setActivated(false);
                ib32.setActivated(false);
                ib33.setActivated(false);
                linearLayout.removeAllViews();
                price=new AllocationSettingSeekbar(context,true,true,allocationSetting);
                linearLayout.addView(price);
                wave=new AllocationSettingSeekbar(context,false,true,allocationSetting);
                linearLayout.addView(wave);
                combination='A';
            }
        });

        ib12.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                ib11.setActivated(false);
                ib12.setActivated(true);
                ib13.setActivated(false);
                ib21.setActivated(false);
                ib22.setActivated(false);
                ib23.setActivated(false);
                ib31.setActivated(false);
                ib32.setActivated(false);
                ib33.setActivated(false);
                linearLayout.removeAllViews();
                wave=new AllocationSettingSeekbar(context,false,true,allocationSetting);
                linearLayout.addView(wave);
                combination='B';
            }
        });

        ib13.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                ib11.setActivated(false);
                ib12.setActivated(false);
                ib13.setActivated(true);
                ib21.setActivated(false);
                ib22.setActivated(false);
                ib23.setActivated(false);
                ib31.setActivated(false);
                ib32.setActivated(false);
                ib33.setActivated(false);
                linearLayout.removeAllViews();
                price=new AllocationSettingSeekbar(context,true,false,allocationSetting);
                linearLayout.addView(price);
                wave=new AllocationSettingSeekbar(context,false,true,allocationSetting);
                linearLayout.addView(wave);
                combination='C';
            }
        });

        ib21.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                ib11.setActivated(false);
                ib21.setActivated(true);
                ib13.setActivated(false);
                ib12.setActivated(false);
                ib22.setActivated(false);
                ib23.setActivated(false);
                ib31.setActivated(false);
                ib32.setActivated(false);
                ib33.setActivated(false);
                linearLayout.removeAllViews();
                price=new AllocationSettingSeekbar(context,true,true,allocationSetting);
                linearLayout.addView(price);
                combination='D';
            }
        });

        ib22.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                ib11.setActivated(false);
                ib13.setActivated(false);
                ib21.setActivated(false);
                ib12.setActivated(false);
                ib23.setActivated(false);
                ib31.setActivated(false);
                ib32.setActivated(false);
                ib33.setActivated(false);
                Toast.makeText(context,"此按钮不可选",Toast.LENGTH_SHORT).show();
            }
        });

        ib23.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                ib11.setActivated(false);
                ib23.setActivated(true);
                ib13.setActivated(false);
                ib21.setActivated(false);
                ib22.setActivated(false);
                ib12.setActivated(false);
                ib31.setActivated(false);
                ib32.setActivated(false);
                ib33.setActivated(false);
                linearLayout.removeAllViews();
                price=new AllocationSettingSeekbar(context,true,false,allocationSetting);
                linearLayout.addView(price);
                combination='E';
            }
        });

        ib31.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                ib11.setActivated(false);
                ib31.setActivated(true);
                ib13.setActivated(false);
                ib21.setActivated(false);
                ib22.setActivated(false);
                ib23.setActivated(false);
                ib12.setActivated(false);
                ib32.setActivated(false);
                ib33.setActivated(false);
                linearLayout.removeAllViews();
                price=new AllocationSettingSeekbar(context,true,true,allocationSetting);
                linearLayout.addView(price);
                wave=new AllocationSettingSeekbar(context,false,false,allocationSetting);
                linearLayout.addView(wave);
                combination='F';
            }
        });

        ib32.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                ib11.setActivated(false);
                ib32.setActivated(true);
                ib13.setActivated(false);
                ib21.setActivated(false);
                ib22.setActivated(false);
                ib23.setActivated(false);
                ib31.setActivated(false);
                ib12.setActivated(false);
                ib33.setActivated(false);
                linearLayout.removeAllViews();
                wave=new AllocationSettingSeekbar(context,false,false,allocationSetting);
                linearLayout.addView(wave);
                combination='G';
            }
        });

        ib33.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                ib11.setActivated(false);
                ib33.setActivated(true);
                ib13.setActivated(false);
                ib21.setActivated(false);
                ib22.setActivated(false);
                ib23.setActivated(false);
                ib31.setActivated(false);
                ib32.setActivated(false);
                ib12.setActivated(false);
                linearLayout.removeAllViews();
                price=new AllocationSettingSeekbar(context,true,false,allocationSetting);
                linearLayout.addView(price);
                wave=new AllocationSettingSeekbar(context,false,false,allocationSetting);
                linearLayout.addView(wave);
                combination='H';
            }
        });

        getMonth();

        Button bt=(Button)findViewById(R.id.allocation_setting_next);
        bt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (combination<='H'&&combination>='A' && !TextUtils.isEmpty(principle.getText()) && !TextUtils.isEmpty(maxLoss.getText())) {
                    if (Double.parseDouble(maxLoss.getText().toString())>=100){
                        dialog.setTitle("信息填写错误");
                        dialog.setMessage("最大允许损失需小于100");
                        dialogShow();
                    }else {
                        progressDialog.show();
                        Map<String, String> values = new HashMap<>();
                        values.put("m0", getM0());
                        values.put("k", getK());
                        values.put("t", getDate());
                        values.put("combination", getCombination() + "");
                        values.put("p1", getP1());
                        values.put("p2", getP2());
                        values.put("sigma1", getSigma1());
                        values.put("sigma2", getSigma2());
//

                        //
                        NetUtil.INSTANCE.sendPostRequest(NetUtil.SERVER_BASE_ADDRESS + "/recommend/recommendPortfolio", values, getContext(), new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                dialog.setTitle("网络连接错误");
                                dialog.setMessage("请稍后再试");
                                dialogShow();
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                progressDialog.dismiss();
                                ResponseMsg responseMsg = NetUtil.INSTANCE.parseJSONWithGSON(response);
                                if (responseMsg.getData() == null ) {
                                    dialog.setTitle("数据返回错误");
                                    dialog.setMessage("请稍后再试");
                                    dialogShow();
                                } else if(responseMsg.getCode() == 1008){
                                    dialog.setTitle("用户未登录");
                                    dialog.setMessage("请先登录");
                                    dialogShow();
                                } else{
                                    mHandler.obtainMessage(INFO_SUCCESS, responseMsg.getData().toString()).sendToTarget();
                                }
                            }
                        });
                    }
                }
                else{
                    dialog.setTitle("信息未填写完善");
                    dialog.setMessage("请重新填写");
                    dialogShow();
                }
            }
        });

    }

    private boolean isInFive() {
        Calendar now=Calendar.getInstance();
        int year=now.get(Calendar.YEAR);
        int month=now.get(Calendar.MONTH)+1;
        int day=now.get(Calendar.DAY_OF_MONTH);
        int weekDay=0;
        Calendar week=Calendar.getInstance();
        week.set(year,month-1,1);
        weekDay=week.get(Calendar.DAY_OF_WEEK);
        int theFourthWeek=weekDay<=3?24-weekDay:31-weekDay;
        return (day>=theFourthWeek-4)&&(day<=theFourthWeek+1);
    }

    private void getMonth(){
        NetUtil.INSTANCE.sendGetRequest("http://stock.finance.sina.com.cn/futures/api/openapi.php/StockOptionService.getStockName", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.obtainMessage(MONTH_FAILURE).sendToTarget();
                dialog.setTitle("网络连接错误");
                dialog.setMessage("请稍后再试");
                dialogShow();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mHandler.obtainMessage(MONTH_SUCCESS,response.body().string()).sendToTarget();
            }
        });
    }

    public char getCombination() {
        return combination;
    }

    public String getDate() {
        return time.getSelectedItem().toString();
    }

    public String getM0() {
        return principle.getText().toString();
    }

    public String getK() {
        return maxLoss.getText().toString();
    }

    public String getP1() {
        switch (combination) {
            case 'A' :
                p1=price.getMin()+"";
                break;
            case 'B':
                p1=wave.getETF()+"";
                break;
            case 'C':
                p1=price.getMin()+"";
                break;
            case 'D' :
                p1=price.getMin()+"";
                break;
            case 'E':
                p1=price.getMin()+"";
                break;
            case 'F':
                p1=price.getMin()+"";
                break;
            case 'G' :
                p1=wave.getETF()+"";
                break;
            case 'H':
                p1=price.getMin()+"";
                break;

        }

        return p1;
    }

    public String getP2() {
        switch (combination) {
            case 'A' :
                p2=price.getMax()+"";
                break;
            case 'B':
                p2=wave.getETF()+"";
                break;
            case 'C':
                p2=price.getMax()+"";
                break;
            case 'D' :
                p2=price.getMax()+"";
                break;
            case 'E':
                p2=price.getMax()+"";
                break;
            case 'F':
                p2=price.getMax()+"";
                break;
            case 'G' :
                p2=wave.getETF()+"";
                break;
            case 'H':
                p2=price.getMax()+"";
                break;

        }
        return p2;
    }

    public String getSigma1() {
        switch (combination) {
            case 'A' :
                sigma1=wave.getMin()+"";
                break;
            case 'B':
                sigma1=wave.getMin()+"";
                break;
            case 'C':
                sigma1=wave.getMin()+"";
                break;
            case 'D' :
                sigma1=price.getSigma()+"";
                break;
            case 'E':
                sigma1=price.getSigma()+"";;
                break;
            case 'F':
                sigma1=wave.getMin()+"";
                break;
            case 'G' :
                sigma1=wave.getMin()+"";
                break;
            case 'H':
                sigma1=wave.getMin()+"";
                break;

        }
        return sigma1;
    }

    public String getSigma2() {
        switch (combination) {
            case 'A' :
                sigma2=wave.getMax()+"";
                break;
            case 'B':
                sigma2=wave.getMax()+"";
                break;
            case 'C':
                sigma2=wave.getMax()+"";
                break;
            case 'D' :
                sigma2=price.getSigma()+"";
                break;
            case 'E':
                sigma2=price.getSigma()+"";;
                break;
            case 'F':
                sigma2=wave.getMax()+"";
                break;
            case 'G' :
                sigma2=wave.getMax()+"";
                break;
            case 'H':
                sigma2=wave.getMax()+"";
                break;

        }
        return sigma2;
    }

    private void initDialog(){
        dialog=new AlertDialog.Builder(allocationSetting.getActivity());
        dialog.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
    }
    private void dialogShow(){
        allocationSetting.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });
    }

    /**
     * 设置进度条
     */
    private void initProgessDialog(){
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setTitle("请稍等");
        progressDialog.setMessage("请求发送中");
    }
}
