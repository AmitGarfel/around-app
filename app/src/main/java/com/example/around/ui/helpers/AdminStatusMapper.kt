package com.example.around.ui.helpers

object AdminStatusMapper {

    fun toStatus(isApproved: Boolean): String {
        return if (isApproved) "approved" else "rejected"
    }
}