package com.project.neardoc.model.localstoragemodels

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import com.project.neardoc.model.HospitalAffiliation
import com.project.neardoc.model.Insurance
import com.project.neardoc.model.Specialty
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue


@Parcelize
data class DocAndRelations(
    @Embedded
    val doc: @RawValue Doc,
    @Relation(parentColumn = "doc_parent_id", entityColumn = "doc_id")
    val docPractice: @RawValue List<DocPractice>,
    @Relation(parentColumn = "doc_parent_id", entityColumn = "doc_id")
    val docProfile: @RawValue List<DocProfile>,
    @Relation(parentColumn = "doc_parent_id", entityColumn = "doc_id")
    val docRating: @RawValue List<DocRatings>,
    @Relation(parentColumn = "doc_parent_id", entityColumn = "doc_id")
    val docSpeciality: @RawValue List<Specialty>,
    @Relation(parentColumn = "doc_parent_id", entityColumn = "doc_id")
    val affiliation: @RawValue HospitalAffiliation?,
    @Relation(parentColumn = "doc_parent_id", entityColumn = "doc_id")
    val insuranceList: @RawValue List<Insurance>?
) : Parcelable