package cc.yfree.yangf.everyday;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/11/8.
 */

/*全局变量*/
public class Global {
    public static JSONObject weatherData;  // 类级变量
    public static JSONObject apiResult1;
    public static int tem;
    public static int Tdre, Tzw;
    public static String skycon_ ,code0, code1, code2;
    public static String PM25_;
    public static String FX;
    public static String Tdescription;
    public static String Ndescription;
    public static JSONObject Tsun;
    public static String sunRise;
    public static String sunDown;
    public static String Tsky;
    public static JSONArray jsonArray1;
    public static JSONObject sky, dre, zwx, alert = null, codeObject;
    public static int FJ;
    public static int SD;
    public static String codeString;

    public static JSONArray nextRain;
    public static String nextRainTime = "";
    public static String nextRainResult = "";
    public static String nextRainTips = "";
    public static String nextRainDirection = "";
    public static int nextRainHour = 0;

}
