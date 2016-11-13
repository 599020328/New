package cc.yfree.yangf.everyday;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class NextrainFragment extends Fragment {
    View view;
    DBManager mgr;
    private Handler handler = new Handler();
    List<TodoList> tempList = null;
    int sizeOfTodo = 0;

    public NextrainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_nextrain, container, false);
        // Inflate the layout for this fragment
        //初始化DBManager
        mgr = new DBManager(getActivity());

        /*首页cardview图片相关*/
        ImageView today_img = (ImageView)view.findViewById(R.id.nextRain_img);
        Picasso.with(getActivity())
                .load(R.drawable.ml2)
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
                JSONObject MyData2 = null, jsonData1 = null, jsonData2 = null;
                int rainTime = 0;

                try {
                    jsonData2 = weatherData2();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("errMsg" + "天气接口调用错误");
                }
                System.out.println("DATA:" + jsonData2);
                try {
                    MyData2 = jsonData2.getJSONObject("result");
//                    System.out.println("errMsg0:" + MyData2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    int nextRainHour = 24;
                    Global.nextRain = MyData2.getJSONObject("hourly").getJSONArray("skycon");

                    for (int i = 0; i < Global.nextRain.length(); i++) {
                        JSONObject jo = Global.nextRain.getJSONObject(i);
                        System.out.println("errMsgTT:" + jo);
                        if (jo.getString("value").equals("RAIN")){
                            if (rainTime == 0){
                                String tempStr = jo.getString("datetime")+":00";
                                Global.nextRainTime = tempStr;
                            }
                            rainTime++;
                        }
                        if (rainTime != 0 && !jo.getString("value").equals("RAIN")){
                            break;
                        }
                    }
                    if (Global.nextRainTime.equals("")){
                        Global.nextRainResult = "近三天无雨";
                        Global.nextRainDirection = "近三天都没有雨，不会影响你的出行计划";
                    }
                    else {
                        long noticeTimeMillis = System.currentTimeMillis();
                        Timestamp nextRainTimeStamp = Timestamp.valueOf(Global.nextRainTime);
                        long nextRainTimeMillis = nextRainTimeStamp.getTime();
                        nextRainHour = (int)((nextRainTimeMillis-noticeTimeMillis)/3600000);
                        Global.nextRainHour = nextRainHour;
                        if (nextRainHour < 1){
                            Global.nextRainResult = "正在下雨";
                            long endTime = noticeTimeMillis + rainTime*3600000;
                            Timestamp endTimeStamp = new Timestamp(endTime);
                            Timestamp startTimeStamp = new Timestamp(noticeTimeMillis);
                            try {
                                tempList = mgr.noComTodoInATime(startTimeStamp, endTimeStamp);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            sizeOfTodo = tempList.size();
                            Global.nextRainDirection = "正在下雨，雨将持续"+rainTime+"个小时，影响你的"+sizeOfTodo+"个出行计划：";
                        }
                        else {
                            Global.nextRainResult = nextRainHour+"小时";
                            long endTime = noticeTimeMillis + (rainTime+nextRainHour)*3600000;
                            Timestamp endTimeStamp = new Timestamp(endTime);
                            Timestamp startTimeStamp = new Timestamp(noticeTimeMillis);
                            try {
                                tempList = mgr.noComTodoInATime(startTimeStamp, endTimeStamp);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            sizeOfTodo = tempList.size();
                            Global.nextRainDirection = "下一场雨在"+nextRainHour+"小时以后，持续"+rainTime+"小时。将影响你的"+sizeOfTodo+"个出行计划：";
                        }
                    }
                    if (nextRainHour < 2){
                        Global.nextRainTips = "快去收衣服";
                    }
                    else if (nextRainHour >= 2 && nextRainHour < 4){
                        Global.nextRainTips = "记得带伞哦";
                    }
                    else if (nextRainHour >= 4 && nextRainHour < 8){
                        Global.nextRainTips = "今天有雨";
                    }
                    else {
                        Global.nextRainTips = "下雨还早呢";
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("errMsg10:" + Global.Tdre);
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        TextView hours = (TextView)view.findViewById(R.id.hours);
                        TextView tips = (TextView)view.findViewById(R.id.tips);
                        TextView direction = (TextView)view.findViewById(R.id.direction);
                        hours.setText(Global.nextRainResult);
                        tips.setText(Global.nextRainTips);
                        direction.setText(Global.nextRainDirection);
                        LinearLayout.LayoutParams text = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        LinearLayout todoLayout = (LinearLayout)view.findViewById(R.id.todoLayout);
                        if (tempList != null){
                            for (int g = 0; g < tempList.size(); g++){
                                TodoList tempTodo = tempList.get(g);
                                String time = new java.text.SimpleDateFormat("MM.dd HH:mm").format(new java.util.Date(tempTodo.time.getTime()));
                                String str = "·   "+time+"  "+tempTodo.todo;
                                todoLayout.addView(makeTodoTextView(str), text);
                            }
                        }
                        if ((sizeOfTodo == 0 || tempList == null) && Global.nextRainHour < 1){
                            todoLayout.addView(makeTodoTextView("      "), text);
                            todoLayout.addView(makeBigTextView("未雨绸缪，有备无患"), text);
                        }
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

    /*加载天气数据2*/
    public JSONObject weatherData2() throws IOException {
        JSONObject jsonData = null;
        String url = "https://api.caiyunapp.com/v2/JJf=WWPzx-TQM4GL/112.8750810000,27.8844250000/forecast.json";
        OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.SECONDS)
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

    /*构造todo*/
    public TextView makeTodoTextView(String str){
        TextView todoTextView = new TextView(getActivity());
        todoTextView.setTextSize(14);
        todoTextView.setTextColor(getResources().getColor(R.color.white));
        todoTextView.setText(str);
        return todoTextView;
    }

    /*构造Big todo*/
    public TextView makeBigTextView(String str){
        TextView todoTextView = new TextView(getActivity());
        todoTextView.setTextSize(18);
        todoTextView.setTextColor(getResources().getColor(R.color.white));
        todoTextView.setText(str);
        return todoTextView;
    }

}
