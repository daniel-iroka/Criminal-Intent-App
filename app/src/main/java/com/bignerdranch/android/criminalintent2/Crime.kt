package com.bignerdranch.android.criminalintent2

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/** THIS IS THE CHALLENGE VERSION OF CRIMINAL INTENT **/


// This is a data class that will hold the list data for the crimes
@Entity
data class Crime(@PrimaryKey val id: UUID = UUID.randomUUID(), var requiresPolice: Int = 2, var title : String = "", var date: Date = Date(), var isSolved: Boolean = false,
                 var contactPolice: String = "Contact Police")