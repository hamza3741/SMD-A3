package com.hamzakhalid.i210704

/*
data class Appointment(
    val userId: String,
    val userName: String,
    val date: String,
    val timeSlot: String,
    val mentorName: String,
    val mentorDescription: String
)*/


class Appointment(
    var userId: String = "",
    var userName: String=" ",
    var mentorName: String = "",
    var mentorDescription: String = "",
    var date: String = "",
    var timeSlot: String = ""

) {
    // No-argument constructor
    constructor() : this("", "", "", "", "")
}