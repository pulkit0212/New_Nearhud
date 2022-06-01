//package common.neighbour.nearhud.api.pagingsource
//
//import android.content.Context
//import android.util.Log
//import androidx.paging.ExperimentalPagingApi
//import androidx.paging.LoadType
//import androidx.paging.PagingState
//import androidx.paging.RemoteMediator
//import common.neighbour.nearhud.api.local.BaseBean
//import common.neighbour.nearhud.api.AppNetworkRepository
//import common.neighbour.nearhud.api.BaseDataSource
//import common.neighbour.nearhud.api.entity.RemoteKeys
//import common.neighbour.nearhud.application.MyApplication
//import common.neighbour.nearhud.database.roomdatabase.AppDB
//import common.neighbour.nearhud.module.Utility
//import common.neighbour.nearhud.repositories.constance.AppConstance
//import common.neighbour.nearhud.retrofit.model.post.Data
//import common.neighbour.nearhud.retrofit.model.post.PostResponse
//import javax.inject.Inject
//
//@OptIn(ExperimentalPagingApi::class)
//class RM_PostHome @Inject constructor(
//    private val context: Context,
//    private val isState: Boolean,
//    private val grpID: String,
//    private val userID: String
//) :
//    RemoteMediator<Int, Data>() {
//
//    private val networkRepo: AppNetworkRepository = MyApplication.getInstance()!!.getNetworkRepo()
//    private val db: AppDB = MyApplication.getInstance()!!.getAppDatabase()
//    private val postDAO = db.postHomeDAO()
//    private val commentDAO = db.commentDataDAO()
//    private val remoteKeysDAO = db.remoteKeysNewsDao()
//    private val INITIAL_LOAD_KEY = 1
//
//    private val TAG = "RM_PostHome"
//
//    override suspend fun load(
//        loadType: LoadType,
//        state: PagingState<Int, Data>
//    ): MediatorResult {
//        Log.d(TAG, "load 1: ${loadType.name}")
//        when (loadType) {
//            LoadType.REFRESH -> {
//                remoteKeysDAO.insert(RemoteKeys(2, 1, 2))
//            }
//            LoadType.APPEND -> {
//                var remotekey = remoteKeysDAO.getRemoteKeyFor(2)
//                if (remotekey != null) {
//                    remotekey.prevKey = remotekey!!.nextKey
//                    remotekey.nextKey = remotekey.nextKey!!.plus(1)
//                }
//                remoteKeysDAO.insert(remotekey!!)
//                Log.d(TAG, "load 2: Getting Page ${remotekey!!.prevKey}")
//            }
//            LoadType.PREPEND -> {
//                Log.d(TAG, "load 3: EOP")
//                return MediatorResult.Success(endOfPaginationReached = true)
//            }
//        }
//        val remotekey = remoteKeysDAO.getRemoteKeyFor(2)
//        val response = networkRepo.getHomePost(isState,remotekey!!.prevKey!!,userID,grpID) as BaseDataSource.Resource<PostResponse>
//        return when (response.status) {
//            BaseDataSource.Resource.Status.ERROR -> {
//                Log.d(TAG, "load 5: Error")
//                MediatorResult.Error(Throwable(""))
//            }
//            BaseDataSource.Resource.Status.SUCCESS -> {
//                Log.d(TAG, "load 6: SUCCESS")
//                val data = response.data
//                val remKey = remoteKeysDAO.getRemoteKeyFor(2)
//                if (remKey!!.prevKey == 1) {
//                    //TODO clear Database and insert new entries
//                    postDAO.clearPostHome()
//                    //postDAO.insert(Utility.getPostHomeObject(-1))
//                    //postDAO.insert(Utility.getPostHomeObject(-2))
//                    //postDAO.insert(Utility.getPostHomeObject(-3))
//                    Log.d(TAG, "load 8: EMPTY INSERTED")
//                }
//                println(data)
//                for (item in data!!.data) {
//                    Log.d(TAG, "load 9: ENTER INSERTED")
//                    println(item)
//                    postDAO.insert(item)
//                   // AppConstance.LAST_POST_ID = data.lastPostId
//
//                    Log.d(TAG, "load 11: ENTER INSERTED")
//                    if (item.comment != null) {
//                        Log.d(TAG, "load 12: ENTER INSERTED")
//                        println(item.comment)
//                        commentDAO.insertAll(item.comment!!)
//                    }
//                }
//
//                if (50 < remKey!!.prevKey!!) {
//                    Log.d(TAG, "load 7: EOP")
//                    MediatorResult.Success(endOfPaginationReached = true)
//                }else{
//                    MediatorResult.Success(endOfPaginationReached = false)
//                }
//            }
//            BaseDataSource.Resource.Status.LOADING -> {
//                Log.d(TAG, "load 10: LOADING")
//                MediatorResult.Success(endOfPaginationReached = false)
//            }
//        }
//    }
//}
//
///*if (data!!.results.isNullOrEmpty()) {
//                   Log.d(TAG, "load 9: EOP")
//                   MediatorResult.Success(endOfPaginationReached = true)
//               } else {
//                   MediatorResult.Success(endOfPaginationReached = false)
//               }*/