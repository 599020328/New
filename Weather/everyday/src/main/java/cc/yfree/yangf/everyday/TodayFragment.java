package cc.yfree.yangf.everyday;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cc.yfree.yangf.everyday.R;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static cc.yfree.yangf.everyday.Global.FX;
import static cc.yfree.yangf.everyday.Global.apiResult1;
import static cc.yfree.yangf.everyday.Global.jsonArray1;
import static cc.yfree.yangf.everyday.Global.tem;
import static cc.yfree.yangf.everyday.Global.weatherData;

/**
 * A simple {@link Fragment} subclass.
 */
public class TodayFragment extends Fragment {

    Activity mActivity;
    AppCompatActivity mAppCompatActivity;
    View view;
    private Handler handler = new Handler();

    /*图表数据*/
    /*=========== 控件相关 ==========*/
    private LineChartView mLineChartView;               //线性图表控件

    /*=========== 数据相关 ==========*/
    private LineChartData mLineData;                    //图表数据
    private int numberOfLines = 1;                      //图上折线/曲线的显示条数
    private int maxNumberOfLines = 4;                   //图上折线/曲线的最多条数
    private int numberOfPoints = 12;                    //图上的节点数

    /*=========== 状态相关 ==========*/
    private boolean isHasAxes = true;                   //是否显示坐标轴
    private boolean isHasAxesNames = false;              //是否显示坐标轴名称
    private boolean isHasLines = true;                  //是否显示折线/曲线
    private boolean isHasPoints = true;                 //是否显示线上的节点
    private boolean isFilled = true;                   //是否填充线下方区域
    private boolean isHasPointsLabels = true;          //是否显示节点上的标签信息
    private boolean isCubic = true;                    //是否是立体的
    private boolean isPointsHasSelected = false;        //设置节点点击后效果(消失/显示标签)
    private boolean isPointsHaveDifferentColor;         //节点是否有不同的颜色

    /*=========== 其他相关 ==========*/
    private ValueShape pointsShape = ValueShape.CIRCLE; //点的形状(圆/方/菱形)
    float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints]; //将线上的点放在一个数组中
    /*图表数据*/


    public TodayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_today, container, false);

        /*温度曲线图表的显示和控制*/
        getLayoutId();
        initView();
        initData();

        /*首页cardview图片相关*/
        ImageView today_img = (ImageView)view.findViewById(R.id.today_img);
        Picasso.with(getActivity())
                .load(R.drawable.todo8)
                .fit()
                .into(today_img);

        /*下拉刷新*/
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO Auto-generated method stub
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 6000);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject MyData1 = null, MyData2 = null, jsonData1 = null, jsonData2 = null;

                try {
                    jsonData1 = weatherData1();
                    jsonData2 = weatherData2();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("errMsg" + "天气接口调用错误");
                }
                System.out.println("DATA:" + jsonData2);
                try {
                    MyData1 = jsonData1.getJSONObject("result");
                    MyData2 = jsonData2.getJSONObject("result");
//                    System.out.println("errMsg0:" + MyData1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    Global.tem    = MyData1.getInt("temperature");
                    Global.SD = (int) (MyData1.getDouble("humidity")*100);
                    Global.skycon_ = Skycon(MyData1.getString("skycon"));
                    Global.PM25_ = Air(MyData1.getInt("pm25"));
                    Global.FX = Feng(MyData1.getJSONObject("wind").getInt("direction"));
                    Global.FJ = FengJ(MyData1.getJSONObject("wind").getInt("speed"));
                    Global.Tdescription = MyData2.getJSONObject("hourly").getString("description");
                    Global.Ndescription = MyData2.getJSONObject("minutely").getString("description");
                    Global.jsonArray1 = MyData2.getJSONObject("daily").getJSONArray("astro");
                    Global.Tsun = (JSONObject) Global.jsonArray1.get(0);
                    Global.sunDown = Global.Tsun.getJSONObject("sunset").getString("time");
                    Global.sunRise = Global.Tsun.getJSONObject("sunrise").getString("time");
                    Global.sky = (JSONObject)MyData2.getJSONObject("daily").getJSONArray("skycon").get(0);
                    Global.Tsky = Global.sky.getString("value");
                    Global.dre = (JSONObject)MyData2.getJSONObject("daily").getJSONArray("dressing").get(0);
                    Global.Tdre = Global.dre.getInt("index");
                    Global.zwx = (JSONObject)MyData2.getJSONObject("daily").getJSONArray("ultraviolet").get(0);
                    Global.Tzw = Global.zwx.getInt("index");
                    if (MyData2.has("alert")){
                        try{
                            Global.codeString = MyData2.getString("alert").substring(2, Global.alert.length()-3);
                            Global.codeObject  = new JSONObject(Global.codeString);
                            Global.code0 = Global.codeObject.getString("code");
                        }
                        catch (Exception e){
                            ;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("errMsg10:" + Global.Tdre);
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //在UI线程更新UI
                        TextView temperature = (TextView) view.findViewById(R.id.temperature);
                        TextView skycon = (TextView) view.findViewById(R.id.skycon);
                        TextView air = (TextView) view.findViewById(R.id.air);
                        TextView fx = (TextView)view.findViewById(R.id.fx);
                        TextView fj = (TextView)view.findViewById(R.id.fj);
                        TextView xdsd = (TextView)view.findViewById(R.id.xdsd);
                        TextView today = (TextView) view.findViewById(R.id.today);
                        TextView now = (TextView) view.findViewById(R.id.now);
                        TextView rise = (TextView)view.findViewById(R.id.sunrise);
                        TextView down = (TextView)view.findViewById(R.id.sunset);
                        TextView tips = (TextView)view.findViewById(R.id.tips);
                        CardView yjCard = (CardView)view.findViewById(R.id.yjCard);
                        String tip1 = umbrella(Global.Tsky);
                        String tip2 = dress(Global.Tdre);
                        String tip3 = zwx(Global.Tzw);
                        if (Global.alert != null) {
                            Yj(Global.code0);
                            yjCard.setVisibility(View.VISIBLE);
                        }//设置颜色和字
                        else{
                            ;
                        }
                        rise.setText("{wic_sunrise}     " + Global.sunRise);
                        down.setText("{wic_sunrise}     " + Global.sunDown);
                        temperature.setText(Global.tem + "°");
                        skycon.setText(Global.skycon_);
                        air.setText(Global.PM25_);
                        if (Global.PM25_.length() > 2){
                            air.setTextSize(TypedValue.COMPLEX_UNIT_SP,16); //设置16SP
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            layoutParams.setMargins(0,16,0,0);//4个参数按顺序分别是左上右下
                            air.setLayoutParams(layoutParams);
                        }
                        fx.setText(Global.FX);
                        fj.setText(Global.FJ + "级");
                        xdsd.setText(Global.SD + "%");
                        today.setText("· " + Global.Tdescription);
                        now.setText("· " + Global.Ndescription);
                        tips.setText("· " + tip1 + "\n\n· " + tip2 + "\n\n· " + tip3);
                    }
                });
            }
        }).start();
        System.out.println("errMsg" + Global.Tsky);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /*预警   暴雨橙色预警*/
    public String Yj(String yjz) {
        String yj1 = null ,yj2 = null ,yj = null;
        TextView alert = (TextView) view.findViewById(R.id.alert);
        CardView yjCard = (CardView)view.findViewById(R.id.yjCard);
        Global.code1 = Global.code0.substring(0,1);
        Global.code2 = Global.code0.substring(2,3);
        if (Global.code1.equals("01")) yj1 = "台风";
        if (Global.code1.equals("02")) yj1 = "暴雨";
        if (Global.code1.equals("03")) yj1 = "暴雪";
        if (Global.code1.equals("04")) yj1 = "寒潮";
        if (Global.code1.equals("05")) yj1 = "大风";
        if (Global.code1.equals("06")) yj1 = "沙尘暴";
        if (Global.code1.equals("07")) yj1 = "高温";
        if (Global.code1.equals("08")) yj1 = "干旱";
        if (Global.code1.equals("09")) yj1 = "雷电";
        if (Global.code1.equals("10")) yj1 = "冰雹";
        if (Global.code1.equals("11")) yj1 = "霜冻";
        if (Global.code1.equals("12")) yj1 = "大雾";
        if (Global.code1.equals("13")) yj1 = "霾";
        if (Global.code1.equals("14")) yj1 = "道路结冰";
        if (Global.code1.equals("15")) yj1 = "森林火灾";
        if (Global.code1.equals("16")) yj1 = "雷雨大风";

        if (Global.code2.equals("01")){
            yj2 = "蓝色";
            yjCard.setCardBackgroundColor(getResources().getColor(R.color.blue));
        }
        if (Global.code2.equals("02")){
            yj2 = "黄色";
            yjCard.setCardBackgroundColor(getResources().getColor(R.color.yellow));
        }
        if (Global.code2.equals("03")){
            yj2 = "橙色";
        }
        if (Global.code2.equals("04")){
            yj2 = "红色";
            yjCard.setCardBackgroundColor(getResources().getColor(R.color.red));
        }
        yj = "   " + yj1 + yj2 + "预警";

        alert.setText(yj);
        return  yj;
    }

    /*紫外线*/
    public String zwx(int zwx) {
        String zw = null;
        System.out.println("wtf" + zwx);
        if (zwx == 1) zw = "今天紫外线超弱的";
        if (zwx == 2) zw = "紫外线弱";
        if (zwx == 3) zw = "紫外线中等";
        if (zwx == 4) zw = "紫外线较强";
        if (zwx == 5) zw = "紫外线很强，你需要防晒霜";
        return  zw;
    }

    /*穿衣*/
    public String dress(int dress){
        String dre = null;
        if (dress == 1) dre = "极热，注意防暑";
        if (dress == 2) dre = "很热，来根冰棍";
        if (dress == 3) dre = "热，适合穿T恤";
        if (dress == 4) dre = "暖，舒服的温度";
        if (dress == 5) dre = "凉，穿薄外套吧";
        if (dress == 6) dre = "冷，厚点的外套";
        if (dress == 7) dre = "寒冷，穿秋裤啦";
        if (dress == 8) dre = "极冷，多穿点！";
        return dre;
    }

    /*是否需要带伞*/
    public String umbrella(String Tsky){
        String um = null;
        if (Tsky.equals("RAIN") || Tsky.equals("SNOW")) um = "记得带伞哦";
        else um = "不用带伞";
        return um;
    }

    /*获取天气情况*/
    public String Skycon(String skycon){
        String sky = null;
        if (skycon.equals("CLEAR_DAY")) sky = "晴天";
        if (skycon.equals("CLEAR_NIGHT")) sky = "晴夜";
        if (skycon.equals("PARTLY_CLOUDY_DAY")) sky = "多云";
        if (skycon.equals("PARTLY_CLOUDY_NIGHT")) sky = "多云";
        if (skycon.equals("CLOUDY")) sky = "阴";
        if (skycon.equals("RAIN")) sky = "雨";
        if (skycon.equals("SNOW")) sky = "雪";
        if (skycon.equals("WIND")) sky = "风";
        if (skycon.equals("FOG")) sky = "雾";
        return sky;
    }

    /*获取空气质量*/
    public String Air(int PM){
        String air = null;
        if (PM <= 35) air = "优";
        if (PM > 35 && PM <= 75) air = "良";
        if (PM > 75 && PM <= 115) air = "轻度污染";
        if (PM > 115 && PM <= 150) air = "中度污染";
        if (PM > 150 && PM <= 250) air = "重度污染";
        if (PM > 250) air = "严重污染";
        return air;
    }

    /*获取风向*/
    public String Feng(int fx){
        String fenX = null;
        if(fx <= 22.5 || fx > 337.5) fenX = "北风";
        if(fx > 22.5 && fx <= 67.5) fenX = "东北风";
        if(fx > 67.5 && fx <= 112.5) fenX = "东风";
        if(fx > 112.5 && fx <= 157.5) fenX = "东南风";
        if(fx > 157.5 && fx <= 202.5) fenX = "南风";
        if(fx > 202.5 && fx <= 247.5) fenX = "西南风";
        if(fx > 247.5 && fx <= 292.5) fenX = "西风";
        if(fx > 292.5 && fx <= 337.5) fenX = "西北风";
        return fenX;
    }

    /*获取风级*/
    public int FengJ(int fj){
        int fenj = 0;
        if (fj < 1) fenj = 0;
        if (fj >= 1 && fj <= 5) fenj = 1;
        if (fj >= 6 && fj <= 11) fenj = 2;
        if (fj >= 12 && fj <= 19) fenj = 3;
        if (fj >= 20 && fj <= 28) fenj = 4;
        if (fj >= 29 && fj <= 38) fenj = 5;
        if (fj >= 39 && fj <= 49) fenj = 6;
        if (fj >= 50 && fj <= 61) fenj = 7;
        if (fj >= 62 && fj <= 74) fenj = 8;
        if (fj >= 75 && fj <= 88) fenj = 9;
        if (fj >= 89 && fj <= 102) fenj = 10;
        if (fj >= 103 && fj <= 117) fenj = 11;
        if (fj > 117) fenj = 12;
        return  fenj;
    }

    /*加载天气数据1*/
    public JSONObject weatherData1() throws IOException {
        JSONObject jsonData = null;
        String url = "https://api.caiyunapp.com/v2/JJf=WWPzx-TQM4GL/112.8750810000,27.8844250000/realtime.json";
        OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(40,TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = mOkHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            String data = response.body().string();
            try {
                if(data.substring(0,1).equals("[")){
                    JSONArray arr=new JSONArray(data);
                    jsonData = new JSONObject("{\"code\": 0,\"msg\": \"OK\"}");
                    jsonData.put("data",arr);
                }
                else{
                    jsonData = new JSONObject(data);
                    if(jsonData.getString("status")!="ok"){
                        System.out.println("URLERROR:"+url);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(getActivity(), "接口调用错误", Toast.LENGTH_SHORT).show();
        }
        return jsonData;
    }

    /*加载天气数据2*/
    public JSONObject weatherData2() throws IOException {
        JSONObject jsonData = null;
        String url = "https://api.caiyunapp.com/v2/JJf=WWPzx-TQM4GL/112.8750810000,27.8844250000/forecast.json";
        OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(40,TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = mOkHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            String data = response.body().string();
            try {
                if(data.substring(0,1).equals("[")){
                    JSONArray arr=new JSONArray(data);
                    jsonData = new JSONObject("{\"code\": 0,\"msg\": \"OK\"}");
                    jsonData.put("data",arr);
                }
                else{
                    jsonData = new JSONObject(data);
                    if(jsonData.getString("status")!="ok"){
                        System.out.println("URLERROR:"+url);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(getActivity(), "接口调用错误", Toast.LENGTH_SHORT).show();
        }
        return jsonData;
    }

    /*设置图表*/
    public void initData()
    {
        setPointsValues();          //设置每条线的节点值
        setLinesDatas();            //设置每条线的一些属性
        resetViewport();            //计算并绘图
    }

    /**
     * 利用随机数设置每条线对应节点的值
     */
    private void setPointsValues() {
        for (int i = 0; i < maxNumberOfLines; ++i) {
            for (int j = 0; j < numberOfPoints; ++j) {
                randomNumbersTab[i][j] = (float) Math.random() * 100f;
            }
        }
    }

    /**
     * 设置线的相关数据
     */
    private void setLinesDatas() {
        List<Line> lines = new ArrayList<>();
        //循环将每条线都设置成对应的属性
        for (int i = 0; i < numberOfLines; ++i) {
            //节点的值
            List<PointValue> values = new ArrayList<>();
            for (int j = 0; j < numberOfPoints; ++j) {
                values.add(new PointValue(j, randomNumbersTab[i][j]));
            }

            /*========== 设置线的一些属性 ==========*/
            Line line = new Line(values);               //根据值来创建一条线
            line.setColor(ChartUtils.COLORS[i]);        //设置线的颜色
            line.setShape(pointsShape);                 //设置点的形状
            line.setHasLines(isHasLines);               //设置是否显示线
            line.setHasPoints(isHasPoints);             //设置是否显示节点
            line.setCubic(isCubic);                     //设置线是否立体或其他效果
            line.setFilled(isFilled);                   //设置是否填充线下方区域
            line.setHasLabels(isHasPointsLabels);       //设置是否显示节点标签
            //设置节点点击的效果
            line.setHasLabelsOnlyForSelected(isPointsHasSelected);
            //如果节点与线有不同颜色 则设置不同颜色
            if (isPointsHaveDifferentColor) {
                line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
            }
            lines.add(line);
        }

        mLineData = new LineChartData(lines);                      //将所有的线加入线数据类中
        mLineData.setBaseValue(Float.NEGATIVE_INFINITY);           //设置基准数(大概是数据范围)
        mLineData.setValueLabelBackgroundAuto(false);            //设置数据背景是否跟随节点颜色
        mLineData.setValueLabelBackgroundEnabled(false);         //设置是否有数据背景
        mLineData.setValueLabelsTextColor(Color.BLACK);           //设置数据文字颜色
        /* 其他的一些属性方法 可自行查看效果
         * mLineData.setValueLabelBackgroundAuto(true);            //设置数据背景是否跟随节点颜色
         * mLineData.setValueLabelBackgroundColor(Color.BLUE);     //设置数据背景颜色
         * mLineData.setValueLabelBackgroundEnabled(true);         //设置是否有数据背景
         * mLineData.setValueLabelsTextColor(Color.RED);           //设置数据文字颜色
         * mLineData.setValueLabelTextSize(15);                    //设置数据文字大小
         * mLineData.setValueLabelTypeface(Typeface.MONOSPACE);    //设置数据文字样式
        */

        //如果显示坐标轴
        if (isHasAxes) {
            Axis axisX = new Axis();                    //X轴
            Axis axisY = new Axis().setHasLines(true);  //Y轴
            axisX.setTextColor(Color.GRAY);             //X轴灰色
            axisY.setTextColor(Color.GRAY);             //Y轴灰色
            //setLineColor()：此方法是设置图表的网格线颜色 并不是轴本身颜色
            //如果显示名称
            if (isHasAxesNames) {
                axisX.setName("Axis X");                //设置名称
                axisY.setName("Axis Y");
            }
            mLineData.setAxisXBottom(axisX);            //设置X轴位置 下方
            //mLineData.setAxisYLeft(axisY);              //设置Y轴位置 左边   在这里不显示Y轴
        } else {
            mLineData.setAxisXBottom(null);
            mLineData.setAxisYLeft(null);
        }

        mLineChartView.setLineChartData(mLineData);    //设置图表控件
    }

    /**
     * 重点方法，计算绘制图表
     */
    private void resetViewport() {
        //创建一个图标视图 大小为控件的最大大小
        final Viewport v = new Viewport(mLineChartView.getMaximumViewport());
        v.left = 0;                             //坐标原点在左下
        v.bottom = 0;
        v.top = 100;                            //最高点为100
        v.right = numberOfPoints - 1;           //右边为点 坐标从0开始 点号从1 需要 -1
        mLineChartView.setMaximumViewport(v);   //给最大的视图设置 相当于原图
        mLineChartView.setCurrentViewport(v);   //给当前的视图设置 相当于当前展示的图
    }

//            @Override
    public int getLayoutId() {
        return R.layout.content_weather;
    }

    //        @Override
    public void initView() {
        mLineChartView = (LineChartView) view.findViewById(R.id.lvc_main);
        /**
         * 禁用视图重新计算 主要用于图表在变化时动态更改，不是重新计算
         * 类似于ListView中数据变化时，只需notifyDataSetChanged()，而不用重新setAdapter()
         */
        mLineChartView.setViewportCalculationEnabled(false);
    }



}
