package com.srfmolina.krocy.data.mapper

import com.srfmolina.krocy.data.datasource.remote.stock.model.StockResponseDTO
import com.srfmolina.krocy.domain.model.common.ConsumptionDate
import com.srfmolina.krocy.domain.model.common.ConsumptionType
import com.srfmolina.krocy.domain.model.stock.StockItem
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
private const val NEVER_EXPIRES_PREFIX = "2999-12-31"

fun StockResponseDTO.toDomain(): StockItem {
    val hints = buildList {
        add("${amount.formatAmount()} uds")
        if (amountOpened > 0) add("${amountOpened.toInt()} abierto")
        val minStock = product.minStockAmount ?: 0.0
        if (minStock > 0 && amount < minStock) add("bajo stock")
    }

    val consumptionDate = bestBeforeDate
        ?.takeUnless { it.isBlank() || it.startsWith(NEVER_EXPIRES_PREFIX) }
        ?.let { parseConsumptionDate(it) }

    return StockItem(
        id = productId,
        name = product.name,
        hints = hints,
        consumptionDate = consumptionDate
    )
}

private fun Double.formatAmount(): String =
    if (this % 1.0 == 0.0) this.toInt().toString() else this.toString()

private fun parseConsumptionDate(dateStr: String): ConsumptionDate? {
    return try {
        val date = LocalDate.parse(dateStr.take(10), DATE_FORMATTER)
        val today = LocalDate.now()
        ConsumptionDate(
            type = ConsumptionType.EXPIRATION,
            date = LocalDateTime.of(date, java.time.LocalTime.MIDNIGHT),
            expired = !date.isAfter(today)
        )
    } catch (_: Exception) {
        null
    }
}
