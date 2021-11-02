package com.fcfm.yuni_corn.models

class Chats(uid: String, type: Int, title: String, lastMessage: String, date: Any, image: String) {
    var uid: String = uid
        get() = field
        set(value){
            field = value
        }

    var type: Int = type
        get() = field
        set(value){
            field = value
        }

    var title: String = title
        get() = field
        set(value){
            field = value
        }

    var lastMessage: String = lastMessage
        get() = field
        set(value){
            field = value
        }

    var date: Any = date
        get() = field
        set(value){
            field = value
        }

    var image: String = image
        get() = field
        set(value){
            field = value
        }
}