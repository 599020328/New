package cc.yfree.yangf.everyday;

import java.sql.Timestamp;

/**
 * Created by Administrator on 2016/11/9.
 */
class TodoList {
    public int _id;
    public String todo, tips, position;
    public int isAllDay,isEnd;
    public Timestamp time; public Timestamp notice;

    public TodoList() {
    }

    public TodoList(String todo, String position, String tips, Timestamp time, Timestamp notice, int isAllDay,int isEnd) {
        this.todo = todo;
        this.position = position;
        this.tips = tips;
        this.time = time;
        this.isAllDay = isAllDay;
        this.notice = notice;
        this.isEnd = isEnd;
    }
}