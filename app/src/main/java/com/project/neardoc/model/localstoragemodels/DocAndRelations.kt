package com.project.neardoc.model.localstoragemodels

import androidx.room.Embedded
import androidx.room.Relation

data class DocAndRelations(
    @Embedded
    val doc: Doc,
    @Relation(parentColumn = "doc_parent_id", entityColumn = "doc_id")
    val docPractice: List<DocPractice>,
    @Relation(parentColumn = "doc_parent_id", entityColumn = "doc_id")
    val docProfile: List<DocProfile>,
    @Relation(parentColumn = "doc_parent_id", entityColumn = "doc_id")
    val docRating: List<DocRatings>
)