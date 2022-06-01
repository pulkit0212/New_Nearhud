package common.neighbour.nearhud.core.extensions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeConverter {
    static String dateFormater(String date)
    {
        String ans = "";
        ans += date.substring(0,date.indexOf('T'));
        ans += " "+date.substring(date.indexOf('T')+1,date.indexOf("."));
        return ans;
    }


    public static String timeDifference(String dateStart)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateStart = dateFormater(dateStart);
        Date crr = new Date();
        String currentDate = formatter.format(crr);
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = formatter.parse(dateStart);
            d2 = formatter.parse(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long difference_In_Time = d2.getTime() - d1.getTime();
        String ans = "Just a time ago";
        long difference_In_Years = TimeUnit.MILLISECONDS.toDays(difference_In_Time)/ 365l;
        if(difference_In_Years>0)
        {
            ans = difference_In_Years+" year ago";
            return ans;
        }
        long difference_In_Days = TimeUnit.MILLISECONDS.toDays(difference_In_Time)% 365;
        if(difference_In_Days>0)
        {
            ans = difference_In_Days+" days ago";
            return ans;
        }
        long difference_In_Hours = TimeUnit.MILLISECONDS.toHours(difference_In_Time)% 24;
        if(difference_In_Hours>0)
        {
            ans = difference_In_Hours+" hours ago";
            return ans;
        }
        long difference_In_Minutes = TimeUnit.MILLISECONDS.toMinutes(difference_In_Time) % 60;
        if(difference_In_Minutes>0)
        {
            ans = difference_In_Minutes+" minutes ago";
            return ans;
        }
        long difference_In_Seconds = TimeUnit.MILLISECONDS.toSeconds(difference_In_Time)% 60;
        if(difference_In_Seconds>0){
            ans = difference_In_Seconds+" seconds ago";
            return ans;
        }
        return ans;
    }
    public static void main(String[] args)
    {
        String dateStart = "2022-01-22T18:09:05.000Z";
        System.out.println(timeDifference(dateStart));
    }
}
