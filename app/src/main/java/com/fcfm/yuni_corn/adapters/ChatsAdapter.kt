package com.fcfm.yuni_corn.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.yuni_corn.ChatActivity
import com.fcfm.yuni_corn.R
import com.fcfm.yuni_corn.models.Chats
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_chat_layout.view.*
import java.text.SimpleDateFormat
import java.util.*

class ChatsAdapter(private val listaChats: MutableList<Chats>, val contexto: Context) :
    RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder>() {

    class  ChatsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        fun asignarInformacion(chat: Chats, contexto: Context){
            itemView.tvGroupName.text = chat.title
            itemView.tvMessageC.text = chat.lastMessage
            itemView.tvDateC.text = SimpleDateFormat("dd/MM/yyyy", Locale("es", "MX"))
                .format(chat.date)
            Picasso.get().load(chat.image)
                .placeholder(R.drawable.ic_baseline_group_24)
                .error(R.drawable.ic_baseline_group_24)
                .into(itemView.ivImageGroupG)

            if(chat.type == 1){
                //Asignar informacion
                itemView.tvGroupName.text = chat.title
                itemView.tvMessageC.text = chat.lastMessage
                itemView.tvDateC.text = SimpleDateFormat("dd/MM/yyyy", Locale("es", "MX"))
                    .format(chat.date)
            }

            itemView.itemChatContainer.setOnClickListener {
                val activity = Intent(contexto, ChatActivity::class.java)

                activity.putExtra("TITLE_CHAT", chat.title)
                activity.putExtra("UID_CHAT", chat.uid)
                activity.putExtra("IMAGE_CHAT", chat.image)
                /*
                activity.putExtra("UID", UID)
                activity.putExtra("USER", USER)
                */
                (contexto as Activity).startActivity(activity)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder {
        return  ChatsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        holder.asignarInformacion(listaChats[position], contexto)
    }

    override fun getItemCount(): Int = listaChats.size
}