package common.neighbour.nearhud.common;


import java.util.ArrayList;

import common.neighbour.nearhud.retrofit.model.group.GroupInfo;
import common.neighbour.nearhud.retrofit.model.post.CommentData;
import common.neighbour.nearhud.retrofit.model.post.Data;
import common.neighbour.nearhud.retrofit.model.post.PostData;
import common.neighbour.nearhud.retrofit.model.token.ReferedData;
import common.neighbour.nearhud.retrofit.model.welcome.Launch;

public class Common {
    public static ArrayList<CommentData> allComments = new ArrayList<>();
    public static ArrayList<Data> post = new ArrayList<>();
    public static ArrayList<Launch> launchData = new ArrayList<>();
    public static String userIdForComment, postIdForComment, commentIdForReply,commentText;

    public static String email,address,zip;
    public static String postAddress="";
    public static ArrayList<String> listState = new ArrayList<>();
    public static ArrayList<String> listGroupLatLng = new ArrayList<>();
    public static GroupInfo savedGrpInfo;
    public static String grpID;
    public static Data updatePostData;
    public static Boolean isPostUpdate = false;
    public static Boolean isPostMediaChange = false;

    public static String userNumber,userLatlng="";
    public static Double sLat,sLng;
    public static Double sCLat,sCLng;

    public static int mapFlag = 0;
    public static int MY_POST_POSITION = 0;

    // save direct refer user data
    public static ArrayList<ReferedData> referedData = new ArrayList<>();
    public static String referGrpID;
    public static Boolean isRefer = false;
    public static String referGrpName;
}
