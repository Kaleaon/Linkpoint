package com.linkpoint.grid.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [GridProfileEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GridDatabase : RoomDatabase() {

    abstract fun gridDirectoryDao(): GridDirectoryDao

    companion object {
        @Volatile
        private var INSTANCE: GridDatabase? = null

        val DEFAULT_PRESET_GRIDS = listOf(
            GridProfileEntity(
                id = "secondlife",
                name = "Second Life (Main Grid)",
                gridNick = "agni",
                loginUri = "https://login.agni.lindenlab.com/cgi-bin/login.cgi",
                helperUri = "https://secondlife.com/helpers/",
                website = "https://secondlife.com",
                status = "online",
                isCustom = false
            ),
            GridProfileEntity(
                id = "secondlife_beta",
                name = "Second Life (Beta - Aditi)",
                gridNick = "aditi",
                loginUri = "https://login.aditi.lindenlab.com/cgi-bin/login.cgi",
                helperUri = "https://secondlife.com/helpers/",
                website = "https://secondlife.com",
                status = "online",
                isCustom = false
            ),
            GridProfileEntity(
                id = "osgrid",
                name = "OSgrid (OpenSim)",
                gridNick = "osgrid",
                loginUri = "http://login.osgrid.org/",
                helperUri = "http://osgrid.org/helpers/",
                website = "https://www.osgrid.org",
                status = "online",
                isCustom = false
            ),
            GridProfileEntity(
                id = "kitely",
                name = "Kitely (OpenSim)",
                gridNick = "kitely",
                loginUri = "https://login.kitely.com/",
                helperUri = "https://www.kitely.com/services/",
                website = "https://www.kitely.com",
                status = "online",
                isCustom = false
            ),
            GridProfileEntity(
                id = "metropolis",
                name = "Metropolis MetaVerse",
                gridNick = "metropolis",
                loginUri = "http://hypergrid.org:8002/",
                helperUri = "http://hypergrid.org/helpers/",
                website = "http://hypergrid.org",
                status = "online",
                isCustom = false
            )
        )

        fun getInstance(context: Context): GridDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GridDatabase::class.java,
                    "linkpoint_grid_directory.db"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed default preset grids into SQLite upon creation
                        CoroutineScope(Dispatchers.IO).launch {
                            getInstance(context).gridDirectoryDao().insertGrids(DEFAULT_PRESET_GRIDS)
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
