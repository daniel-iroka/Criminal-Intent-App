package com.bignerdranch.android.criminalintent2

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


// This is a data class that will hold the list data for the crimes

//@Entity
// The @Entity annotation is used to signify that a class is structured in a table format in our dataBase where our data will be stored
// Each crime in this class will be in form of a "row" and each property will be a "column", in this case 4 columns
// Our Model class is what is mostly annotated by Entity. @Entity is annotated at the class Level

//@PrimaryKey
// The @PrimaryKey annotation is used to represent that a particular column will hold unique data that for our rows and be used to query or reference
// each entry in our Entity or dataBase
@Entity
data class Crime(@PrimaryKey val id: UUID = UUID.randomUUID(), var title : String = "", var date: Date = Date(),
                 var isSolved: Boolean = false, var suspect: String ="" )