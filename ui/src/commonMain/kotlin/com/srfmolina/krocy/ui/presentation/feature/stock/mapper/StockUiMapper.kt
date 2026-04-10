package com.srfmolina.krocy.ui.presentation.feature.stock.mapper

import com.srfmolina.krocy.domain.model.common.ConsumptionDate
import com.srfmolina.krocy.domain.model.stock.StockItem
import com.srfmolina.krocy.ui.presentation.common.model.ConsumptionDateUi
import com.srfmolina.krocy.ui.presentation.feature.stock.model.StockItemUi
import java.time.LocalDate
import java.time.temporal.ChronoUnit

internal fun StockItem.toUi(): StockItemUi = StockItemUi(
    id = id,
    name = name,
    hints = hints,
    consumptionDate = consumptionDate?.toUi()
)

private fun ConsumptionDate.toUi(): ConsumptionDateUi = ConsumptionDateUi(
    type = type,
    date = formatRelativeDate(date.toLocalDate()),
    expired = expired
)

private fun formatRelativeDate(date: LocalDate): String {
    val today = LocalDate.now()
    val days = ChronoUnit.DAYS.between(today, date)
    return when {
        days == 0L   -> "Hoy"
        days == 1L   -> "Mañana"
        days == -1L  -> "Ayer"
        days > 0     -> "En $days días"
        else         -> "Hace ${-days} días"
    }
}
