package com.project.neardoc.model.localstoragemodels

import androidx.room.Embedded
import androidx.room.Relation


data class DocProfileAndProfileLanguage(
    @Embedded
    val docProfile: DocProfile,
    @Relation(parentColumn = "id", entityColumn = "profile_id")
    val profileLanguages: List<DocProfileLanguage> = emptyList()
)