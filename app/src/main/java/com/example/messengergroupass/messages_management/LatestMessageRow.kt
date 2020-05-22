package com.example.messengergroupass.messages_management

import com.example.messengergroupass.R
import com.example.messengergroupass.model.ChatMessage
import com.example.messengergroupass.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_messages_row.view.*

class LatestMessageRow(val chatMessage : ChatMessage): Item<ViewHolder>(){

    var chatSenderUser: User? =null

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.tvLatestMessage.text= chatMessage.text

        val chatSenderId: String
        if(chatMessage.fromId == FirebaseAuth.getInstance().uid)
        {
            chatSenderId=chatMessage.toId
        }
        else
        {
            chatSenderId=chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatSenderId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                chatSenderUser=p0.getValue(User::class.java)
                viewHolder.itemView.tvUsernameLast.text=chatSenderUser?.username
            }
            override fun onCancelled(p0: DatabaseError) {

            }


        })

    }

    override fun getLayout(): Int {
        return R.layout.latest_messages_row
    }

}