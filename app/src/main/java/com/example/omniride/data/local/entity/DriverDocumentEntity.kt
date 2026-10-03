package com.example.omniride.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.omniride.data.model.DocumentType
import com.example.omniride.data.model.KycStatus

@Entity(tableName = "driver_documents")
data class DriverDocumentEntity(
    @PrimaryKey
    val id: String,
    val driverId: String,
    val driverName: String,
    val docType: DocumentType,
    val title: String,
    val documentNumberMasked: String, // e.g. "DL-****-8821" - security rule: never expose raw full SSN/CNIC publicly
    val documentUri: String,
    val status: KycStatus = KycStatus.PENDING,
    val reviewNotes: String = "",
    val uploadedAt: Long = System.currentTimeMillis(),
    val reviewedAt: Long? = null
)
