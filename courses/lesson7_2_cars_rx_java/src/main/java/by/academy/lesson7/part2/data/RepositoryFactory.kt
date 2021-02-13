package by.academy.lesson7.part2.data

import android.content.Context
import by.academy.lesson7.part2.data.DatabaseInfo.Companion.init

class RepositoryFactory {

    fun getRepository(context: Context): AbstractDataRepository = DatabaseRepository(init(context).value)
}