package com.bignerdranch.android.criminalintent2

import java.util.*

// This is a data class that will hold the list data for the crimes
data class Crime(val id: UUID = UUID.randomUUID(), var title : String = "", var date: Date = Date(), var isSolved: Boolean = false )