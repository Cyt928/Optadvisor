package utf8.optadvisor.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
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
import utf8.optadvisor.activity.MainActivity;
import utf8.optadvisor.domain.AllocationResponse;
import utf8.optadvisor.domain.HedgingResponse;
import utf8.optadvisor.domain.response.ResponseMsg;
import utf8.optadvisor.util.AllocationSettingPage;
import utf8.optadvisor.util.NetUtil;

public class HedgingInfoSetting extends Fragment {

    private boolean isNull=true;

    private SeekBar seekBar;
    private EditText textView;
    Spinner date;
    private Calendar calendar; // 通过Calendar获取系统时间
    private int mYear;
    private int mMonth;
    private int mDay;
    private Button next;
    private LinearLayout ll;
    private EditText et1;
    private EditText et2;

    private AlertDialog.Builder dialog;

    private static final int INFO_SUCCESS = 0;
    private static final int INFO_FAILURE = 1;

    private String n;
    private String iK;
    private String pAsset;
    private String sExp;

    private ProgressDialog progressDialog;

    private LayoutInflater inflater;
    private ViewGroup viewGroup;
    private Bundle savedInstanceState;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage (Message msg) {//此方法在ui线程运行
            switch (msg.what) {
                case INFO_SUCCESS:
                    String info = (String) msg.obj;
                    HedgingResponse responseOption=new Gson().fromJson(info,HedgingResponse.class);
                    ll.removeAllViews();
                    isNull=false;
                    ll.addView(new HedgingInfoDisplay(getContext(),responseOption,HedgingInfoSetting.this));
                    break;
                case INFO_FAILURE:
                    System.out.println("1fail");
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_hedging_info_setting, container, false);
        this.inflater=inflater;
        this.viewGroup=container;
        this.savedInstanceState=savedInstanceState;


        initDialog();

        initProgessDialog();

        calendar = Calendar.getInstance();
        seekBar = (SeekBar) view.findViewById(R.id.progress);
        textView = (EditText) view.findViewById(R.id.hedging_et_sb);
        date=(Spinner)view.findViewById(R.id.hedging_spr_validtime);
        ll=(LinearLayout)view.findViewById(R.id.hedging_ll);
        et1=(EditText)view.findViewById(R.id.hedging_et_1);
        et2=(EditText)view.findViewById(R.id.hedging_et_2);

        textView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i== EditorInfo.IME_ACTION_DONE){
                    double n=Double.parseDouble(textView.getText().toString());
                    seekBar.setProgress((int)n*100);
                }
                return true;
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView.setText(Double.toString(progress/100.0));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        List<String> array=new ArrayList<>();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM");
        Calendar now=Calendar.getInstance();
        array.add(sdf.format(now.getTime()));
        now.add(Calendar.MONTH,1);
        array.add(sdf.format(now.getTime()));
        int i=0;
        while (i<2){
            now.add(Calendar.MONTH,1);
            if ((now.get(Calendar.MONTH)+1)%3==0) {
                array.add(sdf.format(now.getTime()));
                i++;
            }
        }
        SpinnerAdapter adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_dropdown_item_1line,array);
        date.setAdapter(adapter);

        next=view.findViewById(R.id.hedging_bt_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();

                HedgingInfoSetting.this.sExp=et2.getText().toString();
                if (et1.getText().toString()!=""&&textView.getText().toString()!=""&&et2.getText().toString()!=""&&date.getSelectedItem().toString()!="") {
                    Map<String, String> values = new HashMap<>();
                    values.put("n0", et1.getText().toString());

                    values.put("a", "" + (Double.parseDouble(textView.getText().toString())));

                    values.put("s_exp", et2.getText().toString());

                    values.put("t", date.getSelectedItem().toString());


                    NetUtil.INSTANCE.sendPostRequest(NetUtil.SERVER_BASE_ADDRESS + "/recommend/hedging", values, getContext(), new Callback() {
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
                            if (responseMsg.getData() == null || responseMsg.getCode() == 1008) {
                                dialog.setTitle("网络连接错误");
                                dialog.setMessage("请重新点击");
                                dialogShow();
                            } else {
                                mHandler.obtainMessage(INFO_SUCCESS, responseMsg.getData().toString()).sendToTarget();
                            }
                        }
                    });
                }
                else {
                    dialog.setTitle("信息未填写完善");
                    dialog.setMessage("请重新填写");
                    dialogShow();
                }

            }
        });

        return view;
    }


    /**
     * 设置进度条
     */
    private void initProgessDialog(){
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setTitle("请稍等");
        progressDialog.setMessage("请求发送中");
    }


    private void initDialog(){
        dialog=new AlertDialog.Builder(getActivity());
        dialog.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
    }
    private void dialogShow(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });
    }

    public String getN(){
        return n;
    }

    public String getiK() {
        return iK;
    }

    public String getpAsset() {
        return pAsset;
    }

    public String getsExp() {
        return sExp;
    }

    public Handler getmHandler() {
        return mHandler;
    }

    public void refresh() {
        if (!isNull) {
            ll.removeAllViews();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            HedgingInfoSetting hedgingInfoSetting = new HedgingInfoSetting();
            transaction.add(R.id.build_portfolio_ll, hedgingInfoSetting);
            transaction.show(hedgingInfoSetting);
            transaction.commit();
        }
    }
}
