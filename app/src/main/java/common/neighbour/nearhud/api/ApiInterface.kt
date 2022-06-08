package common.neighbour.nearhud.api

import common.neighbour.nearhud.repositories.constance.AppConstance
import common.neighbour.nearhud.retrofit.model.comment.PostCommentReplyResponse
import common.neighbour.nearhud.retrofit.model.comment.PostCommentResponse
import common.neighbour.nearhud.retrofit.model.contact_share.ReferNumberListResponse
import common.neighbour.nearhud.retrofit.model.contact_share.ReferNumberResponse
import common.neighbour.nearhud.retrofit.model.group.GroupInfoResponse
import common.neighbour.nearhud.retrofit.model.group.LatLngResponse
import common.neighbour.nearhud.retrofit.model.login.OtpResponse
import common.neighbour.nearhud.retrofit.model.neighbour.NeighbourResponse
import common.neighbour.nearhud.retrofit.model.post.CreatePostResponse
import common.neighbour.nearhud.retrofit.model.post.PostResponse
import common.neighbour.nearhud.retrofit.model.post.vote.VoteResponse
import common.neighbour.nearhud.retrofit.model.register.OccupationResponse
import common.neighbour.nearhud.retrofit.model.register.RegisterResponse
import common.neighbour.nearhud.retrofit.model.state.StateResponse
import common.neighbour.nearhud.retrofit.model.userprofile.UserProfileResponse
import common.neighbour.nearhud.retrofit.model.userprofile.HelpResponse
import common.neighbour.nearhud.retrofit.model.welcome.LaunchResponse
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import retrofit2.http.*
import java.io.Serializable
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

interface ApiInterface {


    @GET("getpostofstate")
    suspend fun getUserPost(@Body hashMap: HashMap<String,String>):Response<PostResponse>

//    @GET("getpostofstate")
//    suspend fun     getHomePosts(
//        @Body par: Map<String, String>
//    ) : Response<PostResponse>

//    @GET(AppConstance.STATE_POST)
//    suspend fun getHomePosts(@Query("groupId") grpId: String,
//                          @Query("userId") userId: String,
//                          @Query("count") count: String):
//            Response<PostResponse>



    @POST(AppConstance.STATE_POST)
    suspend fun getHomePosts(
        @Body params:HashMap<String,String>
    ):Response<PostResponse>

    @POST(AppConstance.STATE_POST)
    suspend fun getHomePost(
        @Header("Authorization") header: String,
        @Body requestBody: RequestBody?
    ): Response<PostResponse>

    @POST(AppConstance.GROUP_POST)
    suspend fun getGroupPost(
        @Header("Authorization") header: String,
        @Body requestBody: RequestBody?
    ): Response<PostResponse>

    @POST(AppConstance.CREATE_POST)
    suspend fun createPost(
        @Body params: HashMap<String, String>
    ):Response<CreatePostResponse>

    @POST(AppConstance.UPDATE_POST)
    suspend fun updatePost(
        @Body params: HashMap<String, String>
    ):Response<RegisterResponse>

    @POST(AppConstance.INSERT_USER)
    suspend fun insertUserAPI(
        @Body params:HashMap<String,String>
    ):Response<RegisterResponse>

    @POST(AppConstance.UPDATE_USER)
    suspend fun updateUserAPI(
        @Body params:HashMap<String,String>
    ):Response<RegisterResponse>

    @POST(AppConstance.GROUP_POST)
    suspend fun getNeighbourPost(
        @Body params:HashMap<String,String>):
            Response<PostResponse>

    @POST(AppConstance.SORT_CONTACT_LIST)
    suspend fun sortContactList(
        @Body params: HashMap<String, Serializable>
    ): Response<ReferNumberListResponse>

    @POST(AppConstance.REFER_NUMBER)
    suspend fun referNumber(
        @Body params: HashMap<String, String>
    ): Response<ReferNumberResponse>

    @GET(AppConstance.GET_COMMENT)
    suspend fun getPostComment(@Query(AppConstance.POST_ID) postId: String):
            Response<PostCommentResponse>

    @GET(AppConstance.GET_REPLY)
    suspend fun getCommentReply(@Query(AppConstance.COMMENT_ID) commentId: String):
            Response<PostCommentReplyResponse>

    @GET(AppConstance.MY_NEIGHBOUR)
    suspend fun getMyNeighbour(@Query(AppConstance.USER_ID) userId: String):
            Response<NeighbourResponse>

    @GET(AppConstance.OCCUPATION_LIST)
    suspend fun getOccupation():
            Response<OccupationResponse>

    @GET(AppConstance.HELP)
    suspend fun getHelp():
            Response<HelpResponse>

    @GET(AppConstance.LAUNCH)
    suspend fun getLaunchScreen():
            Response<LaunchResponse>

    @GET(AppConstance.FIND_GROUP)
    suspend fun getGroupAPI(@Query(AppConstance.LATITUDE) latitude: String,
                            @Query(AppConstance.LONGITUDE)longitude:String):
            Response<GroupInfoResponse>


    @POST(AppConstance.LAT_LNG_ADDRESS)
    suspend fun getLatLngFromAddress(
    @Body params:HashMap<String,String>):
            Response<LatLngResponse>

    @GET(AppConstance.FIND_NEAR_GROUP)
    suspend fun stateAPI(@Query(AppConstance.GROUP_ID) grpId: String):
            Response<StateResponse>

    @GET(AppConstance.USER_PROFILE)
    suspend fun GetProfile(@Query(AppConstance.USER_ID) uId: String):
            Response<UserProfileResponse>

    @GET(AppConstance.MY_POST)
    suspend fun getMyPost(@Query(AppConstance.USER_ID) userId: String):
            Response<PostResponse>

    @GET(AppConstance.SEND_OTP)
    suspend fun sendOtp(@Query(AppConstance.MOB_NO) mob: String):
            Response<OtpResponse>

    @GET(AppConstance.UP_VOTE)
    suspend fun UpVoteAPI(@Query(AppConstance.POST_ID) postId: String,
                            @Query(AppConstance.USER_ID) userId:String):
            Response<VoteResponse>

    @GET(AppConstance.DOWN_VOTE)
    suspend fun DownVoteAPI(@Query(AppConstance.POST_ID) postId: String,
                            @Query(AppConstance.USER_ID) userId:String):
            Response<VoteResponse>


    companion object {
        val _USER_ID = "u123"
        val API_PICSUM = "API_PICSUM"
        val API_SIGNUP = "API_SIGNUP"
        val API_CHECK_MOBILE = "API_CHECK_MOBILE"
        val API_GET_TOKEN = "API_GET_TOKEN"
        val API_CHECK_USERNAME = "API_CHECK_USERNAME"
        val API_SOCIAL_SIGNUP = "API_SOCIAL_SIGNUP"
        val UPDATE_PROFILE = "UPDATE_PROFILE"
        val UPLOAD_MEDIA = "UPLOAD_MEDIA"
        val API_PLACE = "API_PLACE"
        val API_POST_TEXT = "API_POST_TEXT"
        val API_SEARCH_HASH_TAGS = "API_SEARCH_HASH_TAGS"
        val API_GET_POST = "API_GET_POST"
        val API_SAVE_VIDEO = "API_SAVE_VIDEO"
        val API_LIKE_POST = "API_LIKE_POST"
        val API_DELETE_POST = "API_DELETE_POST"
        val API_GET_USER_BY_MOBILE = "GET_USER_BY_MOBILE"


        private const val BASE_URL = "http://35.168.199.26:3000/"
//        private const val BASE_URL_ON_RECORD = "http://18.206.137.111:3000/"
//        private const val BASE_URL_PLACES_API = "https://maps.googleapis.com/maps/api/place/textsearch/"
//        const val PLACES_API_KEY ="AIzaSyDHF83-FNTa5uM_1xUMTlN-DjCi87NLCOc"

        fun createNearhud(token: String?): ApiInterface {
            val httpClient = OkHttpClient().newBuilder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(AuthorizationInterceptor(token!!))
                .build()


            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ApiInterface::class.java)
        }

//        fun createPlacesApi(token: String?): ApiInterface {
//            val httpClient = OkHttpClient().newBuilder()
//                .connectTimeout(60, TimeUnit.SECONDS)
//                .readTimeout(60, TimeUnit.SECONDS)
//                .writeTimeout(60, TimeUnit.SECONDS)
//                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
//              //  .addInterceptor(AuthorizationInterceptor(token!!))
//                .build()
//
//            return Retrofit.Builder()
//                .baseUrl(BASE_URL_PLACES_API)
//                .client(httpClient)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build().create(ApiInterface::class.java)
//        }
//
//        fun createToken(): ApiInterface {
//            return Retrofit.Builder()
//                .baseUrl(BASE_URL_ON_RECORD)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build().create(ApiInterface::class.java)
//        }

    }
}