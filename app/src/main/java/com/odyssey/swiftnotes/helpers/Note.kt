package com.odyssey.swiftnotes.helpers

/**
 * Generated by ObscuriaAndroidDAOGenerator
 *
 */

class Note {
    var id: Long = 0
    var titre: String? = null
    var date: String? = null
    var description: String? = null
    var alarmStatus: String? = null
    var idcateg: Long = 0


    constructor() {}

    constructor(id: Long, titre: String, date: String, description: String, idcateg: Long, alarmStatus: String) {
        this.id = id
        this.titre = titre
        this.date = date
        this.description = description
        this.idcateg = idcateg
        this.alarmStatus = alarmStatus
    }

}