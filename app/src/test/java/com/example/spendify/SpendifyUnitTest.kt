package com.example.spendify

import com.example.spendify.domain.model.CurrencyOption
import com.example.spendify.domain.model.TransactionType
import com.example.spendify.util.CurrencyFormatter
import com.example.spendify.util.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class SpendifyUnitTest {

    @Test
    fun testCurrencyFormatting() {
        val formatted = CurrencyFormatter.format(12450.00, "$")
        assertEquals("$12,450.00", formatted)

        val signedExpense = CurrencyFormatter.formatSigned(250.50, isIncome = false, currencySymbol = "$")
        assertEquals("-$250.50", signedExpense)

        val signedIncome = CurrencyFormatter.formatSigned(4200.00, isIncome = true, currencySymbol = "$")
        assertEquals("+$4,200.00", signedIncome)
    }

    @Test
    fun testDateRanges() {
        val (startMonth, endMonth) = DateUtils.getCurrentMonthRange()
        assertTrue(endMonth > startMonth)

        val (startWeek, endWeek) = DateUtils.getCurrentWeekRange()
        assertTrue(endWeek > startWeek)

        val headerToday = DateUtils.getDateGroupHeader(System.currentTimeMillis())
        assertEquals("Today", headerToday)

        val past6Months = DateUtils.getPast6MonthsRanges()
        assertEquals(6, past6Months.size)
    }
}
