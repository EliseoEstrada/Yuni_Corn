package com.fcfm.yuni_corn.adapters

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.yuni_corn.models.Messages
import com.fcfm.yuni_corn.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_message_layout.view.*
import kotlinx.android.synthetic.main.item_user_layout.view.*
import java.text.SimpleDateFormat
import java.util.*

class MessagesAdapter(private val listaMensajes: MutableList<Messages>, val uidUser: String) :
    RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder>() {

    class MessagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        @SuppressLint("ResourceAsColor")
        fun asignarInformacion(mensaje: Messages, uidUser: String){
            itemView.tvUserM.text = mensaje.user
            val content = mensaje.content
            itemView.tvDateM.text = SimpleDateFormat("hh:mm", Locale("es", "MX"))
                .format(mensaje.date)


            if(mensaje.type == "text"){
                itemView.tvContentM.text = content
            }
            if(mensaje.type == "image"){
                /*
                itemView.ivImageContentM.visibility = View.VISIBLE
                itemView.tvContentM.visibility = View.GONE

                Picasso.get().load(content)
                    .placeholder(R.drawable.ic_baseline_image_24)
                    .error(R.drawable.ic_baseline_image_24)
                    .into(itemView.ivImageContentM)

                 */
                itemView.tvContentM.text = "IMAGE"
            }


            if(uidUser == mensaje.uidUser){

                val params = itemView.msgContainerM.layoutParams
                val newParams = FrameLayout.LayoutParams(
                    params.width,
                    params.height,
                    Gravity.END,
                )
                itemView.msgContainerM.layoutParams = newParams
                itemView.lyMessageBG.setBackgroundResource(R.drawable.rounded2)
                itemView.tvUserM.isVisible = false
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder {
        return MessagesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_message_layout, parent,false)
        )
    }

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        holder.asignarInformacion(listaMensajes[position], uidUser)
    }

    override fun getItemCount(): Int = listaMensajes.size
}

