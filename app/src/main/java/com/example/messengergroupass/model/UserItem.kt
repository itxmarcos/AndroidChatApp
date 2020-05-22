package com.example.messengergroupass.model

import com.example.messengergroupass.R
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class UserItem(val user:User): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_tv_new_message.text = user.username
    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
}
