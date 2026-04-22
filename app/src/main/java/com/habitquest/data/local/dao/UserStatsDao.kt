package com.habitquest.data.local.dao

import androidx.room.*
import com.habitquest.data.local.entity.UserStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserStatsDao {

    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getUserStats(): Flow<UserStatsEntity?>

    @Query("SELECT * FROM user_stats WHERE id = 1")
    suspend fun getUserStatsOnce(): UserStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(stats: UserStatsEntity)

    @Query("UPDATE user_stats SET totalXP = MAX(0, totalXP + :amount) WHERE id = 1")
    suspend fun addXP(amount: Int)

    @Query("UPDATE user_stats SET userName = :name WHERE id = 1")
    suspend fun updateUserName(name: String)

    @Query("UPDATE user_stats SET userAvatar = :avatar WHERE id = 1")
    suspend fun updateUserAvatar(avatar: String)
}
