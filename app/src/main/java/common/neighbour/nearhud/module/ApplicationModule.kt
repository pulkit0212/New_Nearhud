package common.neighbour.nearhud.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.createDataStore
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import common.neighbour.nearhud.database.datastore.DataStoreBase
import common.neighbour.nearhud.database.datastore.DataStoreCustom
import common.neighbour.nearhud.database.prefrence.SharedPre
import common.neighbour.nearhud.repositories.constance.AppConstance
import common.neighbour.nearhud.repositories.methods.MethodsRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
     fun provideCustomRepository(dataStore  : DataStore<Preferences>) : DataStoreBase {
        return DataStoreCustom(dataStore)
    }
    @Provides
    fun providePrefrence(@ApplicationContext context: Context): SharedPre = SharedPre.getInstance(
        context
    )!!
    @Provides
     fun provideDataStore(@ApplicationContext context: Context) : DataStore<Preferences> {
        return context.createDataStore("nearhud")
    }

    @Provides
    fun provideMethodsRepo(@ApplicationContext context: Context,dataStore  : DataStoreBase,sharedPre: SharedPre): MethodsRepo = MethodsRepo(context,dataStore,sharedPre)

    @Provides
    fun provideFirebaseDatabaseInstance(): DatabaseReference = FirebaseDatabase.getInstance().getReference(
        AppConstance.APP_NAME)

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            /*.addInterceptor(HttpRequestInterceptor())*/
            .build()
    }

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}