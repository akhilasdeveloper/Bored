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

    @Query("UPDATE bored_table SET isCompleted = :isCompleted WHERE id = :id")
    fun updateIsCompleted(id:Int, isCompleted:Boolean)

    @Query("SELECT count(*) FROM bored_table WHERE `key` = :key AND isCompleted = :isCompleted")
    fun countOfNotCompletedActivities(key:String, isCompleted:Boolean = false):Int

    @Query("UPDATE bored_table SET createdDate = :createdDate WHERE `key` = :key AND isCompleted = :isCompleted")
    fun updateCreatedDateOfNotCompletedActivity(key:String, createdDate:Long, isCompleted:Boolean = false)

    @Query("UPDATE bored_table SET createdDate = :createdDate WHERE id = :id")
    fun updateCreatedDate(id:Int, createdDate:Long)

    @Query("DELETE FROM bored_table WHERE `key` = :key AND isCompleted = :isCompleted")
    fun deleteAllNotCompletedActivity(key:String, isCompleted:Boolean = false)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addActivity(boredTable: BoredTable)

}
