package com.bignerdranch.android.criminalintent2.database

import androidx.room.TypeConverter
import java.util.*

class CrimeTypeConverters {

    /** NOTE : This functions here will convert our types to be used in our dataBase and vice versa.
     * The "from" will convert it to be used in our database and the "to" will be converted back to its original Type. **/

    @TypeConverter
    fun fromDate(date: Date): Long? {
        return date.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long?) : Date? {
        return millisSinceEpoch?.let {
            Date(it)
        }
    }

    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }


    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }
}