package common.neighbour.nearhud.ui.login.viewmodel

import android.app.Activity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import common.neighbour.nearhud.application.MyApplication
import common.neighbour.nearhud.base.BaseViewModel
import common.neighbour.nearhud.database.datastore.DataStoreBase
import common.neighbour.nearhud.database.prefrence.SharedPre
import common.neighbour.nearhud.repositories.constance.AppConstance
import common.neighbour.nearhud.repositories.methods.FirbaseAuthActions
import common.neighbour.nearhud.repositories.methods.MethodsRepo
import common.neighbour.nearhud.retrofit.model.group.GroupInfoResponse
import common.neighbour.nearhud.retrofit.model.login.OtpResponse
import common.neighbour.nearhud.retrofit.model.neighbour.NeighbourResponse
import common.neighbour.nearhud.retrofit.model.post.PostResponse
import common.neighbour.nearhud.retrofit.model.register.RegisterResponse
import common.neighbour.nearhud.retrofit.model.state.PincodeResponse
import common.neighbour.nearhud.retrofit.model.state.StateResponse
import common.neighbour.nearhud.retrofit.model.token.CheckUserResponse
import common.neighbour.nearhud.retrofit.model.token.ReferedData
import common.neighbour.nearhud.retrofit.model.token.TokenResponse

class LoginViewmodel() : BaseViewModel<LoginNavigator>() {

    private lateinit var datastore: DataStoreBase
    private var tokenresponse=MutableLiveData<TokenResponse>()
    private var checkuserresponse=MutableLiveData<CheckUserResponse>()
    private var otpResponse=MutableLiveData<OtpResponse>()
    lateinit var sharedPre: SharedPre
    private lateinit var methods: MethodsRepo

    var referedData = ArrayList<ReferedData>()


    @ViewModelInject
    constructor(datastore: DataStoreBase,methods: MethodsRepo,sharedPre: SharedPre) : this() {
        this.datastore = datastore
        this.sharedPre = sharedPre
        this.methods=methods
    }

    fun MobileAuthCheck(activity : Activity, phoneNumber: String, authActions: FirbaseAuthActions){
        methods.MobAuth(activity,phoneNumber,authActions);
    }

    fun verifyVerificationCode(code: String?, activityContext: Activity, firebaseActions: FirbaseAuthActions
    ) {
        methods.verifyVerificationCode(activityContext,code,firebaseActions)
    }




    fun ReSendOtp(activity : Activity, phoneNumber: String, authActions: FirbaseAuthActions) {
        methods.MobAuth(activity,phoneNumber,authActions);
    }

    fun getDataStore():DataStoreBase{
        return datastore!!
    }


    fun generateToken(id: String, secreat: String):MutableLiveData<TokenResponse> {
        navigator.onLoading(true)
        AndroidNetworking.get(AppConstance.BASE_URL + AppConstance.GENERATE_TOKEN)
            .addQueryParameter(AppConstance.CLIENT_ID,id)
            .addQueryParameter(AppConstance.CLIENT_SECRET,secreat)
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(TokenResponse::class.java, object : ParsedRequestListener<TokenResponse?> {
                override fun onResponse(response: TokenResponse?) {
                    navigator.onLoading(false)
                    if (response != null ) {
                        tokenresponse.postValue(response!!)
                    }
                }

                override fun onError(anError: ANError) {
                    navigator.onLoading(false)
                    navigator.OnErrorMessage("Server Not Responding")

                }
            })
        return tokenresponse
    }

    fun checkUser(id: String):MutableLiveData<CheckUserResponse> {
        navigator.onLoading(true)
        AndroidNetworking.get(AppConstance.BASE_URL + AppConstance.CHECK_USER)
            .addQueryParameter(AppConstance.USER_ID,id)
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(CheckUserResponse::class.java, object : ParsedRequestListener<CheckUserResponse?> {
                override fun onResponse(response: CheckUserResponse?) {
                    navigator.onLoading(false)
                    if (response != null ) {
                        checkuserresponse.postValue(response!!)
                    }
                }

                override fun onError(anError: ANError) {
                    navigator.onLoading(false)
                    navigator.OnErrorMessage("Server Not Responding")

                }
            })
        return checkuserresponse
    }

    fun sendOtp(mob:String) = MyApplication.repository.sendOtp(mob)

//    fun sendOtp(mob: String):MutableLiveData<OtpResponse> {
//        navigator.onLoading(true)
//        AndroidNetworking.get(AppConstance.BASE_URL + AppConstance.SEND_OTP)
//            .addQueryParameter(AppConstance.MOB_NO,mob)
//            .setPriority(Priority.HIGH)
//            .build()
//            .getAsObject(OtpResponse::class.java, object : ParsedRequestListener<OtpResponse?> {
//                override fun onResponse(response: OtpResponse?) {
//                    navigator.onLoading(false)
//                    if (response != null ) {
//                        otpResponse.postValue(response)
//                    }
//                }
//
//                override fun onError(anError: ANError) {
//                    navigator.onLoading(false)
//                    navigator.OnErrorMessage("Server Not Responding")
//
//                }
//            })
//        return otpResponse
//    }

}