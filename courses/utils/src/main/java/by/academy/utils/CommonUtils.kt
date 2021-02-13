@file:JvmName("CommonUtils")

package by.academy.utils

import java.text.SimpleDateFormat
import java.util.Date

fun dateFormat(date: Date): String? = SimpleDateFormat.getDateInstance().format(date)

fun formatMoney(cost: Double): String = "$cost $"
fun parseDate(string: String): Date = SimpleDateFormat.getDateInstance().parse(string)



