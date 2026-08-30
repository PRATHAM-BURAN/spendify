package com.example.spendify.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    fun formatDisplayDate(dateMillis: Long): String {
        val calendar = Calendar.getInstance()
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

        calendar.timeInMillis = dateMillis

        return when {
            isSameDay(calendar, today) -> "Today, ${formatTime(dateMillis)}"
            isSameDay(calendar, yesterday) -> "Yesterday, ${formatTime(dateMillis)}"
            calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) -> {
                SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()).format(Date(dateMillis))
            }
            else -> {
                SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(dateMillis))
            }
        }
    }

    fun formatDateOnly(dateMillis: Long): String {
        return SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(dateMillis))
    }

    fun formatShortDate(dateMillis: Long): String {
        return SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(dateMillis))
    }

    fun formatMonthYear(dateMillis: Long): String {
        return SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date(dateMillis))
    }

    fun formatMonthShort(dateMillis: Long): String {
        return SimpleDateFormat("MMM", Locale.getDefault()).format(Date(dateMillis))
    }

    fun formatTime(dateMillis: Long): String {
        return SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(dateMillis))
    }

    fun getDateGroupHeader(dateMillis: Long): String {
        val calendar = Calendar.getInstance().apply { timeInMillis = dateMillis }
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

        return when {
            isSameDay(calendar, today) -> "Today"
            isSameDay(calendar, yesterday) -> "Yesterday"
            calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) -> {
                SimpleDateFormat("MMMM dd", Locale.getDefault()).format(Date(dateMillis))
            }
            else -> {
                SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(dateMillis))
            }
        }
    }

    fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    fun getCurrentMonthRange(): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val end = cal.timeInMillis

        return Pair(start, end)
    }

    fun getCurrentWeekRange(): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis

        cal.add(Calendar.DAY_OF_WEEK, 6)
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val end = cal.timeInMillis

        return Pair(start, end)
    }

    fun getCurrentYearRange(): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_YEAR, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis

        cal.set(Calendar.MONTH, 11)
        cal.set(Calendar.DAY_OF_MONTH, 31)
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val end = cal.timeInMillis

        return Pair(start, end)
    }

    fun getPast6MonthsRanges(): List<Triple<String, Long, Long>> {
        val list = mutableListOf<Triple<String, Long, Long>>()
        val cal = Calendar.getInstance()

        for (i in 5 downTo 0) {
            val targetCal = (cal.clone() as Calendar).apply {
                add(Calendar.MONTH, -i)
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val start = targetCal.timeInMillis
            val label = SimpleDateFormat("MMM", Locale.getDefault()).format(Date(start))

            targetCal.set(Calendar.DAY_OF_MONTH, targetCal.getActualMaximum(Calendar.DAY_OF_MONTH))
            targetCal.set(Calendar.HOUR_OF_DAY, 23)
            targetCal.set(Calendar.MINUTE, 59)
            targetCal.set(Calendar.SECOND, 59)
            val end = targetCal.timeInMillis

            list.add(Triple(label, start, end))
        }
        return list
    }
}
