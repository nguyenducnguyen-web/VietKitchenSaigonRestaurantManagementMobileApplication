package com.example.ungdungdatdoanonlinenhvietkitchensg.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lienhe")
data class LienHe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var chuDe: String,
    var noiDung: String,
    val hinhAnh: String?,
    val filetxt: String?,
    val fileExcel: String?
)