package utf8.optadvisor.fragment;

import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import utf8.optadvisor.R;
import utf8.optadvisor.util.MyXFormatter;


public class OptionShow extends Fragment {
    private String information;
    TextView present_price;
    TextView ups_and_downs;
    TextView yesterday_end;
    TextView today_start;
    TextView highest_price;
    TextView lowest_price;
    TextView quantity;
    TextView volume;
    TextView update_time;
    TextView status;
    LineChart lineChart;
    CandleStickChart candleStickChart;
    LineChartInfo lineChartInfo;
    CandleStickChartInfo candleStickChartInfo;



    @Override
    /**
     * 展示行情信息
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_option_show, container, false);
        present_price=(TextView) view.findViewById(R.id.present_price);
        ups_and_downs=(TextView) view.findViewById(R.id.ups_and_downs);
        yesterday_end=(TextView) view.findViewById(R.id.yesterday_end);
        today_start=(TextView) view.findViewById(R.id.today_start);
        highest_price=(TextView) view.findViewById(R.id.highest_price);
        lowest_price=(TextView) view.findViewById(R.id.lowest_price);
        quantity=(TextView) view.findViewById(R.id.quantity);
        volume=(TextView) view.findViewById(R.id.volume);
        update_time=(TextView) view.findViewById(R.id.update_time);
        status=(TextView) view.findViewById(R.id.status);
        get50ETFinfo();
        draw50ETF(information);
        lineChart=(LineChart) view.findViewById(R.id.line_chart);
        initLineChart();
        drawLineChart(lineChartInfo);
        candleStickChart=(CandleStickChart) view.findViewById(R.id.candle_stick_chart);
        initCandleStickChart();
        drawCandleStickChart(candleStickChartInfo);



        return view;
    }


    //初始化线性图
    private void initLineChart(){
        new Thread(new Runnable(){
            @Override
            public void run(){
                try{
                    OkHttpClient client=new OkHttpClient();
                    Request request=new Request.Builder()
                            .url("http://yunhq.sse.com.cn:32041/v1/sh1/line/510050?select=time,price,volume")
                            .build();
                    Response response=client.newCall(request).execute();
                    String jsonData=response.body().string();
                    Gson gson=new Gson();
                    LineChartInfo lineChartInfo =gson.fromJson(jsonData,LineChartInfo.class);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void drawLineChart(LineChartInfo lineChartInfo){
        lineChart.setDrawGridBackground(false);//chart 绘图区后面的背景矩形将绘制
        lineChart.setDrawBorders(false);//禁止绘制图表边框的线
        lineChart.setScaleEnabled(true);
        ArrayList<Entry> entries=new ArrayList<>();
        for(int i = 0; i< lineChartInfo.getLine().length; i++){
            int x=Integer.valueOf(lineChartInfo.getLine()[i][0])/10000*60+Integer.valueOf(lineChartInfo.getLine()[i][0])%10000/100;
            entries.add(new Entry(x,Float.parseFloat(lineChartInfo.getLine()[i][1])));
        }
        LineDataSet set1;
        if (lineChart.getData() != null &&
                lineChart.getData().getDataSetCount() > 0) {
            //获取数据1
            set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            set1.setValues(entries);
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        }else{
            set1 = new LineDataSet(entries,"分时线");
            set1.setLineWidth(1f);//设置线宽
            set1.setColor(Color.parseColor("#D15FEE"));
            set1.setDrawCircles(false);
            set1.enableDashedHighlightLine(10f, 5f, 0f);//点击后的高亮线的显示样式
            set1.setHighlightLineWidth(2f);//设置点击交点后显示高亮线宽
            set1.setHighlightEnabled(true);//是否禁用点击高亮线
            set1.setHighLightColor(Color.parseColor("#D15FEE"));//设置点击交点后显示交高亮线的颜色
            set1.setDrawFilled(false);//设置禁用范围背景填充
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the datasets
            XAxis xAxis= lineChart.getXAxis();
            xAxis.setValueFormatter(new MyXFormatter(true));//x轴自定义格式
            YAxis leftAxis=lineChart.getAxisLeft();
            LimitLine limitLine=new LimitLine(Float.valueOf(yesterday_end.getText().toString()),yesterday_end.getText().toString());
            limitLine.setLineColor(Color.RED);
            limitLine.setTextColor(Color.RED);
            limitLine.setTextSize(10f);
            leftAxis.addLimitLine(limitLine);

            YAxis rightAxis=lineChart.getAxisRight();
            //设置图表右边的y轴禁用
            rightAxis.setEnabled(false);
            //创建LineData对象 属于LineChart折线图的数据集合
            LineData data = new LineData(dataSets);// 添加到图表中
            lineChart.setData(data);//绘制图表
            lineChart.invalidate();
        }



    }

    //线性图数据
    private class LineChartInfo {
        private String code;
        private String pre_close;
        private String date;
        private String time;
        private String total;
        private String begin;
        private String end;
        private String[][] line;
        //"code":"510050","prev_close":2.526,"date":20180814,"time":151455,"total":241,"begin":0,"end":241,"line":

        public void setBegin(String begin) {
            this.begin = begin;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public void setEnd(String end) {
            this.end = end;
        }

        public void setLine(String[][] line) {
            this.line = line;
        }

        public void setPre_close(String pre_close) {
            this.pre_close = pre_close;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getBegin() {
            return begin;
        }

        public String getCode() {
            return code;
        }

        public String getDate() {
            return date;
        }

        public String getEnd() {
            return end;
        }

        public String getPre_close() {
            return pre_close;
        }

        public String getTime() {
            return time;
        }

        public String getTotal() {
            return total;
        }

        public String[][] getLine() {
            return line;
        }
    }




    //初始化k线图
    private void initCandleStickChart(){
        new Thread(new Runnable(){
            @Override
            public void run(){
                try{
                    OkHttpClient client=new OkHttpClient();
                    Request request=new Request.Builder()
                            .url("http://yunhq.sse.com.cn:32041/v1/sh1/dayk/510050?select=date,open,high,low,close,volume&begin=-300&end=-1")
                            .build();
                    Response response=client.newCall(request).execute();
                    String jsonData=response.body().string();
                    Gson gson=new Gson();
                    candleStickChartInfo =gson.fromJson(jsonData,CandleStickChartInfo.class);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

    }
    private void drawCandleStickChart(CandleStickChartInfo candleStickChartInfo){
        candleStickChart.setDrawGridBackground(false);//chart 绘图区后面的背景矩形将绘制
        candleStickChart.setDrawBorders(false);//禁止绘制图表边框的线
        candleStickChart.setScaleXEnabled(true);
        candleStickChart.setScaleYEnabled(false);
        candleStickChart.setMaxVisibleValueCount(8);
        ArrayList<CandleEntry> candleEntries=new ArrayList<>();
        for(int i = 0; i<299; i++){
            CandleEntry entry=new CandleEntry(i,
                    Float.valueOf(candleStickChartInfo.getKline()[i][2]),
                    Float.valueOf(candleStickChartInfo.getKline()[i][3]),
                    Float.valueOf(candleStickChartInfo.getKline()[i][1]),
                    Float.valueOf(candleStickChartInfo.getKline()[i][4]));
            candleEntries.add(entry);
        }
        CandleDataSet set1;
        if (candleStickChart.getData() != null &&
                candleStickChart.getData().getDataSetCount() > 0) {
            //获取数据1
            set1 = (CandleDataSet) candleStickChart.getData().getDataSetByIndex(0);
            set1.setValues(candleEntries);
            candleStickChart.getData().notifyDataChanged();
            candleStickChart.notifyDataSetChanged();
        }else{
            set1 = new CandleDataSet(candleEntries,"日k线");
            set1.setColor(Color.parseColor("#D15FEE"));
            set1.enableDashedHighlightLine(10f, 5f, 0f);//点击后的高亮线的显示样式
            set1.setHighlightLineWidth(2f);//设置点击交点后显示高亮线宽
            set1.setHighlightEnabled(true);//是否禁用点击高亮线
            set1.setHighLightColor(Color.parseColor("#D15FEE"));//设置点击交点后显示交高亮线的颜色
            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setShadowColor(Color.DKGRAY);//影线颜色
            set1.setShadowColorSameAsCandle(true);//影线颜色与实体一致
            set1.setShadowWidth(0.7f);//影线
            set1.setDecreasingColor(Color.RED);
            set1.setDecreasingPaintStyle(Paint.Style.FILL);//红涨，实体
            set1.setIncreasingColor(Color.GREEN);
            set1.setIncreasingPaintStyle(Paint.Style.STROKE);//绿跌，空心
            set1.setNeutralColor(Color.RED);//当天价格不涨不跌（一字线）颜色
            set1.setHighlightLineWidth(1f);//选中蜡烛时的线宽
            set1.setDrawValues(false);//在图表中的元素上面是否显示数值


            ArrayList<ICandleDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the datasets
            XAxis xAxis= candleStickChart.getXAxis();
            Matrix matrix = new Matrix();
            matrix.postScale(3.0f, 1f);

            YAxis rightAxis=candleStickChart.getAxisRight();
            //设置图表右边的y轴禁用
            rightAxis.setEnabled(false);
            //创建LineData对象 属于LineChart折线图的数据集合
            CandleData data = new CandleData(dataSets);// 添加到图表中
            candleStickChart.setData(data);//绘制图表
            candleStickChart.invalidate();
        }



    }

    //k线图数据
    private class CandleStickChartInfo{
        private String code;
        private String total;
        private String begin;
        private String end;
        private String[][] kline;

        public void setTotal(String total) {
            this.total = total;
        }

        public void setEnd(String end) {
            this.end = end;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public void setBegin(String begin) {
            this.begin = begin;
        }

        public void setKline(String[][] kline) {
            this.kline = kline;
        }

        public String getTotal() {
            return total;
        }

        public String getEnd() {
            return end;
        }

        public String getCode() {
            return code;
        }

        public String getBegin() {
            return begin;
        }

        public String[][] getKline() {
            return kline;
        }
    }







    //初始化50etf信息
    private void get50ETFinfo(){
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {

                    try {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url("http://hq.sinajs.cn/list=s_sh510050,sh510050")
                                .build();
                        Response response = client.newCall(request).execute();
                        information = response.body().string();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

        });
        thread.start();
        try {
            thread.sleep(2000);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private void draw50ETF(String information){
        int comma1 = information.indexOf(",");
        int comma2 = information.indexOf(",", comma1 + 1);
        int comma3 = information.indexOf(",", comma2 + 1);
        int comma4 = information.indexOf(",", comma3 + 1);
        int comma5 = information.indexOf(",", comma4 + 1);
        int next = information.indexOf("var hq_str_sh510050");
        int comma1_2=information.indexOf(",",next);
        int comma2_2=information.indexOf(",",comma1_2+  1);
        int comma3_2=information.indexOf(",",comma2_2+  1);
        int comma4_2=information.indexOf(",",comma3_2+  1);
        int comma5_2=information.indexOf(",",comma4_2+  1);
        int comma6_2=information.indexOf(",",comma5_2+  1);
        int last1comma=information.lastIndexOf(",");
        int last2comma=information.lastIndexOf(",",last1comma-1);


        present_price.setText(information.substring(comma1 + 1, comma2));
        ups_and_downs.setText(information.substring(comma2 + 1, comma3));
        if (information.substring(comma2 + 1, comma3).startsWith("-")) {
            ups_and_downs.setTextColor(Color.parseColor("#FF0000"));
        }
        quantity.setText(information.substring(comma4 + 1, comma5)+"手");
        volume.setText(information.substring(comma5 + 1, information.indexOf("\";"))+"万元");
        today_start.setText(information.substring(comma1_2+1,comma2_2));
        yesterday_end.setText(information.substring(comma2_2+1,comma3_2));
        highest_price.setText(information.substring(comma4_2+1,comma5_2));
        lowest_price.setText(information.substring(comma5_2+1,comma6_2));
        update_time.setText(information.substring(last2comma+1,last1comma));
        if(information.substring(information.length()-4).equals("00\";")){
            status.setText("开盘");
        }
        else{
            status.setText("开盘");
        }
    }

}




