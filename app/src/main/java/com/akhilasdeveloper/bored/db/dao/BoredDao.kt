package com.akhilasdeveloper.bored.db.dao

import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.room.*
import com.akhilasdeveloper.bored.db.table.BoredTable
import com.akhilasdeveloper.bored.util.Constants

@Dao
interface BoredDao {

    @Query("SELECT * FROM bored_table ORDER BY createdDate DESC")
    fun getAllActivities(): PagingSource<Int, BoredTable>

    @Query("SELECT * FROM bored_table WHERE state = :state ORDER BY createdDate DESC")
    fun getPassActivities(state:Int = Constants.PASS_SELECTION): PagingSource<Int, BoredTable>

    @Query("SELECT * FROM bored_table WHERE state = :state ORDER BY createdDate DESC")
    fun getAddActivities(state:Int = Constants.ADD_SELECTION): PagingSource<Int, BoredTable>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addActivity(boredTable: BoredTable)

}
