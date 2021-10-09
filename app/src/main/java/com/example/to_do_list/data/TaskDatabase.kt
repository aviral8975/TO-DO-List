package com.example.to_do_list.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.to_do_list.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1, )

abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    class Callback @Inject constructor(
            private val database: Provider<TaskDatabase>,
            @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback()
    {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

        //db operations
        val dao = database.get().taskDao()

        applicationScope.launch {
            dao.insert(Task("Wash the dishes"))
            dao.insert(Task("Take a nap", imprtance = true))
            dao.insert(Task("have medicines"))
            dao.insert(Task("Read xvy book ", completed = true))
        }

        }
    }
}