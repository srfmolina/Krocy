package com.srfmolina.krocy.data.mapper

import com.srfmolina.krocy.domain.model.common.ConsumptionDate
import com.srfmolina.krocy.domain.model.common.ConsumptionType
import com.srfmolina.krocy.domain.model.stock.StockItem
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.openapitools.client.models.CurrentStockResponse
import kotlin.time.Clock

private val NEVER_EXPIRES = LocalDate(2999, 12, 31)

fun CurrentStockResponse.toDomain(): StockItem {
    val amount = amount ?: 0.0
    val amountOpened = amountOpened ?: 0.0
    val minStock = product?.minStockAmount ?: 0.0

    val hints = buildList {
        if (amountOpened > 0) add("${amountOpened.toInt()} abierto")
        if (minStock > 0 && amount < minStock) add("bajo stock")
    }

    val consumptionDate = bestBeforeDate
        ?.takeUnless { it == NEVER_EXPIRES }
        ?.let {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            ConsumptionDate(
                type = ConsumptionType.EXPIRATION,
                date = LocalDateTime(it, LocalTime(0, 0)),
                expired = it < today
            )
        }

    return StockItem(
        id = productId ?: 0,
        name = product?.name.orEmpty(),
        hints = hints,
        consumptionDate = consumptionDate,
        quantity = "${amount.formatAmount()} uds"
    )
}

private fun Double.formatAmount(): String =
    if (this % 1.0 == 0.0) this.toInt().toString() else this.toString()
