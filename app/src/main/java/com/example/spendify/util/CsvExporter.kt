package com.example.spendify.util

import android.content.Context
import androidx.core.content.FileProvider
import com.example.spendify.domain.model.Transaction
import java.io.File
import java.io.FileWriter

object CsvExporter {

    fun exportTransactionsToCsv(
        context: Context,
        transactions: List<Transaction>,
        fileName: String = "spendify_transactions_${System.currentTimeMillis()}.csv"
    ): File {
        val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(exportDir, fileName)

        FileWriter(file).use { writer ->
            // CSV Header
            writer.append("ID,Date,Type,Category,Amount,Payment Method,Note,Recurring,Frequency\n")

            // Rows
            for (t in transactions) {
                val dateStr = DateUtils.formatDateOnly(t.dateMillis)
                val typeStr = t.type.name
                val categoryStr = escapeCsv(t.categoryName)
                val amountStr = String.format("%.2f", t.amount)
                val methodStr = t.paymentMethod.displayName
                val noteStr = escapeCsv(t.note)
                val isRecurringStr = if (t.isRecurring) "Yes" else "No"
                val freqStr = t.recurrenceFrequency?.displayName ?: "N/A"

                writer.append("$t.id,$dateStr,$typeStr,$categoryStr,$amountStr,$methodStr,$noteStr,$isRecurringStr,$freqStr\n")
            }
        }

        return file
    }

    private fun escapeCsv(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"" + value.replace("\"", "\"\"") + "\""
        } else {
            value
        }
    }
}
