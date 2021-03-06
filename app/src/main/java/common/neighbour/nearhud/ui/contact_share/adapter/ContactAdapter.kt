package common.neighbour.nearhud.ui.contact_share.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import common.neighbour.nearhud.R
import common.neighbour.nearhud.database.roomdatabase.tables.ContactList
import common.neighbour.nearhud.databinding.ItemContactsBinding

class ContactAdapter (var from:Boolean,var context: Context,var onSendClick : OnSendInterface) : RecyclerView.Adapter<ContactAdapter.ReplyViewHolder>() {

    private var contactList = ArrayList<common.neighbour.nearhud.retrofit.model.contact_share.ContactList>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyViewHolder {
        var binding: ItemContactsBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_contacts, parent, false)
        return ReplyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReplyViewHolder, position: Int) {
        holder.binding.apply {
            if (from){
                cvShare.visibility= View.VISIBLE
            }
            else{
                cvShare.visibility = View.GONE
            }
            if (position % 2 == 0) {
                recentBackground.setBackgroundColor(context.resources.getColor(R.color.white))
            } else {
                recentBackground.setBackgroundColor(context.resources.getColor(R.color.grey))
            }
            nameOfContact.text = contactList[position].name
            alphabaticaName.text = contactList[position].name
            NumberOfContact.text = contactList[position].number
            cvShare.setOnClickListener {
                onSendClick.send( contactList[position].number)
            }
        }

    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    fun setData(contacts: ArrayList<common.neighbour.nearhud.retrofit.model.contact_share.ContactList>) {
        this.contactList = contacts
    }

    class ReplyViewHolder(var binding: ItemContactsBinding) : RecyclerView.ViewHolder(binding.root)

}
interface OnSendInterface{
    fun send(number:String)
}