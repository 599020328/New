package cc.yfree.yangf.everyday;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.mikepenz.iconics.view.IconicsImageView;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;
import com.baidu.mapapi.SDKInitializer;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static cc.yfree.yangf.everyday.R.id.notice;

public class HomeActivity extends AppCompatActivity {

    private String TAG = HomeActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private BottomNavigationBar bottomNavigationBar;
    public static Activity HomeActivity;
    private Intent mintent;
    public enum State {
        EXPANDED,
        COLLAPSED,
        IDLE
    }
    private int year,month,day,hour,minute,noticeTime = 0;
    private int zyear,zmonth,zday,zhour,zminute,isAllDay = 0;
    private Calendar cal;
    TodoList First, Second;
    DBManager mgr;
    int i = 0;
    List<TodoList> noCamTodo;
    TextView todo,time;
    String ztimeStr = null;
    int wtf = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //图标支持
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
        super.onCreate(savedInstanceState);
        HomeActivity = this;
        //设置动画效果开启
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        SDKInitializer.initialize(getApplicationContext());

        /*获取第一个未完成的任务*/
        mgr = new DBManager(this);
        noCamTodo = null;
        try {
            noCamTodo = mgr.queryNoTodo();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.drawlayout_home);
        getDate();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        View carAndBus = findViewById(R.id.takeCar);
        View Analyse = findViewById(R.id.analysis);
        View nextRain = findViewById(R.id.nextRain);
        View bestWeather = findViewById(R.id.bestWeather);
        todo = (TextView)findViewById(R.id.todo);
        time = (TextView)findViewById(R.id.time);
        Button end = (Button)findViewById(R.id.end);
        final Button late = (Button)findViewById(R.id.late);
        String timeString = null;
        if (noCamTodo.size() > 0){
            First = noCamTodo.get(i);
            wtf = First._id;
            todo.setText(First.todo);
            if (First.isAllDay == 1){
                timeString = new java.text.SimpleDateFormat("yyyy年MM月dd日").format(new java.util.Date(First.time.getTime()));
            }
            else {
                timeString = new java.text.SimpleDateFormat("yyyy年MM月dd日  HH:mm").format(new java.util.Date(First.time.getTime()));
            }
            time.setText(timeString);

            /*完成的监听*/
            end.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    i++;
                    endTodo(First._id);
                }
            });

            /*延后的监听*/
            late.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    i++;
                    showCustom();
                }
            });
        }
        else {
            todo.setText("一个计划也没有");
            time.setText("快去添加计划吧！");
        }


        final boolean[] isOpen = {true};
        setSupportActionBar(toolbar);


        //设置动画效果
        Transition explode = TransitionInflater.from(this).inflateTransition(R.transition.slide_start);
        getWindow().setEnterTransition(explode);
//        getWindow().setReenterTransition(explode);
//        getWindow().setExitTransition(explode);


        /*抽屉开关*/
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        CircularImageView mCircularImageView = (CircularImageView) findViewById(R.id.headiamage);
        mCircularImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

         /*Bottom bar*/
        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        //设置模式和风格
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE);
        bottomNavigationBar
                .setActiveColor(R.color.white)  //导航栏背景颜色  （RIPPLE模式）
                .setInActiveColor(R.color.gray)      //未被选中的标签的颜色
                .setBarBackgroundColor(R.color.blue); //选中的标签的颜色（RIPPLE模式）

        //设置条目
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.ic_menu_home, "主页"))
                .addItem(new BottomNavigationItem(R.drawable.ic_menu_weather, "天气"))//.setActiveColorResource(R.color.color_white))//散开的波纹效果
                .addItem(new BottomNavigationItem(R.drawable.ic_menu_todo, "Todo"))
                .addItem(new BottomNavigationItem(R.drawable.ic_menu_map, "地图"))
                .setFirstSelectedPosition(0)
                .initialise();


        /*“快速上车”监听*/
        carAndBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mintent = new Intent();
                mintent.setClass(HomeActivity.this, CarActivity.class);
                mintent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(mintent);
            }
        });

         /*“一键分析”监听*/
        Analyse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mintent = new Intent();
                mintent.setClass(HomeActivity.this, RoutePlanningActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("activityName", "HomeActivity");
                mintent.putExtras(bundle);
                mintent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(mintent);
            }
        });

        /*“下一场雨”监听*/
        nextRain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mintent = new Intent();
                mintent.putExtra("id", 1);
                mintent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                mintent.setClass(HomeActivity.this, WeatherActivity_.class);
//                startActivity(mintent, ActivityOptions.makeSceneTransitionAnimation(HomeActivity).toBundle());
                startActivity(mintent);
            }
        });

        /*“最佳天气”监听*/
        bestWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mintent = new Intent();
                mintent.putExtra("id", 2);
                mintent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                mintent.setClass(HomeActivity.this, WeatherActivity_.class);
//                startActivity(mintent, ActivityOptions.makeSceneTransitionAnimation(HomeActivity).toBundle());
                startActivity(mintent);
            }
        });

        /*快速记录添加*/
        IconicsImageView mAdd = (IconicsImageView) findViewById(R.id.addItem);
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSingleChoice();
            }
        });


        //底部导航栏监听器
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener(){
            public void onTabSelected(int position) {
                Log.d(TAG, "onTabSelected() called with: " + "position = [" + position + "]");
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        Intent intent = new Intent();
                        //Intent传递参数
                        //intent.putExtra("testIntent", "123");
                        intent.setClass(HomeActivity.this, WeatherActivity_.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        HomeActivity.this.overridePendingTransition(0, 0);
                        break;
                    case 2:
                        Intent intent2 = new Intent();
                        intent2.setClass(HomeActivity.this, TodoActivity.class);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent2);
                        HomeActivity.this.overridePendingTransition(0, 0);
                        break;
                    case 3:
                        Intent intent3 = new Intent();
                        intent3.setClass(HomeActivity.this, MapActivity.class);
//                        intent3.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                        intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent3);
                        break;
                    default:
                        break;
                }

            }
            @Override
            public void onTabUnselected(int position) {
            }
            @Override
            public void onTabReselected(int position) {
            }
        });

        /*首页cardview图片相关*/
        ImageView nextTodo_img = (ImageView)findViewById(R.id.nextTodo_img);
        Picasso.with(this)
                .load(R.drawable.sunny)
                .fit()
                .into(nextTodo_img);

//        ImageView festival     = (ImageView)findViewById(R.id.festival);
//        Picasso.with(this)
//                .load(R.drawable.zq)
//                .fit()
//                .into(festival);

        /*监听可伸缩导航栏*/
//        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeLayout);
        AppBarLayout mAppBarLayout = (AppBarLayout)findViewById(R.id.app_bar);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                Log.d("STATE", state.name());
                if( state == State.EXPANDED ) {
                    //开启状态
                    isOpen[0] = true;
//                    swipeRefreshLayout.setEnabled(true);
                }else if(state == State.COLLAPSED){
                    //关闭状态
                    isOpen[0] = false;
//                    swipeRefreshLayout.setEnabled(false);
                }else {
                    //中间状态
                    isOpen[0] = false;
//                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });

        /*设置home-list快速记录*/
        //RecyclerView的初始化
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view_list);
        //创建现行LinearLayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        //设置LayoutMananger
        mRecyclerView.setLayoutManager(mLayoutManager);
        //设置item的动画，可以不设置
        //mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        MyAdapter adapter = new MyAdapter(initDate());
        //设置Adapter
        mRecyclerView.setAdapter(adapter);
    }


//    @Override
//    protected void onPause() {
//        HomeActivity.finish();
//        super.onPause();
//    }

    /*弹出输入框*/
    public void showSingleChoice() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.home_dialog,
                (ViewGroup) findViewById(R.id.inputDialog));

        Dialog mDialog = new AlertDialog.Builder(this)
                .setTitle("随便记点什么吧~")
                .setView(layout)
                .setPositiveButton("保存", null)
                .setNegativeButton("取消", null).show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue);
        if (swipeRefreshLayout.isRefreshing()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    swipeRefreshLayout.setRefreshing(false);
                    swipeRefreshLayout.setEnabled(false);
                }
            }, 3000);
        }
        bottomNavigationBar.selectTab(0);
    }

    /*设置再按一次退出程序*/
    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /*监听伸缩导航栏的状态*/
    public abstract class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener {
        private State mCurrentState = State.IDLE;

        @Override
        public final void onOffsetChanged(AppBarLayout appBarLayout, int i) {
            if (i == 0) {
                if (mCurrentState != State.EXPANDED) {
                    onStateChanged(appBarLayout, State.EXPANDED);
                }
                mCurrentState = State.EXPANDED;
            } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
                if (mCurrentState != State.COLLAPSED) {
                    onStateChanged(appBarLayout, State.COLLAPSED);
                }
                mCurrentState = State.COLLAPSED;
            } else {
                if (mCurrentState != State.IDLE) {
                    onStateChanged(appBarLayout, State.IDLE);
                }
                mCurrentState = State.IDLE;
            }
        }

        public abstract void onStateChanged(AppBarLayout appBarLayout, State state);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    /*测试数据*/
    private List<String> initDate(){
        List<String> list = new ArrayList<>();
        for(int i=0;i<5;i++){
            list.add("测试用例：" + i);
        }
        return list;
    }

    class MyAdapter  extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
        private List<String> items;

        public MyAdapter(List<String> items) {
            this.items = items;
        }

        /**
         * 创建ViewHolder的布局
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_list,parent,false);
            return new ViewHolder(view);
        }

        /**
         * 通过ViewHolder将数据绑定到界面上进行显示
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
            holder.mTextView.setText(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public TextView mTextView;
            public ViewHolder(View itemView) {
                super(itemView);
                mTextView = (TextView) itemView.findViewById(R.id.textView);
            }
        }
    }

    //获取当前日期
    private void getDate() {
        cal= Calendar.getInstance();
        year=cal.get(Calendar.YEAR);       //获取年月日时分秒
        month=cal.get(Calendar.MONTH);   //获取到的月份是从0开始计数
        day=cal.get(Calendar.DAY_OF_MONTH);
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);
    }

    /*弹出自定义框*/
    public void showCustom(){
        DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker arg0, int year, int month, int day) {
                TimePickerDialog.OnTimeSetListener time_callback = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker arg0, int arg1, int arg2) {//arg1表示小时，arg2表示分钟
                        zhour = arg1;
                        zminute = arg2;
                        dialog1();
                    }
                };
                zyear = year;
                zmonth = month;
                zday = day;
                TimePickerDialog timePicker=new TimePickerDialog(HomeActivity.this, 0, time_callback, hour, minute, true);
                timePicker.show();
            }
        };
        DatePickerDialog dialog=new DatePickerDialog(HomeActivity.this, 0,listener,year,month,day);//后边三个参数为显示dialog时默认的日期，月份从0开始，0-11对应1-12个月
        dialog.show();
    }

    /*确认延后修改*/
    private void dialog1(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("提示"); //设置标题
        builder.setMessage("确认修改吗?"); //设置内容
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CardView cardView = (CardView) findViewById(R.id.home_card);
                WindowManager wm = getParent().getWindowManager();
                int width = wm.getDefaultDisplay().getWidth();
                float cardViewX = cardView.getX()-convertDipOrPx(getParent(),8);
                ObjectAnimator translationUp0 = ObjectAnimator.ofFloat(cardView, "alpha", 1f, 0f).setDuration(100);
                ObjectAnimator translationUp1 = ObjectAnimator.ofFloat(cardView, "alpha", 0f, 1f).setDuration(0);
                ObjectAnimator translationUp2 = ObjectAnimator.ofFloat(cardView, "translationX", width, cardViewX).setDuration(700);
                AnimatorSet set0=new AnimatorSet();
                AnimatorSet set1=new AnimatorSet();
                set1.playTogether(translationUp1,translationUp2);
                set0.playSequentially(translationUp0,set1);
                set0.start();

                translationUp0.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        latet(wtf);
                    }
                });
                dialog.dismiss(); //关闭dialog
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //参数都设置完成了，创建并显示出来
        builder.create().show();
    }

    /*延后修改*/
    public void latet(int id){
        String ztimeStr = zyear+"-"+(zmonth+1)+"-"+zday+" "+zhour+":"+zminute+":00";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT+8"));// 中国北京时间，东八区
        System.out.println("query------->0:" + ztimeStr);
        Date zdateTime = null;
        try {
            zdateTime = format.parse(ztimeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Timestamp noticeTimeStamp = new Timestamp(zdateTime.getTime());
        System.out.println("query------->GG0:" + noticeTimeStamp);
        mgr.late(id, noticeTimeStamp);
        refresh();
    }

    public static int convertDipOrPx(Context context, int dip) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dip*scale + 0.5f*(dip>=0?1:-1));
    }

    /*完成任务*/
    public void endTodo(int id){
        mgr.End(id);
        CardView cardView = (CardView) findViewById(R.id.home_card);
        /*设置动画效果*/
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        float cardViewX = cardView.getX()-convertDipOrPx(this,8);
//        ObjectAnimator translationUp = ObjectAnimator.ofFloat(cardView, "translationX", cardView.getX(), -width);
        ObjectAnimator translationUp0 = ObjectAnimator.ofFloat(cardView, "alpha", 1f, 0f).setDuration(100);
        ObjectAnimator translationUp1 = ObjectAnimator.ofFloat(cardView, "alpha", 0f, 1f).setDuration(0);
        ObjectAnimator translationUp2 = ObjectAnimator.ofFloat(cardView, "translationX", width, cardViewX).setDuration(700);
//        ObjectAnimator translationUp1 = ObjectAnimator.ofFloat(cardView, "alpha", 0f, 1f);
        AnimatorSet set0=new AnimatorSet();
        AnimatorSet set1=new AnimatorSet();
        set1.playTogether(translationUp1,translationUp2);
        set0.playSequentially(translationUp0,set1);
        set0.start();

        translationUp0.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                refresh();
            }
        });
    }

    /*更新View*/
    public void refresh(){
        System.out.println("query------->GG0:" + "刷新函数执行了吗？");
        if (i < noCamTodo.size()){
            First = noCamTodo.get(i);
            wtf = First._id;
            todo.setText(First.todo);
            String timeString;
            if (First.isAllDay == 1){
                timeString = new java.text.SimpleDateFormat("yyyy年MM月dd日").format(new java.util.Date(First.time.getTime()));
            }
            else {
                timeString = new java.text.SimpleDateFormat("yyyy年MM月dd日  HH:mm").format(new java.util.Date(First.time.getTime()));
            }
            time.setText(timeString);
        }
        else {
            todo.setText("一个计划也没有");
            time.setText("快去添加计划吧！");
        }
    }

    /*更新延后View*/
    public void refreshForLate(){
        i = 0;
        try {
            noCamTodo = mgr.queryNoTodo();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (noCamTodo.size() > 0){
            First = noCamTodo.get(i);
            wtf = First._id;
            todo.setText(First.todo);
            String timeString;
            if (First.isAllDay == 1){
                timeString = new java.text.SimpleDateFormat("yyyy年MM月dd日").format(new java.util.Date(First.time.getTime()));
            }
            else {
                timeString = new java.text.SimpleDateFormat("yyyy年MM月dd日  HH:mm").format(new java.util.Date(First.time.getTime()));
            }
            time.setText(timeString);
        }
        else {
            todo.setText("一个计划也没有");
            time.setText("快去添加计划吧！");
        }
    }
}
