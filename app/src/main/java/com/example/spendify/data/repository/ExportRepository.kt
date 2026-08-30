package com.example.spendify.data.repository

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.spendify.domain.model.Transaction
import com.example.spendify.util.CsvExporter
import com.example.spendify.util.PdfExporter
import java.io.File

class ExportRepository(private val context: Context) {

    fun exportAndShareCsv(transactions: List<Transaction>) {
        val file = CsvExporter.exportTransactionsToCsv(context, transactions)
        shareFile(file, "text/csv", "Share Transactions CSV")
    }

    fun exportAndSharePdf(transactions: List<Transaction>, periodTitle: String, currencySymbol: String) {
        val file = PdfExporter.exportSummaryPdf(context, transactions, periodTitle, currencySymbol)
        shareFile(file, "application/pdf", "Share Financial Report PDF")
    }

    private fun shareFile(file: File, mimeType: String, chooserTitle: String) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Spendify Export")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val chooser = Intent.createChooser(shareIntent, chooserTitle).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(chooser)
    }
}
