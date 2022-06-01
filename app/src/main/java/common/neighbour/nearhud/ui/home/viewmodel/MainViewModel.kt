package common.neighbour.nearhud.ui.home.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.location.Location
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import common.neighbour.nearhud.core.extensions.replaceFragment
import com.google.android.gms.maps.model.LatLng
import common.neighbour.nearhud.R
import common.neighbour.nearhud.api.pagingsource.HomeNeighbourPostPaging
import common.neighbour.nearhud.api.pagingsource.HomePostPaging
import common.neighbour.nearhud.application.MyApplication
import common.neighbour.nearhud.application.MyApplication.Companion.repository
import common.neighbour.nearhud.core.BaseViewModel
import common.neighbour.nearhud.ui.home.ui.MainActivity
import common.neighbour.nearhud.ui.home.ui.MainFragment
import common.neighbour.nearhud.newUi.PlacesResult
import common.neighbour.nearhud.retrofit.model.post.Data
import common.neighbour.nearhud.ui.home.fragment.setting.SettingFragment
import java.util.*

class MainViewModel (app: Application) : BaseViewModel(app) {
    var flashOn: Boolean = false
    var encodedPoly: String? = null
    var lastLocation: Location? = null
    var address: String = ""
    var postIsFromCamera = false
    var videoMuted = false
    var placeResult: PlacesResult? = null
    lateinit var clickedImageBitmap: Bitmap
    lateinit var latLongList: LinkedList<LatLng>

    var grpId = ""
    var userId = ""
    var isSstate = true

    val homeSelectedColor by lazy { MutableLiveData<Int>(R.color.white) }
    val searchSelectedColor by lazy { MutableLiveData<Int>(R.color.white) }

    fun switchHomePage(activity: MainActivity) {
        homeSelectedColor.postValue(R.color.colorPrimary)
        searchSelectedColor.postValue(R.color.white)
        if (activity.supportFragmentManager.findFragmentById(R.id.container) !is MainFragment) {
            activity.replaceFragment(MainFragment(), R.id.container, "frag_home")
        }
    }
    fun switchSettingPage(activity: MainActivity) {
        homeSelectedColor.postValue(R.color.white)
        searchSelectedColor.postValue(R.color.colorPrimary)
        if (activity.supportFragmentManager.findFragmentById(R.id.container) !is SettingFragment){
            activity.replaceFragment(SettingFragment(), R.id.container, "frag_setting")
        }
    }
    fun switchSettingPage2(activity: FragmentActivity) {
        homeSelectedColor.postValue(R.color.white)
        searchSelectedColor.postValue(R.color.colorPrimary)
        if (activity.supportFragmentManager.findFragmentById(R.id.container) !is SettingFragment){
            activity.replaceFragment(SettingFragment(), R.id.container, "frag_setting")
        }
    }
    fun switchHomePage2(activity: FragmentActivity) {
        homeSelectedColor.postValue(R.color.colorPrimary)
        searchSelectedColor.postValue(R.color.white)
        if (activity.supportFragmentManager.findFragmentById(R.id.container) !is MainFragment) {
            activity.replaceFragment(MainFragment(), R.id.container, "frag_home")
        }
    }

    //pagination
    // fun getHomePost() = MyApplication.repository.getHomeStatePostMediator()

    fun getPostByState(grpId: String,userId: String,isState: Boolean) = MyApplication.repository.getPostById(grpId,userId,isState)
    fun getMyPost(userId: String) = MyApplication.repository.getMyPost(userId)
    fun getNeighbourPost(grpId: String,userId: String,count: String) = MyApplication.repository.getNeighbourPost(grpId,userId,count)
    fun getPostComments(postId:String) = MyApplication.repository.getPostComment(postId)
    fun getCommentReply(commentId:String) = MyApplication.repository.getCommentReply(commentId)
    fun getMyNeighbour(userId:String) = MyApplication.repository.getMyNeighbour(userId)
    fun getOccupation() = MyApplication.repository.getOccupation()
    fun getHelp() = MyApplication.repository.getHelp()
    fun getGroupAPI(latitude: Double, longitude: Double) = MyApplication.repository.getGroupAPI(latitude,longitude)
    fun stateAPI(grpId: String) = MyApplication.repository.stateAPI(grpId)
    fun GetProfile(uid: String) = MyApplication.repository.GetProfile(uid)
    fun UpVoteAPI(postId: Int,userId: String) = MyApplication.repository.UpVoteAPI(postId.toString(),userId)
    fun DownVoteAPI(postId: Int,userId: String) = MyApplication.repository.DownVoteAPI(postId.toString(),userId)
    fun createPost(textMess: String,
                   uid: String?,
                   curDate: String?,
                   generatedVideoFilePath: String,
                   generatedImageFilePath: String,
                   userCity: String?,
                   grpID: String,
                   userState: String,
                   postLocation: String
    ) = MyApplication.repository.createPost(textMess,uid!!,curDate!!,
        generatedVideoFilePath,
        generatedImageFilePath,
        userCity!!,
        grpID,
        userState,
        postLocation
    )
    fun updatePost(textMess: String,
                   uid: String?,
                   curDate: String?,
                   updateDate: String?,
                   generatedVideoFilePath: String,
                   generatedImageFilePath: String,
                   userCity: String?,
                   grpID: String,
                   userState: String,
                   postLocation: String,
                   postId: String
    ) = MyApplication.repository.updatePost(textMess,uid!!,curDate!!,updateDate!!,
        generatedVideoFilePath,
        generatedImageFilePath,
        userCity!!,
        grpID,
        userState,
        postLocation,
        postId
    )

    fun updateUserAPI(userId: String,
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
    )= repository.updateUserApi(
        userId,
        firstName,
        lastName,
        occupation,
        pic,
        cDate,
        uDate,
        mob,
        latLng,
        groupId,
        city,
        state,
        address,
        zip,
        email
    )

    //
    var postsLiveData = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false,
            prefetchDistance = 10
        ),
        pagingSourceFactory = { HomePostPaging(repository, grpId,userId,isSstate) }
    ).liveData.cachedIn(viewModelScope)

    fun getHomePosts(isState: Boolean,grpID: String,userID: String)
    : LiveData<PagingData<Data>> {
        grpId = grpID
        userId = userID
        isSstate = isState
        //if (newPostIsAdded) { }
        postsLiveData = Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 10
            ),
            pagingSourceFactory = { HomePostPaging(repository, grpID,userID,isSstate) }
        ).liveData.cachedIn(viewModelScope)
        return postsLiveData
    }

    var postsNeighbourLiveData = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false,
            prefetchDistance = 10
        ),
        pagingSourceFactory = { HomeNeighbourPostPaging(repository,grpId,userId) }
    ).liveData.cachedIn(viewModelScope)

    fun getHomeNeighbourPosts(newPostIsAdded: Boolean,grpID: String,userID: String):
            LiveData<PagingData<common.neighbour.nearhud.retrofit.model.post.Data>> {
        grpId = grpID
        userId = userID
        if (newPostIsAdded) {
            postsLiveData = Pager(
                config = PagingConfig(
                    pageSize = 20,
                    enablePlaceholders = false,
                    prefetchDistance = 10
                ),
                pagingSourceFactory = { HomeNeighbourPostPaging(repository,grpID,userID) }
            ).liveData.cachedIn(viewModelScope)
        }
        return postsNeighbourLiveData
    }
}