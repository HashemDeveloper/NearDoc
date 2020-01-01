package com.project.neardoc.model.localstoragemodels

import androidx.room.Embedded
import androidx.room.Relation
import com.project.neardoc.model.VisitAddress


data class PracticeAndRelations(
    @Embedded
    val docPractice: DocPractice,
    @Relation(parentColumn = "id", entityColumn = "practice_id")
    val insuranceUidList: List<DocPracticeInsuranceUidList> = emptyList(),
    @Relation(parentColumn = "id", entityColumn = "visit_address_practice_id")
    val visitAddress: PracticeVisitAddress,
    @Relation(parentColumn = "id", entityColumn = "practice_id")
    val officeHoursList: List<DocPracticeOfficeHrList>,
    @Relation(parentColumn = "id", entityColumn = "practice_id")
    val phoneList: List<DocPracticePhoneList>
)