package common.neighbour.nearhud.ui.setting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import common.neighbour.nearhud.R
import common.neighbour.nearhud.api.BaseDataSource
import common.neighbour.nearhud.application.MyApplication
import common.neighbour.nearhud.retrofit.model.post.Data

class ViewPagerProfileAdapter(
    private val userId: String,
    private val lifecycle: Lifecycle,
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //private val db: AppDB = MyApplication.getInstance()!!.getAppDatabase()
    private var imageVidOnly = mutableListOf<Data>()
    private var textOnly = mutableListOf<Data>()

    private val TAG = "ViewPagerProfileAdapter"
    lateinit var postGridAdapter: PostGridAdapter
    private val postTextAdapter = PostTextAdapter(context,object :PostTextAdapter.OnClickInterface{
        override fun onCommentClick(postData: MutableList<Data>, position: Int) {

        }

    })

    private val repo = MyApplication.getInstance()!!.getNetworkRepo()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_view_pager_search,
            parent,
            false
        )
        val vh = ItemHolder(view)
        return vh
    }

    override fun getItemCount(): Int {
        return 2
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemHolder).bind(position)
    }

    inner class ItemHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(position: Int) {
            view.apply {
                when (position) {
                    0 -> {
                        val rcSearch = view.findViewById<RecyclerView>(R.id.rvSearchVPItem)
                        val llNoPost = view.findViewById<LinearLayout>(R.id.llNoPost)
                        val ivNoPost = view.findViewById<ImageView>(R.id.ivNoPost)
                        val tvNoPostTitle = view.findViewById<TextView>(R.id.tvNoPostTitle)
                        val tvNoPostDes = view.findViewById<TextView>(R.id.tvNoPostDes)

                        postGridAdapter = PostGridAdapter(userId,context)
                        rcSearch.adapter = postGridAdapter
                        rcSearch.isNestedScrollingEnabled = false
                        rcSearch.layoutManager = GridLayoutManager(context, 3)

                        repo.getMyPost(userId).observe(context as LifecycleOwner,{
                            when (it.status) {
                                BaseDataSource.Resource.Status.SUCCESS -> {
                                    imageVidOnly.clear()
                                    for (item in it.data!!.data){
                                        if (item.image == "" && item.video ==""){

                                        }
                                        else{
                                            imageVidOnly.add(item)
                                        }
                                    }

                                    postGridAdapter!!.setData(imageVidOnly)
                                    postGridAdapter!!.notifyDataSetChanged()
                                    if (it.data.data.isNullOrEmpty()) {
                                        llNoPost.visibility = View.VISIBLE
                                        ivNoPost.setImageDrawable(resources.getDrawable(R.drawable.svg_tab_images_active))
                                        tvNoPostTitle.text = "No memories yet shared."
                                        tvNoPostDes.text = "Stay tuned."
                                        rcSearch.visibility = View.GONE
                                    } else {
                                        llNoPost.visibility = View.GONE
                                        rcSearch.visibility = View.VISIBLE
                                    }
                                }
                            }
                        })
                    }

                    1 -> {
                        val rcSearch = view.findViewById<RecyclerView>(R.id.rvSearchVPItem)
                        val llNoPost = view.findViewById<LinearLayout>(R.id.llNoPost)
                        val ivNoPost = view.findViewById<ImageView>(R.id.ivNoPost)
                        val tvNoPostTitle = view.findViewById<TextView>(R.id.tvNoPostTitle)
                        val tvNoPostDes = view.findViewById<TextView>(R.id.tvNoPostDes)

                        rcSearch.adapter = postTextAdapter
                        rcSearch.isNestedScrollingEnabled = false
                        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        rcSearch.layoutManager = layoutManager

                        repo.getMyPost(userId).observe(context as LifecycleOwner,{
                            when (it.status) {
                                BaseDataSource.Resource.Status.SUCCESS -> {
                                    imageVidOnly.clear()
                                    for (item in it.data!!.data){
                                        if (item.image == "" && item.video ==""){
                                            textOnly.add(item)
                                        }
                                    }

                                    postTextAdapter!!.setData(textOnly)
                                    postTextAdapter!!.notifyDataSetChanged()
                                    if (it.data.data.isNullOrEmpty()) {
                                        llNoPost.visibility = View.VISIBLE
                                        ivNoPost.setImageDrawable(resources.getDrawable(R.drawable.svg_tab_images_active))
                                        tvNoPostTitle.text = "No memories yet shared."
                                        tvNoPostDes.text = "Stay tuned."
                                        rcSearch.visibility = View.GONE
                                    } else {
                                        llNoPost.visibility = View.GONE
                                        rcSearch.visibility = View.VISIBLE
                                    }
                                }
                            }
                        })
                    }
                }
            }
        }
    }
}

