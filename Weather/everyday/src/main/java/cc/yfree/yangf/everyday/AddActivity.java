package cc.yfree.yangf.everyday;

import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class AddActivity extends AppCompatActivity {

    private TextView date,time,notice;
    private Calendar cal;
    private int year,month,day,hour,minute,noticeTime = 0;
    private int zyear,zmonth,zday,zhour,zminute,isAllDay = 0;
    private String week;
    private DBManager mgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //初始化DBManager
        mgr = new DBManager(this);



        /*设置toolbar*/
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.save:
                        add();
//                        deleteAll();
                        break;
                }
                return true;
            }
        });

        //获取当前日期
        getDate();
        date = (TextView)findViewById(R.id.datepick);
        date.setText(year+"年"+(month+1)+"月"+day+"日" +"  "+week);
        date.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker arg0, int year_n, int month_n, int day_n) {
//                        Toast.makeText(getApplicationContext(), "click" + year + month + day, Toast.LENGTH_SHORT).show();
                        year = year_n;
                        month = month_n;
                        day = day_n;
                        cal.set(year, month, day);
                        week = getWeekDay(cal);
                        date.setText(year+"年"+(month+1)+"月"+day+"日" +"  "+week);      //将选择的日期显示到TextView中,因为之前获取month直接使用，所以不需要+1，这个地方需要显示，所以+1
                    }
                };
                DatePickerDialog dialog = new DatePickerDialog(AddActivity.this, 0,listener,year,month,day);//dialog上的日期
                dialog.show();
//                Toast.makeText(getApplicationContext(), "click" + year + month + day, Toast.LENGTH_SHORT).show();
            }           //getDate
        });

        /*打开开关显示*/
        time = (TextView)findViewById(R.id.timepick);
        Switch sw = (Switch)super.findViewById(R.id.switch1);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    time.setVisibility(View.GONE);
                    isAllDay = 1;
                }
                else{
                    time.setVisibility(View.VISIBLE);
                    isAllDay = 0;
                }
            }
        });

        time.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                TimePickerDialog.OnTimeSetListener time_callback=new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker arg0, int arg1, int arg2) {//arg1表示小时，arg2表示分钟
                        time.setText(String.format("%s:%s", arg1, format_conver(arg2)));//格式输出
                        hour = arg1;
                        minute = arg2;
                    }
                };
                TimePickerDialog timePicker=new TimePickerDialog(AddActivity.this, 0, time_callback, hour, minute, true);
                timePicker.show();
            }
        });


        /* 添加提醒*/
        notice = (TextView)findViewById(R.id.notice);
        notice.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showSingleChoice();
            }
        });

    }

    /*检查空白？*/
    private void dialog1(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("提示"); //设置标题
        builder.setMessage("请输入计划内容!"); //设置内容
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); //关闭dialog
            }
        });

        //参数都设置完成了，创建并显示出来
        builder.create().show();
    }

    /*添加Todo数据*/
    public void add() {
        ArrayList<TodoList> todoLists = new ArrayList<TodoList>();
        String timeStr = year+"-"+(month+1)+"-"+day+" "+hour+":"+minute+":00";
        String ztimeStr = zyear+"-"+(zmonth+1)+"-"+zday+" "+zhour+":"+zminute+":00";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT+8"));// 中国北京时间，东八区

        Date zdateTime = null;
        try {
            zdateTime = format.parse(ztimeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Timestamp noticeTimeStamp = new Timestamp(zdateTime.getTime());

        Date dateTime = null;
        try {
            dateTime = (Date) format.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Timestamp timestamp = new Timestamp(dateTime.getTime());

        if (noticeTime == 1){
            System.out.println("query------->" + "111");
            noticeTimeStamp = new Timestamp(System.currentTimeMillis());
        }
        else if(noticeTime == 2){
            System.out.println("query------->" + "222");
            noticeTimeStamp = timestamp;
        }
        else if(noticeTime == 3){
            System.out.println("query------->" + "333");
            long sqlLastTime = timestamp.getTime();
            sqlLastTime = sqlLastTime - 900000;
            noticeTimeStamp = new Timestamp(sqlLastTime);
        }
        else if(noticeTime == 4){
            System.out.println("query------->" + "444");
            long sqlLastTime = timestamp.getTime();
            sqlLastTime = sqlLastTime - 1800000;
            noticeTimeStamp = new Timestamp(sqlLastTime);
        }
        else if (noticeTime == 5){
            System.out.println("query------->" + "555");
//            noticeTimeStamp = new Timestamp(System.currentTimeMillis());
        }
        else {
            System.out.println("query------->" + "else0");
            noticeTimeStamp = new Timestamp(System.currentTimeMillis());
        }
        TextView todoEditView = (TextView)findViewById(R.id.editText);
        TextView positionEditView = (TextView)findViewById(R.id.position);
        TextView tipsEditView = (TextView)findViewById(R.id.tips);
        String todoValue = todoEditView.getText().toString();
        String positon = positionEditView.getText().toString();
        String tips = tipsEditView.getText().toString();
        int isEnd = 0;
        System.out.println("query------->" + noticeTimeStamp + "***" + timestamp);
        if (todoValue.equals("")){
            dialog1();
        }
        else {
            TodoList todo1 = new TodoList(todoValue, positon, tips, timestamp, noticeTimeStamp, isAllDay, isEnd);
            todoLists.add(todo1);
            mgr.add(todoLists);
            onBackPressed();
        }
    }

    /*删除一个Todo数据*/
    public void delete() {
//        TodoList todoList = (TodoList) mgr.query();
        mgr.deleteOldTodo();
    }

    /*删除所有Todo数据*/
    public void deleteAll() {
//        TodoList todoList = (TodoList) mgr.query();
        mgr.deleteAllTodo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //应用的最后一个Activity关闭时应释放DB
        mgr.closeDB();
    }

    /*重写手机的物理返回事件*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.setClass(AddActivity.this, TodoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    //获取当前日期
    private void getDate() {
        cal=Calendar.getInstance();
        year=cal.get(Calendar.YEAR);       //获取年月日时分秒
        month=cal.get(Calendar.MONTH);   //获取到的月份是从0开始计数
        day=cal.get(Calendar.DAY_OF_MONTH);
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);
        week = getWeekDay(cal);

    }

    /*获取星期几*/
    private String getWeekDay(Calendar c)
    {
//        Toast.makeText(getApplicationContext(), "getWeekDay", Toast.LENGTH_SHORT).show();
        if (c == null){
            return "周一";
        }
        if (Calendar.MONDAY == c.get(Calendar.DAY_OF_WEEK)){
            return "周一";
        }
        if (Calendar.TUESDAY == c.get(Calendar.DAY_OF_WEEK)){
            return "周二";
        }
        if (Calendar.WEDNESDAY == c.get(Calendar.DAY_OF_WEEK)){
            return "周三";
        }
        if (Calendar.THURSDAY == c.get(Calendar.DAY_OF_WEEK)){
            return "周四";
        }
        if (Calendar.FRIDAY == c.get(Calendar.DAY_OF_WEEK)){
            return "周五";
        }
        if (Calendar.SATURDAY == c.get(Calendar.DAY_OF_WEEK)){
            return "周六";
        }
        if (Calendar.SUNDAY == c.get(Calendar.DAY_OF_WEEK)){
            return "周日";
        }
        return "周一";
    }

    public String format_conver(int s){//该方法为了输出一位数时保证前面加一个0，使之与实现十位数对齐，比如时间是12：5，使用该方法后输出为12：05
        return s>=10?""+s:"0"+s;
    }


    /*弹出提醒*/
    public void showSingleChoice() {
        final String[] items = new String[]{"无通知","在活动开始时","提前15分钟","提前30分钟","自定义"};
        new AlertDialog.Builder(this)
//            .setTitle("列表框")
            .setItems(new String[] {"无通知","在活动开始时","提前15分钟","提前30分钟","自定义"}, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    switch (which) {
                        case 0:
                            notice.setText(items[0]);
                            noticeTime = 1;
                            break;
                        case 1:
                            notice.setText(items[1]);
                            noticeTime = 2;
                            break;
                        case 2:
                            notice.setText(items[2]);
                            noticeTime = 3;
                            break;
                        case 3:
                            notice.setText(items[3]);
                            noticeTime = 4;
                            break;
                        case 4:
                            notice.setText(items[4]);
                            noticeTime = 5;
                            showCustom();
                            break;
                    }
                }
            })
            .show();
    }

    /*弹出自定义框*/
    public void showCustom(){
        DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker arg0, int year, int month, int day) {
                TimePickerDialog.OnTimeSetListener time_callback = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker arg0, int arg1, int arg2) {//arg1表示小时，arg2表示分钟
                        notice.setText(notice.getText() + String.format("%s:%s", arg1, format_conver(arg2)));
                        zhour = arg1;
                        zminute = arg2;
                    }
                };
                zyear = year;
                zmonth = month;
                zday = day;
                notice.setText(year+"年"+(month+1)+"月"+day+"日  ");
                TimePickerDialog timePicker=new TimePickerDialog(AddActivity.this, 0, time_callback, hour, minute, true);
                timePicker.show();
            }
        };
        DatePickerDialog dialog=new DatePickerDialog(AddActivity.this, 0,listener,year,month,day);//后边三个参数为显示dialog时默认的日期，月份从0开始，0-11对应1-12个月
        dialog.show();

//        String timeStr = zyear[0] +"-"+ zmonth[0] +"-"+ zday[0] +" "+ zhour[0] +":"+ zminute[0] +":00";
    }

}
