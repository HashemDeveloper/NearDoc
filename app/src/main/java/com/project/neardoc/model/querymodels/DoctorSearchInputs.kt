package com.project.neardoc.model.querymodels

import com.project.neardoc.utils.LocalDbInsertionOption

data class DoctorSearchInputs(
    var betterDocApiKey: String,
    var latitude: String,
    var longitude: String,
    var query: String,
    var ataInsertionType: LocalDbInsertionOption
)