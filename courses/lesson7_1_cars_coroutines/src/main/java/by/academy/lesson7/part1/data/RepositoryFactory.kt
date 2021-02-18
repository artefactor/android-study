package by.academy.lesson7.part1.data

import android.content.Context
import by.academy.lesson7.part1.data.DatabaseInfo.Companion.init
import kotlinx.coroutines.CoroutineScope

class RepositoryFactory {

    fun getRepository(context: Context, scope: CoroutineScope): AbstractDataRepository = DatabaseRepository(init(context).value, scope)
}