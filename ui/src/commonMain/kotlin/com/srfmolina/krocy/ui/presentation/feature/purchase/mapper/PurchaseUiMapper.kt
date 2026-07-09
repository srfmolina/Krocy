package com.srfmolina.krocy.ui.presentation.feature.purchase.mapper

import com.srfmolina.krocy.domain.model.product.ProductPurchaseInfo
import com.srfmolina.krocy.ui.presentation.feature.purchase.model.ProductPurchaseInfoUi

internal fun ProductPurchaseInfo.toUi(): ProductPurchaseInfoUi = ProductPurchaseInfoUi(
    purchaseUnitName = purchaseUnitName,
    purchaseUnitNamePlural = purchaseUnitNamePlural,
    stockUnitName = stockUnitName,
    stockUnitNamePlural = stockUnitNamePlural,
    conversionFactorPurchaseToStock = conversionFactorPurchaseToStock,
    lastPrice = lastPrice,
)
