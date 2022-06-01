package common.neighbour.nearhud.api.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import common.neighbour.nearhud.api.AppNetworkRepository
import common.neighbour.nearhud.repositories.constance.AppConstance
import common.neighbour.nearhud.retrofit.model.post.Data
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import java.lang.NullPointerException

class HomeNeighbourPostPaging(private val repo: AppNetworkRepository, private val grpId: String, private val userId: String) :
    PagingSource<Int, Data>() {

    var STARTING_PAGE_INDEX = 1

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Data> {

        val page = params.key ?: STARTING_PAGE_INDEX

        val map = hashMapOf(
            AppConstance.GROUP_ID to grpId,
            AppConstance.USER_ID to userId,
            AppConstance.PAGE to page.toString()
        )

        return try {
            val response = repo.getHomeNeighbourPosts(map)
            val data = response.body()?.data

            if(!data.isNullOrEmpty()){
                LoadResult.Page(
                    data = data,
                    prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                    nextKey = if (data.isEmpty()) null else page + 1
                )
            }else{
                return LoadResult.Error(NullPointerException("Data is Null or Empty"))
            }

        } catch (e: HttpException) {
            return LoadResult.Error(e)
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (exception: Exception) {
            exception.printStackTrace()
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Data>): Int {
        return 0
    }
}