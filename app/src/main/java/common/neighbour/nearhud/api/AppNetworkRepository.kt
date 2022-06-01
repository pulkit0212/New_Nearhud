package common.neighbour.nearhud.api

import android.content.Context
import common.neighbour.nearhud.database.prefrence.SharedPre
import common.neighbour.nearhud.repositories.constance.AppConstance
import common.neighbour.nearhud.retrofit.model.post.PostResponse
import retrofit2.Response

class AppNetworkRepository(private val context: Context) : BaseDataSource() {


    var apiService = ApiInterface.createOnRecord(SharedPre.getInstance(context)!!.jwtToken)
    //var appDb = AppDB.getDatabase(context)

//    var apiServicePlaces =
//        ApiInterface.createPlacesApi(PrefManager(context).getString(PrefManager.PREF_TOKEN))
//    var apiServiceToken = ApiInterface.createToken()

//
//    fun getToken(apikey: String) = performOperation(ApiInterface.API_GET_TOKEN) {
//        getResult(ApiInterface.API_GET_TOKEN) {
//            val map = hashMapOf(
//                "api_key" to apikey,
//            )
//            apiServiceToken.getToken(map)
//        }
//    }


    //  suspend fun getUserPost(map: HashMap<String, String>) = apiService.getUserPost(map)

//    fun getAppContent() = performOperation("") {
//        getResult("") {
//            apiService.getAppContent()
//        }
//    }

//    fun getPostById(grpId: String,userId: String,count: String) = performOperation{
//        getResult{
//            apiService.getHomePosts(hashMapOf("groupId" to grpId,"userId" to userId,"count" to count))
//        }
//    }


    fun getPostById(grpId: String,userId: String,isState: Boolean) = performOperation {
        getResult {
            val map = hashMapOf(
                AppConstance.GROUP_ID to grpId,
                AppConstance.USER_ID to userId,
                AppConstance.isState to isState.toString()
            )
            apiService.getHomePosts(map)
        }
    }
    fun createPost(
        textMess: String,
        uid: String,
        curDate: String,
        generatedVideoFilePath: String,
        generatedImageFilePath: String,
        city: String,
        grpId: String,
        state: String,
        postLocation: String
    ) = performOperation {
        getResult {
            val map = hashMapOf(
                AppConstance.BODY_TEXT to textMess,
                AppConstance.USER_ID to uid,
                AppConstance.CREATED_ON to curDate,
                AppConstance.IMAGE to generatedImageFilePath,
                AppConstance.VIDIEO to generatedVideoFilePath,
                AppConstance.UPDATED_ON to curDate,
                AppConstance.UPVOTECOUNT to "0",
                AppConstance.DOWNVOTECOUNT to "0",
                AppConstance.LOCATION to postLocation,
                AppConstance.GROUP_ID to grpId,
                AppConstance.CITY to city,
                AppConstance.STATE to state
            )
            apiService.createPost(map)
        }
    }

    fun updatePost(
        textMess: String,
        uid: String,
        curDate: String,
        updateDate: String,
        generatedVideoFilePath: String,
        generatedImageFilePath: String,
        city: String,
        grpId: String,
        state: String,
        postLocation: String,
        postId: String
    ) = performOperation {
        getResult {
            val map = hashMapOf(
                AppConstance.BODY_TEXT to textMess,
                AppConstance.USER_ID to uid,
                AppConstance.CREATED_ON to curDate,
                AppConstance.IMAGE to generatedImageFilePath,
                AppConstance.VIDIEO to generatedVideoFilePath,
                AppConstance.UPDATED_ON to updateDate,
                AppConstance.UPVOTECOUNT to "0",
                AppConstance.DOWNVOTECOUNT to "0",
                AppConstance.LOCATION to postLocation,
                AppConstance.GROUP_ID to grpId,
                AppConstance.CITY to city,
                AppConstance.STATE to state,
                AppConstance.POST_ID to postId
            )
            apiService.updatePost(map)
        }
    }

    fun insertUserAPI(
        userId: String,
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
    ) = performOperation {
        getResult {
            val map = hashMapOf(
                AppConstance.USER_ID to userId,
                AppConstance.FIRST_NAME to firstName,
                AppConstance.LAST_NAME to lastName,
                AppConstance.OCCUPATION to occupation,
                AppConstance.PROFILE_PICTURE to pic,
                AppConstance.CREATED_ON to cDate,
                AppConstance.UPDATED_ON to cDate,
                AppConstance.PHONE_NO to mob,
                AppConstance.LAT_LNG to latLng,
                AppConstance.GROUP_ID to groupId,
                AppConstance.CITY to city,
                AppConstance.STATE to state,
                AppConstance.ADDRESS to address,
                AppConstance.ZIP to zip,
                AppConstance.EMAIL to email,
            )
            apiService.insertUserAPI(map)
        }
    }
    fun updateUserApi(
        userId: String,
        firstName: String,
        lastName: String,
        occupation:String,
        pic:String,
        cDate: String,
        uDate: String,
        mob: String,
        latLng:String,
        groupId:String,
        city: String,
        state: String,
        address: String,
        zip:String,
        email:String
    ) = performOperation {
        getResult {
            val map = hashMapOf(
                AppConstance.USER_ID to userId,
                AppConstance.FIRST_NAME to firstName,
                AppConstance.LAST_NAME to lastName,
                AppConstance.OCCUPATION to occupation,
                AppConstance.PROFILE_PICTURE to pic,
                AppConstance.CREATED_ON to cDate,
                AppConstance.UPDATED_ON to uDate,
                AppConstance.PHONE_NO to mob,
                AppConstance.LAT_LNG to latLng,
                AppConstance.GROUP_ID to groupId,
                AppConstance.CITY to city,
                AppConstance.STATE to state,
                AppConstance.ADDRESS to address,
                AppConstance.ZIP to zip,
                AppConstance.EMAIL to email,
            )
            apiService.updateUserAPI(map)
        }
    }


//    fun getHashTags(key: String?) = performOperation(ApiInterface.API_SEARCH_HASH_TAGS) {
//        getResult(ApiInterface.API_SEARCH_HASH_TAGS) {
//            val map = hashMapOf(
//                "keyword" to key
//            )
//            apiService.searchHashTags(map)
//        }
//    }

    fun getNeighbourPost(grpId: String,userId: String,count: String) = performOperation {
        getResult {
            val map = hashMapOf(
                AppConstance.GROUP_ID to grpId,
                AppConstance.USER_ID to userId,
                AppConstance.PAGE to count
            )
            apiService.getNeighbourPost(map)
        }
    }
    fun getPostComment(postId: String) = performOperation {
        getResult {
            apiService.getPostComment(
                postId
            )
        }
    }
    fun getCommentReply(commentID: String) = performOperation {
        getResult {
            apiService.getCommentReply(
                commentID
            )
        }
    }
    fun getMyNeighbour(userId: String) = performOperation {
        getResult {
            apiService.getMyNeighbour(
                userId
            )
        }
    }
    fun getOccupation() = performOperation {
        getResult {
            apiService.getOccupation()
        }
    }
    fun getHelp() = performOperation {
        getResult {
            apiService.getHelp()
        }
    }
    fun getLaunchScreen() = performOperation {
        getResult {
            apiService.getLaunchScreen()
        }
    }
    fun getLatLngFromAddress(address: String,zip: String) = performOperation {
        getResult {
            val map = hashMapOf(
                AppConstance.ADDRESS to address,
                AppConstance.PINCODE to zip
            )
            apiService.getLatLngFromAddress(map)
        }
    }

    fun getGroupAPI(latitude: Double,longitude:Double) = performOperation {
        getResult {
            apiService.getGroupAPI(
                latitude.toString(),
                longitude.toString()
            )
        }
    }
    fun stateAPI(grpId: String) = performOperation {
        getResult {
            apiService.stateAPI(
                grpId
            )
        }
    }
    fun GetProfile(uId: String) = performOperation {
        getResult {
            apiService.GetProfile(
                uId
            )
        }
    }
    fun getMyPost(userId: String) = performOperation {
        getResult {
            apiService.getMyPost(
                userId
            )
        }
    }
    fun UpVoteAPI(postId: String,userId:String) = performOperation {
        getResult {
            apiService.UpVoteAPI(
                postId,
                userId
            )
        }
    }
    fun DownVoteAPI(postId: String,userId:String) = performOperation {
        getResult {
            apiService.DownVoteAPI(
                postId,
                userId
            )
        }
    }
    fun sendOtp(mob: String) = performOperation {
        getResult {
            apiService.sendOtp(
                mob
            )
        }
    }

//    suspend fun getAndSaveAuthToken() {
//        if (getAuthToken().isNullOrEmpty()) {
//            var authResponse = apiService.getAuthToken(Utility.convertToRequestBody(mapOf("api_key" to AppConstance.API_PRIVATE_KEY)))
//            if (authResponse.isSuccessful) {
//                appDb.authDao().insertAll(AuthToken(Utility.getDeviceID(context)!!, authResponse.body()!!.token, ""))
//            }
//        }
//    }
//
//    fun getAuthToken(): String {
//        var auth = appDb.authDao().getAuthData()
//        return if (auth == null) {
//            ""
//        } else {
//            auth.authToken
//        }
//    }

//
//    suspend fun getHomePosts(map: HashMap<String, String>): Response<GetPostHomeResponse> {
//        return apiService.getHomePosts(map)
//    }
//
//    suspend fun getExplore(map: HashMap<String, String>): Response<GetPostHomeResponse> {
//        return apiService.getExploreSearch(map)
//    }



//    fun getTextPostSearch(
//        userId: String,
//        keyword: String,
//        searchType: String
//    ): Flow<PagingData<com.onrecord.live.api.modal.responses.SearchText_PostRespResult>> {
//        return Pager(
//            config = PagingConfig(
//                pageSize = 40,
//                enablePlaceholders = false,
//                prefetchDistance = 1
//            ),
//            pagingSourceFactory = {  }
//        ).flow // flow & livedata are already asynchronous
//    }


    //new
    suspend fun getHomePosts(map: HashMap<String, String>): Response<PostResponse> {
        return apiService.getHomePosts(map)
    }

    suspend fun getHomeNeighbourPosts(map: HashMap<String, String>): Response<PostResponse> {
        return apiService.getNeighbourPost(map)
    }

}