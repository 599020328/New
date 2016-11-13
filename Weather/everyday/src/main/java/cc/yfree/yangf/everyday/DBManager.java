package cc.yfree.yangf.everyday;

/**
 * Created by Administrator on 2016/11/9.
 */

import java.sql.Time;
import java.util.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {
    private DBHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        helper = new DBHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    /**
     * add todoLists
     * @param todoLists
     */
    public void add(List<TodoList> todoLists) {
        db.beginTransaction();  //开始事务
        try {
            for (TodoList todoList : todoLists) {
                db.execSQL("INSERT INTO TodoList VALUES(null, ?, ?, ?, ?, ?, ?, ?)", new Object[]{todoList.todo, todoList.position, todoList.tips, todoList.time, todoList.notice, todoList.isAllDay, todoList.isEnd});
            }
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

//    /**
//     * update person's age
//     * @param person
//     */
//    public void updateAge(Person person) {
//        ContentValues cv = new ContentValues();
//        cv.put("age", person.age);
//        db.update("person", cv, "name = ?", new String[]{person.name});
//    }

    /*删除最近一条*/
    public void deleteOldTodo() {
        TodoList todoList = queryTodo();
        db.delete("TodoList", "_id = ? ", new String[]{String.valueOf(todoList._id)});
    }

    /*删除所有*/
    public void deleteAllTodo() {
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
            TodoList todoList = queryTodo();
            db.delete("TodoList", "_id = ? ", new String[]{String.valueOf(todoList._id)});
        }
        c.close();
    }

    /*查询第一条数据*/
    public TodoList queryTodo() {
        TodoList todoList = new TodoList();
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
            todoList._id = c.getInt(c.getColumnIndex("_id"));
            todoList.todo = c.getString(c.getColumnIndex("todo"));
//            todoList.time = c.getInt(c.getColumnIndex("time"));
            todoList.position = c.getString(c.getColumnIndex("position"));
//            System.out.println("query------->" + "Todo："+todoList.todo+" "+"时间："+todoList.time);
        }
//            todoList.add(todoList);
        c.close();
        return todoList;
    }

    /*查询未完成Todo数据*/
    public List<TodoList> queryNoTodo() throws ParseException {
        ArrayList<TodoList> todoLists = new ArrayList<TodoList>();
        Cursor c = queryNoComTodo();
        while (c.moveToNext()) {
            TodoList todoList = new TodoList();
            todoList._id = c.getInt(c.getColumnIndex("_id"));
            todoList.todo = c.getString(c.getColumnIndex("todo"));
            String timeStr = c.getString(c.getColumnIndex("time"));
//            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date date = (Date) format.parse(timeStr);
            todoList.time = Timestamp.valueOf(timeStr);
            todoList.position = c.getString(c.getColumnIndex("position"));
            todoList.tips = c.getString(c.getColumnIndex("tips"));
            todoList.notice = new Timestamp(c.getInt(c.getColumnIndex("notice")));
            todoList.isAllDay = c.getInt(c.getColumnIndex("isAllDay"));
            todoList.isEnd = c.getInt(c.getColumnIndex("isEnd"));
            System.out.println("query------->" + "Todo："+todoList.todo+" "+"时间："+todoList.time);
            todoLists.add(todoList);
        }
        c.close();
        return todoLists;
    }

    /*查询完成Todo数据*/
    public List<TodoList> queryEndTodo() throws ParseException {
        ArrayList<TodoList> todoLists = new ArrayList<TodoList>();
        Cursor c = queryComTodo();
        while (c.moveToNext()) {
            TodoList todoList = new TodoList();
            todoList._id = c.getInt(c.getColumnIndex("_id"));
            todoList.todo = c.getString(c.getColumnIndex("todo"));
            String timeStr = c.getString(c.getColumnIndex("time"));
            todoList.time = Timestamp.valueOf(timeStr);
            todoList.position = c.getString(c.getColumnIndex("position"));
            todoList.tips = c.getString(c.getColumnIndex("tips"));
            todoList.notice = new Timestamp(c.getInt(c.getColumnIndex("notice")));
            todoList.isAllDay = c.getInt(c.getColumnIndex("isAllDay"));
            todoList.isEnd = c.getInt(c.getColumnIndex("isEnd"));
            System.out.println("query------->" + "Todo："+todoList.todo+" "+"时间："+todoList.time);
            todoLists.add(todoList);
        }
        c.close();
        return todoLists;
    }


    /**
     * query all persons, return list
     * @return List<Person>
     */
    public List<TodoList> query() {
        ArrayList<TodoList> todoLists = new ArrayList<TodoList>();
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
            TodoList todoList = new TodoList();
            todoList._id = c.getInt(c.getColumnIndex("_id"));
            todoList.todo = c.getString(c.getColumnIndex("todo"));
//            todoList.time = c.getInt(c.getColumnIndex("time"));
            todoList.position = c.getString(c.getColumnIndex("position"));
//            System.out.println("query------->" + "Todo："+todoList.todo+" "+"时间："+todoList.time);
        }
//            todoList.add(todoList);
        c.close();
        return todoLists;
    }

    /*打印测试数据*/
    public void Print() throws ParseException {
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
            TodoList todoList = new TodoList();

            String str,str1;
            SimpleDateFormat format;
            Date date = null, date1 = null;
            str = c.getString(c.getColumnIndex("time"));
            str1 = c.getString(c.getColumnIndex("notice"));
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = (Date) format.parse(str);
            date1 = (Date) format.parse(str1);
            Date time = date, time1 = date1;

            todoList._id = c.getInt(c.getColumnIndex("_id"));
            todoList.todo = c.getString(c.getColumnIndex("todo"));
            todoList.position = c.getString(c.getColumnIndex("position"));
            todoList.tips = c.getString(c.getColumnIndex("tips"));
            todoList.isAllDay = c.getInt(c.getColumnIndex("isAllDay"));
            todoList.isEnd = c.getInt(c.getColumnIndex("isEnd"));
            System.out.println("query------->" + "id:"+todoList._id+"//Todo："+todoList.todo+"***时间："+time+"***提醒："+time1+"***位置："+todoList.position+"***tips"+todoList.tips+"***AllDay:"+todoList.isAllDay+"***isEnd"+todoList.isEnd);
        }
//            todoList.add(todoList);
        c.close();
    }

    /*打印测试数据*/
    public void PrintN() throws ParseException {
        Cursor c = queryNoComTodo();
        while (c.moveToNext()) {
            TodoList todoList = new TodoList();

            String str,str1;
            SimpleDateFormat format;
            Date date = null, date1 = null;
            str = c.getString(c.getColumnIndex("time"));
            str1 = c.getString(c.getColumnIndex("notice"));
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = (Date) format.parse(str);
            date1 = (Date) format.parse(str1);
            Date time = date, time1 = date1;

            todoList._id = c.getInt(c.getColumnIndex("_id"));
            todoList.todo = c.getString(c.getColumnIndex("todo"));
            todoList.position = c.getString(c.getColumnIndex("position"));
            todoList.tips = c.getString(c.getColumnIndex("tips"));
            todoList.isAllDay = c.getInt(c.getColumnIndex("isAllDay"));
            todoList.isEnd = c.getInt(c.getColumnIndex("isEnd"));
            System.out.println("query------->" + "id:"+todoList._id+"//Todo："+todoList.todo+"***时间："+str+"***提醒："+time1+"***位置："+todoList.position+"***tips"+todoList.tips+"***AllDay:"+todoList.isAllDay+"***isEnd"+todoList.isEnd);
        }
//            todoList.add(todoList);
        c.close();
    }


    /*查询所有数据*/
    public Cursor queryTheCursor() {
        Cursor c = db.rawQuery("SELECT * FROM TodoList", null);
        return c;
    }

    /*查询未完成todo*/
    public Cursor queryNoComTodo() {
        Timestamp nowTime = new Timestamp(System.currentTimeMillis());
        Cursor c = db.rawQuery("SELECT * FROM TodoList WHERE isEnd = 0 AND time > '" + nowTime +"' ORDER BY time", null);
        return c;
    }

    /*查询已完成todo*/
    public Cursor queryComTodo() {
        Timestamp nowTime = new Timestamp(System.currentTimeMillis());
        Cursor c = db.rawQuery("SELECT * FROM TodoList WHERE isEnd = 1 OR time < '" + nowTime +"' ORDER BY time", null);
        return c;
    }

    /*完成*/
    public void End(int id){
        db.execSQL("update TodoList set isEnd =? where _id =?", new Object[]{1, id});
    }

    /*更新延后数据*/
    public void late(int id, Timestamp newTime){
        ContentValues cv = new ContentValues();
        cv.put("time", String.valueOf(newTime));
        db.update("TodoList", cv, "_id = ?", new String[]{id+""});
    }

    /*查询一个时间段内的未完成计划*/
    public Cursor queryNoComTodoInATime(Timestamp start, Timestamp end){
        Cursor c = db.rawQuery("SELECT * FROM TodoList WHERE isEnd = 0 AND time < '" + end +"' AND time > '" +start+"'", null);
        return c;
    }

    /*查询一个时间段内未完成Todo数据*/
    public List<TodoList> noComTodoInATime(Timestamp start, Timestamp end) throws ParseException {
        ArrayList<TodoList> todoLists = new ArrayList<TodoList>();
        Cursor c = queryNoComTodoInATime(start, end);
        while (c.moveToNext()) {
            TodoList todoList = new TodoList();
            todoList._id = c.getInt(c.getColumnIndex("_id"));
            todoList.todo = c.getString(c.getColumnIndex("todo"));
            String timeStr = c.getString(c.getColumnIndex("time"));
//            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date date = (Date) format.parse(timeStr);
            todoList.time = Timestamp.valueOf(timeStr);
            todoList.position = c.getString(c.getColumnIndex("position"));
            todoList.tips = c.getString(c.getColumnIndex("tips"));
            todoList.notice = new Timestamp(c.getInt(c.getColumnIndex("notice")));
            todoList.isAllDay = c.getInt(c.getColumnIndex("isAllDay"));
            todoList.isEnd = c.getInt(c.getColumnIndex("isEnd"));
            System.out.println("query------->" + "Todo："+todoList.todo+" "+"时间："+todoList.time);
            todoLists.add(todoList);
        }
        c.close();
        return todoLists;
    }

    /**
     * close database
     */
    public void closeDB() {
        db.close();
    }
}
