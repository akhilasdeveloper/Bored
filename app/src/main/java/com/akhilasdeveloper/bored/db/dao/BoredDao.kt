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

    @Query("SELECT * FROM bored_table WHERE state = :state ORDER BY isCompleted,createdDate DESC")
    fun getAddActivities(state:Int = Constants.ADD_SELECTION): PagingSource<Int, BoredTable>

    @Query("UPDATE bored_table SET isCompleted = :isCompleted, createdDate = :createdDate WHERE id = :id")
    fun updateIsCompleted(id:Int, isCompleted:Boolean, createdDate:Long = System.currentTimeMillis())

    @Query("UPDATE bored_table SET state = :state, isCompleted = :isCompleted, createdDate = :createdDate WHERE id = :id")
    fun updateState(id:Int, isCompleted:Boolean = false, state: Int, createdDate:Long = System.currentTimeMillis())

    @Query("SELECT count(*) FROM bored_table WHERE `key` = :key AND isCompleted = :isCompleted AND state = :state")
    fun countOfNotCompletedTODOActivities(key:String, isCompleted:Boolean = false, state:Int = Constants.ADD_SELECTION):Int

    @Query("UPDATE bored_table SET createdDate = :createdDate WHERE `key` = :key AND isCompleted = :isCompleted AND state = :state")
    fun updateCreatedDateOfNotCompletedTODOActivity(key:String, createdDate:Long, isCompleted:Boolean = false, state:Int = Constants.ADD_SELECTION)

    @Query("DELETE FROM bored_table WHERE `key` = :key AND isCompleted = :isCompleted AND state = :state")
    fun deleteAllNotCompletedTODOActivities(key:String, isCompleted:Boolean = false, state:Int = Constants.ADD_SELECTION)

    @Query("DELETE FROM bored_table WHERE id = :id")
    fun deleteActivityByID(id: Int)

    @Query("DELETE FROM bored_table WHERE `key` = :key AND state = :state")
    fun deleteAllSkippedActivitiesByKey(key:String, state:Int = Constants.PASS_SELECTION)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addActivity(boredTable: BoredTable)

}
