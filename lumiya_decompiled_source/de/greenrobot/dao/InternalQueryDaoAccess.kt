package de.greenrobot.dao

import android.database.Cursor
import de.greenrobot.dao.internal.TableStatements

// Kotlin translation note: parity with InternalQueryDaoAccess.java (no Linkpoint counterpart found).
class InternalQueryDaoAccess<T>(private val dao: AbstractDao<T, *>) {
    companion object {
        @JvmStatic
        fun <T2> getStatements(dao: AbstractDao<T2, *>): TableStatements = dao.statements
    }

    fun getStatements(): TableStatements = dao.statements

    fun loadAllAndCloseCursor(cursor: Cursor): List<T> = dao.loadAllAndCloseCursor(cursor)

    fun loadCurrent(cursor: Cursor, offset: Int, lock: Boolean): T =
        dao.loadCurrent(cursor, offset, lock)

    fun loadUniqueAndCloseCursor(cursor: Cursor): T = dao.loadUniqueAndCloseCursor(cursor)
}
