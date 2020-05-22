package com.example.messengergroupass.model

class ChatMessage(val id:String, val text: String, val fromId:String,val toId:String, val timeSent:Long) {
    constructor() : this("","","","",-1 )
}