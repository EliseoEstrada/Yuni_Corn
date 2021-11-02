package com.fcfm.yuni_corn.models

class Homeworks(
    uid: String,
    title: String,
    description: String,
    startDate: String,
    finishDate: String,
    uidGroup:String,
    group:String
) {
    var uid: String = uid
        get() = field
        set(value){
            field = value
        }
    var title: String = title
        get() = field
        set(value){
            field = value
        }
    var description: String = description
        get() = field
        set(value){
            field = value
        }
    var startDate: String = startDate
        get() = field
        set(value){
            field = value
        }
    var finishDate: String = finishDate
        get() = field
        set(value){
            field = value
        }
    var uidGroup: String = uidGroup
        get() = field
        set(value){
            field = value
        }
    var group: String = group
        get() = field
        set(value){
            field = value
        }
}