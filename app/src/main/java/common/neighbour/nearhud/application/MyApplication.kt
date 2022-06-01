package common.neighbour.nearhud.application
/*Android Developer
* Pulkit Sharma
* Java and Kotlin
*
* */
import android.content.Context
import androidx.multidex.MultiDexApplication
import com.androidnetworking.AndroidNetworking
import com.danikula.videocache.HttpProxyCacheServer
import common.neighbour.nearhud.api.AppNetworkRepository
import common.neighbour.nearhud.database.prefrence.SharedPre
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

@HiltAndroidApp(MultiDexApplication::class)
class MyApplication  : common.neighbour.nearhud.application.Hilt_MyApplication(){
    companion object{
        private var instance: MyApplication? = null
        lateinit var repository:AppNetworkRepository

        //private lateinit var appDatabase: AppDB

        @JvmStatic
        fun getInstance(): MyApplication? {
            if (instance == null) {
                instance = MyApplication()
            }
            return instance
        }
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
        repository = AppNetworkRepository(this)
        //appDatabase = AppDB.getDatabase(this)
        val okHttpClient = OkHttpClient().newBuilder().addInterceptor { chain ->
            val newRequest = chain.request().newBuilder().addHeader(
                "Authorization", "Bearer " +  SharedPre.getInstance(applicationContext)!!.jwtToken
            )
                .build()
            chain.proceed(newRequest)
        }
            .connectTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .build()
        AndroidNetworking.initialize(this, okHttpClient)
    }

    var proxy: HttpProxyCacheServer? = null

    fun getProxy(context: Context): HttpProxyCacheServer? {
        val app = context.applicationContext as common.neighbour.nearhud.application.MyApplication
        return if (app.proxy == null) app.newProxy().also { app.proxy = it } else app.proxy
    }

    fun newProxy(): HttpProxyCacheServer {
        return HttpProxyCacheServer(this)
    }
    fun getNetworkRepo(): AppNetworkRepository {
        return repository
    }

//    fun getAppDatabase(): AppDB {
//        return appDatabase
//    }
}