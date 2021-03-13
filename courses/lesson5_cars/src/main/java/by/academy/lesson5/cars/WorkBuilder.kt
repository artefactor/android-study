package by.academy.lesson5.cars

class WorkBuilder {
    var carDataItemId: Long = -1;
    var status: Int = WS_IN_PROGRESS
    lateinit var workCost: String
    lateinit var workDescription: String
    lateinit var workName: String

    fun isEmpty(): Boolean {
        return workName.isEmpty() || workDescription.isEmpty() || workCost.isEmpty()
    }
}
