package common.neighbour.nearhud.ui.home.viewmodel

import android.graphics.Bitmap
import android.location.Location
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import common.neighbour.nearhud.application.MyApplication
import common.neighbour.nearhud.application.MyApplication.Companion.repository
import common.neighbour.nearhud.base.BaseViewModel
import common.neighbour.nearhud.database.prefrence.SharedPre
import common.neighbour.nearhud.core.extensions.IMAGE_POST
import common.neighbour.nearhud.newUi.PlacesResult
import common.neighbour.nearhud.repositories.constance.AppConstance
import common.neighbour.nearhud.retrofit.model.comment.PostCommentReplyResponse
import common.neighbour.nearhud.retrofit.model.comment.PostCommentResponse
import common.neighbour.nearhud.retrofit.model.group.GroupInfoResponse
import common.neighbour.nearhud.retrofit.model.invalidtoken.InvalidToken
import common.neighbour.nearhud.retrofit.model.neighbour.NeighbourResponse
import common.neighbour.nearhud.retrofit.model.post.CreatePostResponse
import common.neighbour.nearhud.retrofit.model.post.PostResponse
import common.neighbour.nearhud.retrofit.model.post.vote.VoteResponse
import common.neighbour.nearhud.retrofit.model.register.RegisterResponse
import common.neighbour.nearhud.retrofit.model.state.PincodeResponse
import common.neighbour.nearhud.retrofit.model.state.StateResponse
import common.neighbour.nearhud.retrofit.model.userprofile.UserProfileResponse
import java.io.File
import java.util.*

class HomeViewModel: BaseViewModel<HomeNavigator>
{

    var flashOn: Boolean = false
    var mediaFile: File? = null
    var encodedPoly: String? = null
    var lastLocation: Location? = null
    var address: String = ""
    var postType = IMAGE_POST
    var postIsFromCamera = false
    var videoMuted = false
    var placeResult: PlacesResult? = null
    lateinit var clickedImageBitmap: Bitmap
    lateinit var latLongList: LinkedList<LatLng>

    private var userMutableData=MutableLiveData<UserProfileResponse>()
    private var postMutableResponse=MutableLiveData<PostResponse>()
    private var postCommentMutableResponse=MutableLiveData<PostCommentResponse>()
    private var postCommentReplyMutableResponse=MutableLiveData<PostCommentReplyResponse>()
    private var neighbourResponse=MutableLiveData<NeighbourResponse>()
    private var voteMutableResponse=MutableLiveData<VoteResponse>()
    private var createPostResponse=MutableLiveData<CreatePostResponse>()
    private var registerResponse=MutableLiveData<RegisterResponse>()
    private var stateResponse=MutableLiveData<StateResponse>()
    private var pincodeResponse=MutableLiveData<PincodeResponse>()
    private var groupInfoResponse=MutableLiveData<GroupInfoResponse>()

    lateinit var sharedPre: SharedPre
    @ViewModelInject
    constructor(sharedPre: SharedPre) : this() {
        this.sharedPre = sharedPre
    }

    constructor()

    fun GetProfile(uid:String): MutableLiveData<UserProfileResponse> {
        navigator.onLoading(true)
        AndroidNetworking.get(AppConstance.BASE_URL + AppConstance.USER_PROFILE)
            .addQueryParameter(AppConstance.USER_ID,uid)
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(UserProfileResponse::class.java, object :
                ParsedRequestListener<UserProfileResponse?> {
                override fun onResponse(response: UserProfileResponse?) {
                    navigator.onLoading(false)
                    if (response != null ) {
                        userMutableData.postValue(response!!)
                    }
                }

                override fun onError(anError: ANError) {
                    val res: InvalidToken = Gson().fromJson(anError.errorBody,InvalidToken::class.java)
                    if(res!=null && res.message.equals("Invalid token",true)){
                        navigator.Logout()
                    }
                    navigator.OnErrorMessage("Server Not Responding")
                    navigator.onLoading(false)
                }
            })
        return userMutableData
    }
//
//    fun getMyNeighbour(userId: String):MutableLiveData<NeighbourResponse> {
//        navigator.onLoading(true)
//        AndroidNetworking.get(AppConstance.BASE_URL + AppConstance.MY_NEIGHBOUR)
//            .addQueryParameter(AppConstance.USER_ID,userId)
//            .setPriority(Priority.HIGH)
//            .build()
//            .getAsObject(NeighbourResponse::class.java, object : ParsedRequestListener<NeighbourResponse?> {
//                override fun onResponse(response: NeighbourResponse?) {
//                    navigator.onLoading(false)
//                    if (response != null ) {
//                        neighbourResponse.postValue(response)
//                    }
//                }
//
//                override fun onError(anError: ANError) {
//                    navigator.onLoading(false)
//                    navigator.OnErrorMessage("Server Not Responding")
//
//                }
//            })
//        return neighbourResponse
//    }
    fun getMyPost(userId: String):MutableLiveData<PostResponse> {
        navigator.onLoading(false)
        AndroidNetworking.get(AppConstance.BASE_URL + AppConstance.MY_POST)
            .addQueryParameter(AppConstance.USER_ID,userId)
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(PostResponse::class.java, object : ParsedRequestListener<PostResponse?> {
                override fun onResponse(response: PostResponse?) {
                    navigator.onLoading(false)
                    if (response != null ) {
                        postMutableResponse.postValue(response!!)
                    }
                }

                override fun onError(anError: ANError) {
                    navigator.onLoading(false)
                    navigator.OnErrorMessage("Server Not Responding")

                }
            })
        return postMutableResponse
    }

    fun getLatLngFromAddress(address: String, zip: String) = repository.getLatLngFromAddress(address,zip)

    fun getPostByState(grpId: String,userId: String,count: Boolean) = repository.getPostById(grpId,userId,count)
    fun getNeighbourPost(grpId: String,userId: String,count: String) = repository.getNeighbourPost(grpId,userId,count)
    fun getPostComments(postId:String) = repository.getPostComment(postId)
    fun getCommentReply(commentId:String) = repository.getCommentReply(commentId)
    fun getMyNeighbour(userId:String) = repository.getMyNeighbour(userId)
    fun getGroupAPI(latitude: Double, longitude: Double) = repository.getGroupAPI(latitude,longitude)
    fun stateAPI(grpId: String) = repository.stateAPI(grpId)

    fun insertUserAPI(userId: String,
                      firstName: String,
                      lastName: String,
                      occupation:String,
                      pic:String,
                      cDate: String,
                      mob: String,
                      latLng:String,
                      groupId:String,
                      city: String,
                      state: String,
                      address: String,
                      zip:String,
                      email:String
    )= repository.insertUserAPI(
        userId,
        firstName,
        lastName,
        occupation,
        pic,
        cDate,
        mob,
        latLng,
        groupId,
        city,
        state,
        address,
        zip,
        email
    )

//    fun getStatePost(grpId: String,userId: String,count: String):MutableLiveData<PostResponse> {
//        navigator.onLoading(true)
//        AndroidNetworking.get(AppConstance.BASE_URL + AppConstance.STATE_POST)
//            .addQueryParameter(AppConstance.USER_ID,userId)
//            .addQueryParameter(AppConstance.GROUP_ID,grpId)
//            .addQueryParameter(AppConstance.COUNT,count)
//            .setPriority(Priority.HIGH)
//            .build()
//            .getAsObject(PostResponse::class.java, object : ParsedRequestListener<PostResponse?> {
//                override fun onResponse(response: PostResponse?) {
//                    navigator.onLoading(false)
//                    if (response != null ) {
//                        postMutableResponse.postValue(response)
//                    }
//                }
//
//                override fun onError(anError: ANError) {
//                    navigator.onLoading(false)
//                    navigator.OnErrorMessage("Server Not Responding")
//
//                }
//            })
//        return postMutableResponse
//    }

//    fun getNeighbourPost(groupId: String,userId: String,count: String):MutableLiveData<PostResponse> {
//        navigator.onLoading(true)
//        AndroidNetworking.get(AppConstance.BASE_URL + AppConstance.GROUP_POST)
//            .addQueryParameter(AppConstance.USER_ID,userId)
//            .addQueryParameter(AppConstance.GROUP_ID,groupId)
//            .addQueryParameter(AppConstance.COUNT,count)
//            .setPriority(Priority.HIGH)
//            .build()
//            .getAsObject(PostResponse::class.java, object : ParsedRequestListener<PostResponse?> {
//                override fun onResponse(response: PostResponse?) {
//                    navigator.onLoading(false)
//                    if (response != null ) {
//                        postMutableResponse.postValue(response)
//                    }
//                }
//
//                override fun onError(anError: ANError) {
//                    navigator.onLoading(false)
//                    navigator.OnErrorMessage("Server Not Responding")
//
//                }
//            })
//        return postMutableResponse
//    }

    fun UpVoteAPI(postId: Int,userId: String):MutableLiveData<VoteResponse> {
        navigator.onLoading(true)
        AndroidNetworking.get(AppConstance.BASE_URL + AppConstance.UP_VOTE)
            .addQueryParameter(AppConstance.USER_ID,userId)
            .addQueryParameter(AppConstance.POST_ID,postId.toString())
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(VoteResponse::class.java, object : ParsedRequestListener<VoteResponse?> {
                override fun onResponse(response: VoteResponse?) {
                    navigator.onLoading(false)
                    if (response != null ) {
                        voteMutableResponse.postValue(response!!)
                    }
                }

                override fun onError(anError: ANError) {
                    navigator.onLoading(false)
                    navigator.OnErrorMessage("Server Not Responding")

                }
            })
        return voteMutableResponse
    }

    fun DownVoteAPI(postId: Int,userId: String):MutableLiveData<VoteResponse> {
        navigator.onLoading(true)
        AndroidNetworking.get(AppConstance.BASE_URL + AppConstance.DOWN_VOTE)
            .addQueryParameter(AppConstance.USER_ID,userId)
            .addQueryParameter(AppConstance.POST_ID,postId.toString())
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(VoteResponse::class.java, object : ParsedRequestListener<VoteResponse?> {
                override fun onResponse(response: VoteResponse?) {
                    navigator.onLoading(false)
                    if (response != null ) {
                        voteMutableResponse.postValue(response!!)
                    }
                }

                override fun onError(anError: ANError) {
                    navigator.onLoading(false)
                    navigator.OnErrorMessage("Server Not Responding")

                }
            })
        return voteMutableResponse
    }

    fun addComment(comment: String,date: String, userId: String,postId:String): MutableLiveData<PostCommentResponse> {
        navigator.onLoading(true)
        AndroidNetworking.post(AppConstance.BASE_URL + AppConstance.ADD_COMMENT)
            .addBodyParameter(AppConstance.USER_ID,userId)
            .addBodyParameter(AppConstance.POST_ID,postId)
            .addBodyParameter(AppConstance.CREATED_ON,date)
            .addBodyParameter(AppConstance.UPDATED_ON,date)
            .addBodyParameter(AppConstance.COMMENT,comment)
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(PostCommentResponse::class.java, object : ParsedRequestListener<PostCommentResponse?> {
                override fun onResponse(response: PostCommentResponse?) {
                    navigator.onLoading(false)
                    if (response != null) {
                        postCommentMutableResponse.postValue(response!!)
                    }else{
                        navigator.OnErrorMessage("Erron on Comment ")
                    }
                }

                override fun onError(anError: ANError) {
                    navigator.onLoading(false)
                    val res: InvalidToken = Gson().fromJson(anError.errorBody, InvalidToken::class.java)
                    navigator.OnErrorMessage( res.message)


                }
            })
        return postCommentMutableResponse
    }
    fun addReply(reply: String,date: String, userId: String,postId:String,commentId:String): MutableLiveData<PostCommentReplyResponse> {
        navigator.onLoading(true)
        AndroidNetworking.post(AppConstance.BASE_URL + AppConstance.ADD_REPLY)
            .addBodyParameter(AppConstance.USER_ID,userId)
            .addBodyParameter(AppConstance.POST_ID,postId)
            .addBodyParameter(AppConstance.COMMENT_ID,commentId)
            .addBodyParameter(AppConstance.CREATED_ON,date)
            .addBodyParameter(AppConstance.UPDATED_ON,date)
            .addBodyParameter(AppConstance.COMMENT,reply)
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(PostCommentReplyResponse::class.java, object : ParsedRequestListener<PostCommentReplyResponse?> {
                override fun onResponse(response: PostCommentReplyResponse?) {
                    navigator.onLoading(false)
                    if (response != null) {
                        postCommentReplyMutableResponse.postValue(response!!)
                    }else{
                        navigator.OnErrorMessage("Erron on Comment ")
                    }
                }

                override fun onError(anError: ANError) {
                    navigator.onLoading(false)
                    val res: InvalidToken = Gson().fromJson(anError.errorBody, InvalidToken::class.java)
                    navigator.OnErrorMessage( res.message)


                }
            })
        return postCommentReplyMutableResponse
    }

//    fun getPostComments(postId: String):MutableLiveData<PostCommentResponse> {
//        navigator.onLoading(true)
//        AndroidNetworking.get(AppConstance.BASE_URL + AppConstance.GET_COMMENT)
//            .addQueryParameter(AppConstance.POST_ID,postId)
//            .setPriority(Priority.HIGH)
//            .build()
//            .getAsObject(PostCommentResponse::class.java, object : ParsedRequestListener<PostCommentResponse?> {
//                override fun onResponse(response: PostCommentResponse?) {
//                    navigator.onLoading(false)
//                    if (response != null ) {
//                        postCommentMutableResponse.postValue(response)
//                    }
//                }
//
//                override fun onError(anError: ANError) {
//                    navigator.onLoading(false)
//                    navigator.OnErrorMessage("Server Not Responding"+anError)
//
//                }
//            })
//        return postCommentMutableResponse
//    }

//    fun getCommentReply(commentId: String):MutableLiveData<PostCommentReplyResponse> {
//        navigator.onLoading(true)
//        AndroidNetworking.get(AppConstance.BASE_URL + AppConstance.GET_REPLY)
//            .addQueryParameter(AppConstance.COMMENT_ID,commentId)
//            .setPriority(Priority.HIGH)
//            .build()
//            .getAsObject(PostCommentReplyResponse::class.java, object : ParsedRequestListener<PostCommentReplyResponse?> {
//                override fun onResponse(response: PostCommentReplyResponse?) {
//                    navigator.onLoading(false)
//                    if (response != null ) {
//                        postCommentReplyMutableResponse.postValue(response)
//                    }
//                }
//
//                override fun onError(anError: ANError) {
//                    navigator.onLoading(false)
//                    navigator.OnErrorMessage("Server Not Responding"+anError)
//
//                }
//            })
//        return postCommentReplyMutableResponse
//    }


    fun createPost(
        textMess: String,
        uid: String?,
        curDate: String?,
        generatedVideoFilePath: String,
        generatedImageFilePath: String,
        postLocation: String,
        grpId: String,
        city: String,
        state: String
    ): MutableLiveData<CreatePostResponse> {
        navigator.onLoading(true)
        AndroidNetworking.post(AppConstance.BASE_URL + AppConstance.CREATE_POST)
            .addBodyParameter(AppConstance.BODY_TEXT,textMess)
            .addBodyParameter(AppConstance.USER_ID,uid)
            .addBodyParameter(AppConstance.CREATED_ON,curDate)
            .addBodyParameter(AppConstance.IMAGE,generatedImageFilePath)
            .addBodyParameter(AppConstance.VIDIEO,generatedVideoFilePath)
            .addBodyParameter(AppConstance.UPDATED_ON,curDate)
            .addBodyParameter(AppConstance.UPVOTECOUNT,"0")
            .addBodyParameter(AppConstance.DOWNVOTECOUNT,"0")
            .addBodyParameter(AppConstance.LOCATION,postLocation)
            .addBodyParameter(AppConstance.GROUP_ID,grpId)
            .addBodyParameter(AppConstance.CITY,city)
            .addBodyParameter(AppConstance.STATE,state)
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(CreatePostResponse::class.java, object : ParsedRequestListener<CreatePostResponse?> {
                override fun onResponse(response: CreatePostResponse?) {
                    navigator.onLoading(false)
                    if (response != null) {
                        createPostResponse.postValue(response!!)
                    }
                }

                override fun onError(anError: ANError) {
                    navigator.onLoading(false)
                    Log.d("CreatePostError: ",anError.toString())
                    val res: InvalidToken = Gson().fromJson(anError.errorBody, InvalidToken::class.java)
                    navigator.OnErrorMessage( res.message)
                }
            })
        return createPostResponse
    }

    fun updatePost(
        textMess: String,
        uid: String?,
        curDate: String?,
        generatedVideoFilePath: String,
        generatedImageFilePath: String,
        postLocation: String,
        createdDate: String,
        postId: String,
        grpId: String,
        city: String,
        state: String
    ): MutableLiveData<CreatePostResponse> {
        navigator.onLoading(true)
        AndroidNetworking.post(AppConstance.BASE_URL + AppConstance.UPDATE_POST)
            .addBodyParameter(AppConstance.BODY_TEXT,textMess)
            .addBodyParameter(AppConstance.USER_ID,uid)
            .addBodyParameter(AppConstance.CREATED_ON,createdDate)
            .addBodyParameter(AppConstance.IMAGE,generatedImageFilePath)
            .addBodyParameter(AppConstance.VIDIEO,generatedVideoFilePath)
            .addBodyParameter(AppConstance.UPDATED_ON,curDate)
            .addBodyParameter(AppConstance.UPVOTECOUNT,"0")
            .addBodyParameter(AppConstance.DOWNVOTECOUNT,"0")
            .addBodyParameter(AppConstance.LOCATION,postLocation)
            .addBodyParameter(AppConstance.GROUP_ID,grpId)
            .addBodyParameter(AppConstance.CITY,city)
            .addBodyParameter(AppConstance.STATE,state)
            .addBodyParameter(AppConstance.POST_ID,postId)
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(CreatePostResponse::class.java, object : ParsedRequestListener<CreatePostResponse?> {
                override fun onResponse(response: CreatePostResponse?) {
                    navigator.onLoading(false)
                    if (response != null) {
                        createPostResponse.postValue(response!!)
                    }
                }

                override fun onError(anError: ANError) {
                    navigator.onLoading(false)
                    Log.d("CreatePostError: ",anError.toString())
                    val res: InvalidToken = Gson().fromJson(anError.errorBody, InvalidToken::class.java)
                    navigator.OnErrorMessage( res.message)
                }
            })
        return createPostResponse
    }

    //register


//
//    fun insertUserAPI(userId: String,
//                      firstName: String,
//                      lastName: String,
//                      occupation:String,
//                      pic:String,
//                      cDate: String,
//                      mob: String,
//                      latLng:String,
//                      groupId:String,
//                      city: String,
//                      state: String,
//                      address: String,
//                      zip:String,
//                      email:String
//    )
//            : MutableLiveData<RegisterResponse> {
//        navigator.onLoading(false)
//        AndroidNetworking.post(AppConstance.BASE_URL + AppConstance.INSERT_USER)
//            .addBodyParameter(AppConstance.USER_ID,userId)
//            .addBodyParameter(AppConstance.FIRST_NAME,firstName)
//            .addBodyParameter(AppConstance.LAST_NAME,lastName)
//            .addBodyParameter(AppConstance.OCCUPATION,occupation)
//            .addBodyParameter(AppConstance.PROFILE_PICTURE,pic)
//            .addBodyParameter(AppConstance.CREATED_ON,cDate)
//            .addBodyParameter(AppConstance.UPDATED_ON,cDate)
//            .addBodyParameter(AppConstance.PHONE_NO,mob)
//            .addBodyParameter(AppConstance.LAT_LNG,latLng)
//            .addBodyParameter(AppConstance.GROUP_ID,groupId)
//            .addBodyParameter(AppConstance.CITY,city)
//            .addBodyParameter(AppConstance.STATE,state)
//            .addBodyParameter(AppConstance.ADDRESS,address)
//            .addBodyParameter(AppConstance.ZIP,zip)
//            .addBodyParameter(AppConstance.EMAIL,email)
//            .setPriority(Priority.HIGH)
//            .build()
//            .getAsObject(RegisterResponse::class.java, object : ParsedRequestListener<RegisterResponse?> {
//
//                override fun onResponse(response: RegisterResponse?) {
//                    navigator.onLoading(false)
//                    if (response != null) {
//                        registerResponse.postValue(response!!)
//                    }
//                }
//
//                override fun onError(anError: ANError) {}
//            })
//        return registerResponse
//    }

//    fun stateAPI(grpId: String): MutableLiveData<StateResponse> {
//        navigator.onLoading(true)
//        AndroidNetworking.get(AppConstance.BASE_URL + AppConstance.FIND_NEAR_GROUP)
//            .addQueryParameter(AppConstance.GROUP_ID,grpId)
//            .setPriority(Priority.HIGH)
//            .build()
//            .getAsObject(StateResponse::class.java, object : ParsedRequestListener<StateResponse?> {
//                override fun onResponse(response: StateResponse?) {
//                    navigator.onLoading(false)
//                    if (response != null) {
//                        stateResponse.postValue(response!!)
//                    }
//                }
//                override fun onError(anError: ANError) {}
//            })
//        return stateResponse
//    }

    fun pincodeAPI(pin: String): MutableLiveData<PincodeResponse> {
        navigator.onLoading(true)
        AndroidNetworking.get(AppConstance.PINCODE_URL + pin)
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(PincodeResponse::class.java, object : ParsedRequestListener<PincodeResponse?> {
                override fun onResponse(response: PincodeResponse?) {
                    navigator.onLoading(false)
                    if (response != null) {
                        pincodeResponse.postValue(response!!)
                    }
                }
                override fun onError(anError: ANError) {}
            })
        return pincodeResponse
    }

//    fun getGroupAPI(latitude: String,longitude: String): MutableLiveData<GroupInfoResponse> {
//        navigator.onLoading(true)
//        AndroidNetworking.get(AppConstance.BASE_URL + AppConstance.FIND_GROUP)
//            .addQueryParameter(AppConstance.LATITUDE,latitude)
//            .addQueryParameter(AppConstance.LONGITUDE,longitude)
//            .setPriority(Priority.HIGH)
//            .build()
//            .getAsObject(GroupInfoResponse::class.java, object : ParsedRequestListener<GroupInfoResponse?> {
//                override fun onResponse(response: GroupInfoResponse?) {
//                    navigator.onLoading(false)
//                    if (response != null) {
//                        groupInfoResponse.postValue(response!!)
//                    }
//                }
//                override fun onError(anError: ANError) {
//
//                }
//            })
//        navigator.onLoading(false)
//        return groupInfoResponse
//    }
}