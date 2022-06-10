package common.neighbour.nearhud.ui.contact_share.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import common.neighbour.nearhud.R
import common.neighbour.nearhud.databinding.ReferItemBinding
import common.neighbour.nearhud.retrofit.model.token.ReferedData

class ReferDataAdapter (var context: Context, var onReferClick : OnReferInterface) : RecyclerView.Adapter<ReferDataAdapter.ReplyViewHolder>() {

    private var referList = ArrayList<ReferedData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyViewHolder {
        var binding: ReferItemBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.refer_item, parent, false)
        return ReplyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReplyViewHolder, position: Int) {
        holder.binding.apply {

            grpId.text = referList[position].referredByGroupId
            grpName.text = referList[position].referredByGroupName
            llRoot.setOnClickListener {
                onReferClick.refer(referList[position])
            }
        }

    }

    override fun getItemCount(): Int {
        return referList.size
    }

    fun setData(contacts: ArrayList<ReferedData>) {
        this.referList = contacts
    }

    class ReplyViewHolder(var binding: ReferItemBinding) : RecyclerView.ViewHolder(binding.root)

}
interface OnReferInterface{
    fun refer(referedData: ReferedData)
}