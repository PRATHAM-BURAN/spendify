package com.example.spendify.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import com.example.spendify.domain.model.Transaction
import com.example.spendify.domain.model.TransactionType
import java.io.File
import java.io.FileOutputStream

object PdfExporter {

    fun exportSummaryPdf(
        context: Context,
        transactions: List<Transaction>,
        periodTitle: String,
        currencySymbol: String = "$",
        fileName: String = "spendify_report_${System.currentTimeMillis()}.pdf"
    ): File {
        val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(exportDir, fileName)

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Standard A4 (595 x 842 points)
        val page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        // Background: Deep dark #13131b
        val bgPaint = Paint().apply { color = Color.parseColor("#13131B") }
        canvas.drawRect(0f, 0f, 595f, 842f, bgPaint)

        val titlePaint = Paint().apply {
            color = Color.parseColor("#C0C1FF")
            textSize = 24f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val subtitlePaint = Paint().apply {
            color = Color.parseColor("#C7C4D7")
            textSize = 12f
            isAntiAlias = true
        }

        val bodyPaint = Paint().apply {
            color = Color.parseColor("#E4E1ED")
            textSize = 10f
            isAntiAlias = true
        }

        val boldBodyPaint = Paint().apply {
            color = Color.parseColor("#E4E1ED")
            textSize = 10f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val incomePaint = Paint().apply {
            color = Color.parseColor("#4EDEA3")
            textSize = 10f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val expensePaint = Paint().apply {
            color = Color.parseColor("#FFB4AB")
            textSize = 10f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val cardPaint = Paint().apply {
            color = Color.parseColor("#1F1F27")
            style = Paint.Style.FILL
        }

        val borderPaint = Paint().apply {
            color = Color.parseColor("#464554")
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }

        // Draw Header
        canvas.drawText("Spendify Financial Report", 40f, 50f, titlePaint)
        canvas.drawText("Period: $periodTitle | Generated: ${DateUtils.formatDateOnly(System.currentTimeMillis())}", 40f, 70f, subtitlePaint)

        // Calculate Totals
        val totalIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val totalExpense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val netBalance = totalIncome - totalExpense

        // Draw Summary Bento Cards
        // Card 1: Total Income
        val card1 = RectF(40f, 90f, 195f, 145f)
        canvas.drawRoundRect(card1, 8f, 8f, cardPaint)
        canvas.drawRoundRect(card1, 8f, 8f, borderPaint)
        canvas.drawText("Total Income", 50f, 110f, subtitlePaint)
        canvas.drawText("+$currencySymbol${String.format("%.2f", totalIncome)}", 50f, 132f, incomePaint.apply { textSize = 14f })

        // Card 2: Total Expense
        val card2 = RectF(205f, 90f, 360f, 145f)
        canvas.drawRoundRect(card2, 8f, 8f, cardPaint)
        canvas.drawRoundRect(card2, 8f, 8f, borderPaint)
        canvas.drawText("Total Expense", 215f, 110f, subtitlePaint)
        canvas.drawText("-$currencySymbol${String.format("%.2f", totalExpense)}", 215f, 132f, expensePaint.apply { textSize = 14f })

        // Card 3: Net Balance
        val card3 = RectF(370f, 90f, 555f, 145f)
        canvas.drawRoundRect(card3, 8f, 8f, cardPaint)
        canvas.drawRoundRect(card3, 8f, 8f, borderPaint)
        canvas.drawText("Net Balance", 380f, 110f, subtitlePaint)
        val netPaint = if (netBalance >= 0) incomePaint else expensePaint
        canvas.drawText("${if (netBalance >= 0) "+" else ""}$currencySymbol${String.format("%.2f", netBalance)}", 380f, 132f, netPaint)

        // Table Header
        var yPos = 175f
        canvas.drawRect(40f, yPos - 15f, 555f, yPos + 10f, cardPaint)
        canvas.drawText("DATE", 45f, yPos, subtitlePaint)
        canvas.drawText("CATEGORY", 120f, yPos, subtitlePaint)
        canvas.drawText("NOTE / DETAILS", 240f, yPos, subtitlePaint)
        canvas.drawText("PAYMENT", 410f, yPos, subtitlePaint)
        canvas.drawText("AMOUNT", 490f, yPos, subtitlePaint)

        // Draw Divider
        canvas.drawLine(40f, yPos + 12f, 555f, yPos + 12f, borderPaint)

        yPos += 28f

        // Draw Table Rows (up to fit on page)
        val rowsToShow = transactions.take(22)
        for (t in rowsToShow) {
            val dateStr = DateUtils.formatShortDate(t.dateMillis)
            canvas.drawText(dateStr, 45f, yPos, bodyPaint)
            canvas.drawText(t.categoryName.take(18), 120f, yPos, boldBodyPaint)
            canvas.drawText(t.note.ifBlank { "-" }.take(26), 240f, yPos, bodyPaint)
            canvas.drawText(t.paymentMethod.displayName, 410f, yPos, bodyPaint)

            val amountStr = if (t.type == TransactionType.INCOME) {
                "+$currencySymbol${String.format("%.2f", t.amount)}"
            } else {
                "-$currencySymbol${String.format("%.2f", t.amount)}"
            }
            val amtPaint = if (t.type == TransactionType.INCOME) incomePaint.apply { textSize = 10f } else expensePaint.apply { textSize = 10f }
            canvas.drawText(amountStr, 490f, yPos, amtPaint)

            canvas.drawLine(40f, yPos + 8f, 555f, yPos + 8f, Paint().apply {
                color = Color.parseColor("#292932")
                strokeWidth = 0.5f
            })

            yPos += 24f
        }

        if (transactions.size > rowsToShow.size) {
            canvas.drawText("... and ${transactions.size - rowsToShow.size} more transactions", 45f, yPos + 10f, subtitlePaint)
        }

        // Footer
        canvas.drawText("Spendify App - Personal Financial Management", 40f, 810f, subtitlePaint)
        canvas.drawText("Page 1 / 1", 510f, 810f, subtitlePaint)

        document.finishPage(page)

        FileOutputStream(file).use { out ->
            document.writeTo(out)
        }
        document.close()

        return file
    }
}
