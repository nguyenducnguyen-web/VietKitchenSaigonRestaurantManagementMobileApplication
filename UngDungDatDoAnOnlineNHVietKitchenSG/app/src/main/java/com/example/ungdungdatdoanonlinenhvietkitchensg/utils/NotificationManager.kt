package com.example.ungdungdatdoanonlinenhvietkitchensg.utils

object NotificationManager {
    private val notifications = mutableListOf<Pair<String, Int>>()

    fun addNotification(message: String, imageResId: Int) {
        notifications.add(0, Pair(message, imageResId)) // Thêm vào đầu danh sách
    }

    fun getNotifications(): List<Pair<String, Int>> {
        return notifications
    }

    fun clearNotifications() {
        notifications.clear()
    }
}