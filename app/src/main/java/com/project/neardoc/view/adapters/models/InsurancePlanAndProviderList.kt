package com.project.neardoc.view.adapters.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class InsurancePlanAndProviderList(
    var list: @RawValue MutableList<InsurancePlanAndProvider>
): Parcelable