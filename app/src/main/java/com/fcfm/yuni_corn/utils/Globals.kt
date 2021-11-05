package com.fcfm.yuni_corn.utils

import android.app.Application
import com.fcfm.yuni_corn.models.Rewards
import com.fcfm.yuni_corn.models.Users


class Globals : Application() {
    companion object {
        @JvmField
        //Lista que se usa para agregar miembrosa un grupo o chat grupal
        var listUsersGroup = mutableListOf<Members>()

        //Objeto que guarda al usuario logueado
        lateinit var UserLogged: Users

        //Lista de uids de miembros en un grupo
        var listMembersGroup = mutableListOf<String>()

        //Lista de logros de usuario
        var listRewardsUser = mutableListOf<Rewards>()
    }



}