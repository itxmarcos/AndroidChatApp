package com.example.messengergroupass.messages_management

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.messengergroupass.R
import com.example.messengergroupass.chat_messages.ChatLogActivity
import com.example.messengergroupass.model.ChatMessage
import com.example.messengergroupass.model.User
import com.example.messengergroupass.user_management.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_messenger.*

class MessengerActivity : AppCompatActivity() {

    companion object{
        var currentUser: User?=null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messenger)

        recyclerview_latest_messages.adapter=adapter
        recyclerview_latest_messages.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))

        adapter.setOnItemClickListener{ item, view ->
            val intent= Intent(this, ChatLogActivity::class.java)

            val row = item as LatestMessageRow

            intent.putExtra(NewMessageActivity.USER_KEY,  row.chatSenderUser)
            startActivity(intent)

        }

        listenToNewMessages()

        fetchCurrentUser()

        verifyUserIsLoggedIn()

    }


    val messengerMessagesMap= HashMap<String, ChatMessage>()

    private fun refreshRecyclerViewMessages()
    {
        adapter.clear()
        messengerMessagesMap.values.forEach{
            adapter.add(
                LatestMessageRow(
                    it
                )
            )
        }
    }

    private fun listenToNewMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        val ref= FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage:: class.java) ?: return
                messengerMessagesMap[p0.key!!]= chatMessage
                refreshRecyclerViewMessages()
            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage:: class.java) ?: return
                messengerMessagesMap[p0.key!!]= chatMessage
                refreshRecyclerViewMessages()
            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {}
            override fun onCancelled(p0: DatabaseError) {}

        })
    }

    val adapter= GroupAdapter<ViewHolder>()

    private fun fetchCurrentUser()
    {
        val uid = FirebaseAuth.getInstance().uid
        val ref= FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                currentUser =p0.getValue(User::class.java)
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })
    }


    private fun verifyUserIsLoggedIn()
    {
        val uid= FirebaseAuth.getInstance().uid
        if(uid==null)
        {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }





    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item?.itemId)
        {
            R.id.menu_new_message ->{
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out ->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, MainActivity::class.java)
                //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.nav_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
}
