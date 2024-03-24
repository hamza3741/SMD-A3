package com.hamzakhalid.i210704

data class MentorReview(
    val mentorName: String = "",
    val feedback: String = ""
) {
    // No-argument constructor
    constructor() : this("", "")
}