package com.fcfm.yuni_corn.utils

class UserHomework(
    uid: String,
    uidGroup:String,
    finishDate: String,
    sendDate: String,
    document:String,
    points: Int,
    sent: Boolean
) {
    var uid: String = uid
        get() = field
        set(value){
            field = value
        }
    var uidGroup: String = uidGroup
        get() = field
        set(value){
            field = value
        }
    var finishDate: String = finishDate
        get() = field
        set(value){
            field = value
        }
    var sendDate: String = sendDate
        get() = field
        set(value){
            field = value
        }
    var document: String = document
        get() = field
        set(value){
            field = value
        }
    var points: Int = points
        get() = field
        set(value){
            field = value
        }
    var sent: Boolean = sent
        get() = field
        set(value){
            field = value
        }

}