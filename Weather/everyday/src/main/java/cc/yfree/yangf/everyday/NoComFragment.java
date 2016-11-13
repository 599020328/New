package cc.yfree.yangf.everyday;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.sql.Array;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class NoComFragment extends Fragment {
    LinearLayout.LayoutParams only2W,l4,WwHm,l3_2,l2_2,l2_1,l2_3,l1_2,l1_1,t2_3;
    boolean isF = true;
    String Tyear = "2016";

    public NoComFragment() {
        // Required empty public constructor
    }

    public static int convertDipOrPx(Context context, int dip) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dip*scale + 0.5f*(dip>=0?1:-1));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_no_com, container, false);

        DBManager mgr = new DBManager(getActivity());
        List<TodoList> noCamTodo = null;
        try {
            noCamTodo = mgr.queryNoTodo();
        } catch (ParseException e) {
            e.printStackTrace();
        }
//                mgr.deleteAllTodo();
//        try {
//            mgr.PrintN();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        String st = "2016-8-01";
//        System.out.println("query------->" + "date:"+Integer.valueOf(st));
        Map<String,List<TodoList>> noComMap = new TreeMap<String,List<TodoList>>();
        for(int i=0; i < noCamTodo.size(); i++){

            TodoList todoList = noCamTodo.get(i);
            System.out.println("query------->" + "Tododate0:"+todoList.todo);
            String date = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(todoList.time.getTime()));
            System.out.println("query------->" + "Tododate:"+todoList.time);
            if (checkIsHasInMap(noComMap, date)){
                List<TodoList> tempList = noComMap.get(date);
                tempList.add(todoList);
                noComMap.put(date,tempList);
            }
            else{
                List<TodoList> tempList = new ArrayList<TodoList>();
                tempList.add(todoList);
                noComMap.put(date,tempList);
            }
        }

        boolean isFirst = true;
        for(String date : noComMap.keySet()){
            System.out.println("query------->" + "date:"+date);
            LinearLayout lLyout4  , lLyout3_1, lLyout3_2, lLyout2_1, lLyout2_2, lLyout2_3, lLyout1_1, lLyout1_2;
            TextView T1_2_1, T1_2_2, T2_3 , T2_2 , T1_1_1, T1_1_2;
            /*TextView组件的设置*/
            T1_2_1 = new TextView(getActivity());
            T1_2_2 = new TextView(getActivity());
            T2_3 = new TextView(getActivity());
            T2_2 = new TextView(getActivity());
            T1_1_1 = new TextView(getActivity());
            T1_1_2 = new TextView(getActivity());
//        T1_2_1.setText("9");
            T1_2_1.setTextSize(24);
            T1_2_1.setTextColor(getResources().getColor(R.color.primary_text));
            if (isFirst){
                T1_2_1.setTextColor(getResources().getColor(R.color.blueFont));
            }
//        T1_2_2.setText("10月");
            T1_2_2.setTextSize(8);
            T1_2_2.setTextColor(getResources().getColor(R.color.primary_text));
            if (isFirst){
                T1_2_2.setTextColor(getResources().getColor(R.color.blueFont));
            }
//        T2_3.setText("周日");
            T2_3.setTextSize(16);
            T2_3.setTextColor(getResources().getColor(R.color.primary_text));
            if (isFirst){
                T2_3.setTextColor(getResources().getColor(R.color.blueFont));
            }
//        T2_2.setText("全天事件");
            T2_2.setTextSize(14);
            T2_2.setTextColor(getResources().getColor(R.color.white));
//        T1_1_1.setText("跑步");
            T1_1_1.setTextSize(14);
            T1_1_1.setTextColor(getResources().getColor(R.color.white));
//        T1_1_2.setText("下午6:00到7:00");
            T1_1_2.setTextSize(14);
            T1_1_2.setTextColor(getResources().getColor(R.color.white));


            /*布局属性的设置*/
            lLyout4   = new LinearLayout(getActivity());
            lLyout3_1 = new LinearLayout(getActivity());
            lLyout3_2 = new LinearLayout(getActivity());
            lLyout2_1 = new LinearLayout(getActivity());
            lLyout2_1.setBackground(getResources().getDrawable(R.drawable.shape_todocard));
            lLyout2_2 = new LinearLayout(getActivity());
            lLyout2_2.setBackground(getResources().getDrawable(R.drawable.shape_todocard));
            lLyout2_3 = new LinearLayout(getActivity());
            lLyout1_1 = new LinearLayout(getActivity());
            lLyout1_2 = new LinearLayout(getActivity());
            only2W = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); //1-1-t1t2,1-2-t1t2,2-2-t
            l4 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            l4.setMargins(0, convertDipOrPx(getActivity(),8), 0, convertDipOrPx(getActivity(),8));
            WwHm = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); //3-1
            l3_2 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); //3-2
            lLyout3_2.setOrientation(LinearLayout.VERTICAL);
            l2_2 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); //2-2
            l2_2.setMargins(0, 0, convertDipOrPx(getActivity(),16), convertDipOrPx(getActivity(),8));
            lLyout2_2.setPadding(convertDipOrPx(getActivity(),8),convertDipOrPx(getActivity(),8),convertDipOrPx(getActivity(),8),convertDipOrPx(getActivity(),8));
            l2_1 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, convertDipOrPx(getActivity(),90)); //2-1
            l2_1.setMargins(0, 0, convertDipOrPx(getActivity(),16), convertDipOrPx(getActivity(),8));
            lLyout2_1.setPadding(convertDipOrPx(getActivity(),8),convertDipOrPx(getActivity(),8),convertDipOrPx(getActivity(),8),convertDipOrPx(getActivity(),8));
            l2_3 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); //2-3
            lLyout2_3.setOrientation(LinearLayout.VERTICAL);
            lLyout2_3.setPadding(convertDipOrPx(getActivity(),16), 0, convertDipOrPx(getActivity(),8), 0);
            l1_2 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); //1-2
            lLyout1_2.setOrientation(LinearLayout.HORIZONTAL);
            l1_1 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); //1-1
            lLyout1_1.setOrientation(LinearLayout.VERTICAL);
            l1_1.gravity = Gravity.BOTTOM;
            t2_3 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); //2-3t

            /*获取一些值*/
            String[] data = getDate(date);
            String week  = data[0];
            String day   = reduce0(data[1]);
            String month = data[2];

            T1_2_2.setText(month+"月");
            T2_3.setText(week);
            T1_2_1.setText(day);

            /*加载最外层布局*/
            lLyout4.addView(lLyout3_1, WwHm);
            lLyout4.addView(lLyout3_2, l3_2);
            lLyout3_1.addView(lLyout2_3, l2_3);
            lLyout2_3.addView(lLyout1_2, l1_2);
            lLyout2_3.addView(T2_3, t2_3);
            lLyout1_2.addView(T1_2_1, only2W);
            lLyout1_2.addView(T1_2_2, only2W);


            List<TodoList> value = noComMap.get(date);
            for(int i = 0 ; i < value.size() ; i++) {
                TodoList tempTodo = value.get(i);

                if (tempTodo.isAllDay == 1){
                    String todo = tempTodo.todo;

                    /*加载全天事件布局*/
                    lLyout3_2.addView(setAllDayTodo(todo), l2_2);
                }

            }

            for(int i = 0 ; i < value.size() ; i++) {
                TodoList tempTodo = value.get(i);

                if (tempTodo.isAllDay == 0){
                    String todo = tempTodo.todo;
                    String time = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date(tempTodo.time.getTime()));
                    int hour = Integer.valueOf(time.substring(0, 2));
                    String minute = time.substring(3, 5);
                    System.out.println("query------->0" + "hour:"+hour+"***minute:"+minute+"***time"+time+"***todo:"+todo+"***todotime:"+tempTodo.time);
                    if(hour >= 13 && hour <= 23){
                        hour-=12;
                        time = "下午 "+hour +":"+ minute;
                    }
                    else {
                        time = "上午 "+hour +":"+ minute;
                    }

                    /*加载非全天事件布局*/
                    lLyout3_2.addView(setNoAllDayTodo2(todo, time), l2_1);
                }
            }
            LinearLayout fatherLayout = (LinearLayout)view.findViewById(R.id.list);
            fatherLayout.addView(lLyout4, l4);
            isFirst = false;
        }

        /*首页cardview图片相关*/
        ImageView nextTodo_img = (ImageView)view.findViewById(R.id.topPicNo);
        Picasso.with(getActivity())
                .load(R.drawable.todo3)
                .fit()
                .into(nextTodo_img);

        /*年份设置*/
        TextView year = (TextView)view.findViewById(R.id.year);
        String setyear = Tyear + "年";
        year.setText(setyear);

        return view;
    }

    /*判断Map的键中是否已存在传入值*/
    public static boolean checkIsHasInMap(Map<String,List<TodoList>> noComMap, String targetValue) {
        if (noComMap == null) return false;
        for (String key : noComMap.keySet()) {
            if(key.equals(targetValue))
                return true;
        }
        return false;
    }

    /*判断星期*/
    public String[] getDate(String date){
        String[] week = date.split("-");
        Tyear = week[0];
        int c = Integer.valueOf(week[0].substring(0, 1));
        int y = Integer.valueOf(week[0].substring(2, 3));
        int month = Integer.valueOf(week[1]);
        // int weekIndex=y+(y/4)+(c/4)-2*c+(26*(month+1)/10)+day-1;
        if (month >= 3 && month <= 14) {
        } else {
            y = y - 1;
            month = month + 12;
        }
        int day = Integer.valueOf(week[2]);
        int weekIndex = y + (y / 4) + (c / 4) - 2 * c + (26 * (month + 1) / 10) + day - 1;
        if (weekIndex < 0) {
            weekIndex = weekIndex * (-1);
        } else {
            weekIndex = weekIndex % 7;
        }
        String weekDate=null;
        switch (Integer.valueOf(weekIndex)) {
            case 1:
                weekDate = "周一  ";
                break;
            case 2:
                weekDate = "周二  ";
                break;
            case 3:
                weekDate = "周三  ";
                break;
            case 4:
                weekDate = "周四  ";
                break;
            case 5:
                weekDate = "周五  ";
                break;
            case 6:
                weekDate = "周六  ";
                break;
            case 7:
                weekDate = "周日  ";
                break;
            default:
                weekDate = "周一  ";
                break;
        }
        String stringDay = week[2];
        String stringMonth = week[1];
        String[] data = {weekDate, stringDay, stringMonth};
        return data;
    }

    /*设置全天事件布局*/
    public LinearLayout setAllDayTodo(String todo){
        LinearLayout lLyout2_2;
        lLyout2_2 = new LinearLayout(getActivity());
        lLyout2_2.setBackground(getResources().getDrawable(R.drawable.shape_todocard));
        if (isF){
            lLyout2_2.setBackground(getResources().getDrawable(R.drawable.shape_todocard3));
            isF = false;
        }
        lLyout2_2.setPadding(convertDipOrPx(getActivity(),8),convertDipOrPx(getActivity(),8),convertDipOrPx(getActivity(),8),convertDipOrPx(getActivity(),8));
        lLyout2_2.addView(setAllDayText(todo), only2W);
        return  lLyout2_2;
    }

    /*设置非全天事件布局1*/
    public LinearLayout setNoAllDayTodo1(String todo, String time){
        LinearLayout lLyout1_1;
        lLyout1_1 = new LinearLayout(getActivity());
        lLyout1_1.setOrientation(LinearLayout.VERTICAL);
        lLyout1_1.addView(setNoAllDayText1(todo), only2W);
        lLyout1_1.addView(setNoAllDayText2(time), only2W);
        return  lLyout1_1;
    }

    /*设置非全天事件布局2*/
    public LinearLayout setNoAllDayTodo2(String todo, String time){
        LinearLayout lLyout2_1;
        lLyout2_1 = new LinearLayout(getActivity());
        lLyout2_1.setBackground(getResources().getDrawable(R.drawable.shape_todocard2));
        if (isF){
            lLyout2_1.setBackground(getResources().getDrawable(R.drawable.todo14));
            isF = false;
        }
        lLyout2_1.setPadding(convertDipOrPx(getActivity(),8),convertDipOrPx(getActivity(),8),convertDipOrPx(getActivity(),8),convertDipOrPx(getActivity(),8));
        lLyout2_1.addView(setNoAllDayTodo1(todo,time), l1_1);
        return  lLyout2_1;
    }

    /*设置全天事件里的TextView*/
    public TextView setAllDayText(String todo){
        TextView T2_2;
        T2_2 = new TextView(getActivity());
        T2_2.setTextSize(14);
        T2_2.setTextColor(getResources().getColor(R.color.white));
        T2_2.setText(todo);
        return  T2_2;
    }

    /*设置非全天事件里的TextView1*/
    public TextView setNoAllDayText1(String todo){
        TextView T1_1_1;
        T1_1_1 = new TextView(getActivity());
        T1_1_1.setTextSize(14);
        T1_1_1.setTextColor(getResources().getColor(R.color.white));
        T1_1_1.setText(todo);
        return  T1_1_1;
    }

    /*设置非全天事件里的TextView2*/
    public TextView setNoAllDayText2(String time){
        TextView T1_1_2;
        T1_1_2 = new TextView(getActivity());
        T1_1_2.setTextSize(14);
        T1_1_2.setTextColor(getResources().getColor(R.color.white));
        T1_1_2.setText(time);
        return  T1_1_2;
    }

    /*该方法为了输出一位数时保证前面加一个0，使之与实现十位数对齐，比如时间是12：5，使用该方法后输出为12：05*/
    public String reduce0(String s){
        int jj = Integer.parseInt(s);
        String result = jj + "";
        return result;
    }




}
