package by.academy.questionnaire

import java.text.SimpleDateFormat
import java.util.Date

fun formatDate(date: Date) = SimpleDateFormat("yyyy-MM-dd hh:mm").format(date)