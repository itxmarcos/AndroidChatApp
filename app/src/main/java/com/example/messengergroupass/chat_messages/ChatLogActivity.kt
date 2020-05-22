package com.example.messengergroupass.chat_messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.messengergroupass.messages_management.MessengerActivity
import com.example.messengergroupass.messages_management.NewMessageActivity
import com.example.messengergroupass.R
import com.example.messengergroupass.model.ChatMessage
import com.example.messengergroupass.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()
    var toUser: User?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter=adapter

        toUser=intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        supportActionBar?.title = toUser?.username

     listenForMessages()

     send_button_chat.setOnClickListener{
         performSendMessage()
     }
}

    private fun listenForMessages()
    {
        val fromId = FirebaseAuth.getInstance().uid
        val toId= toUser?.uid
        val ref= FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object : ChildEventListener{

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage= p0.getValue(ChatMessage::class.java)

                if(chatMessage!=null)
                {
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid)
                    {
                        val currentUser= MessengerActivity.currentUser
                            ?: return
                        adapter.add(
                            ChatFromItem(
                                chatMessage.text,
                                currentUser!!
                            )
                        )
                    }
                    else
                    {
                        adapter.add(
                            ChatToItem(
                                chatMessage.text,
                                toUser!!
                            )
                        )
                    }
                }
                recyclerview_chat_log.scrollToPosition(adapter.itemCount-1)
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("Not yet implemented")
            }
        })
    }


    private fun performSendMessage()
    {
        val text = edittext_chat_log.text.toString()

        val fromId = FirebaseAuth.getInstance().uid


        val user= intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user.uid

        if(fromId == null) return
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis()/1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                edittext_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount-1)
            }
        toReference.setValue(chatMessage)

        val  messengerFromReference = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        messengerFromReference.setValue(chatMessage)

        val  messengerToReference = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        messengerToReference.setValue(chatMessage)
    }

}


class ChatFromItem(val text: String, val user: User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.tvFromRow.text= text
    }

    override fun getLayout(): Int {
       return R.layout.chat_from_row
    }
}

class ChatToItem(val text: String, val user: User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.tvToRow.text= text
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}
