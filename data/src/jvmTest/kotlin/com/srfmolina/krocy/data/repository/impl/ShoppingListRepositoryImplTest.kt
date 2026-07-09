package com.srfmolina.krocy.data.repository.impl

import com.srfmolina.krocy.data.datasource.remote.generic.GenericEntityDataSource
import com.srfmolina.krocy.data.datasource.remote.shoppinglist.ShoppingListDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.openapitools.client.models.CurrentVolatilStockResponseMissingProductsInner
import org.openapitools.client.models.ObjectsEntityGet200ResponseInner
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ShoppingListRepositoryImplTest {

    private val genericStub = object : GenericEntityDataSource {
        override suspend fun getQuantityUnits() = Result.success(listOf(
            ObjectsEntityGet200ResponseInner(id = 3, name = "pack", namePlural = "packs"),
            ObjectsEntityGet200ResponseInner(id = 6, name = "lata", namePlural = "latas"),
        ))
        override suspend fun getLocations() = Result.success(emptyList<ObjectsEntityGet200ResponseInner>())
        override suspend fun getProductGroups() = Result.success(listOf(
            ObjectsEntityGet200ResponseInner(id = 4, name = "Despensa"),
        ))
        override suspend fun createProduct(body: ObjectsEntityGet200ResponseInner) = Result.success(1)
        override suspend fun getProducts() = Result.success(listOf(
            // Stock unit lata (6), purchase unit pack (3): the mismatch case from grocy.
            ObjectsEntityGet200ResponseInner(id = 1, name = "P7", productGroupId = 4, quIdStock = 6, quIdPurchase = 3),
            ObjectsEntityGet200ResponseInner(id = 2, name = "Pan de molde"),
        ))
        override suspend fun getShoppingLocations() = Result.success(emptyList<ObjectsEntityGet200ResponseInner>())
    }

    /** Stateful stub: created/updated/deleted rows are reflected by later [getItems] calls. */
    private class ShoppingListDataSourceStub(
        var lists: List<ObjectsEntityGet200ResponseInner> = listOf(ObjectsEntityGet200ResponseInner(id = 2)),
        var missing: List<CurrentVolatilStockResponseMissingProductsInner> = emptyList(),
        val items: MutableList<ObjectsEntityGet200ResponseInner> = mutableListOf(),
    ) : ShoppingListDataSource {
        var createdListName: String? = null
        val doneCalls = mutableListOf<Pair<Int, Boolean>>()
        var itemFetches = 0
        private var nextId = 100

        override suspend fun getShoppingLists() = Result.success(lists)

        override suspend fun createShoppingList(name: String): Result<Int> {
            createdListName = name
            return Result.success(2)
        }

        override suspend fun getItems(): Result<List<ObjectsEntityGet200ResponseInner>> {
            itemFetches++
            return Result.success(items.toList())
        }

        override suspend fun getMissingProducts() = Result.success(missing)

        override suspend fun createItem(
            listId: Int,
            productId: Int,
            amount: Double,
            quId: Int?,
            note: String?,
        ): Result<Unit> {
            items += ObjectsEntityGet200ResponseInner(
                id = nextId++,
                shoppingListId = listId,
                productId = productId,
                amount = amount,
                quId = quId,
                note = note,
                done = 0,
            )
            return Result.success(Unit)
        }

        override suspend fun updateItem(
            itemId: Int,
            amount: Double,
            quId: Int?,
            note: String?,
        ): Result<Unit> {
            val index = items.indexOfFirst { it.id == itemId }
            items[index] = items[index].copy(
                amount = amount,
                quId = quId ?: items[index].quId,
                note = note ?: items[index].note,
            )
            return Result.success(Unit)
        }

        override suspend fun setDone(itemId: Int, done: Boolean): Result<Unit> {
            doneCalls += itemId to done
            return Result.success(Unit)
        }

        override suspend fun deleteItem(itemId: Int): Result<Unit> {
            items.removeAll { it.id == itemId }
            return Result.success(Unit)
        }
    }

    private fun missing(productId: Int, amount: Double) =
        CurrentVolatilStockResponseMissingProductsInner(id = productId, amountMissing = amount)

    private fun autoRow(id: Int, productId: Int, amount: Double, quId: Int?, done: Int = 0) =
        ObjectsEntityGet200ResponseInner(
            id = id, shoppingListId = 2, productId = productId, amount = amount, quId = quId, done = done,
        )

    @Test
    fun `a deficit without a row is added in stock units and joined with master data`() = runBlocking {
        val stub = ShoppingListDataSourceStub(missing = listOf(missing(productId = 1, amount = 3.0)))
        val repo = ShoppingListRepositoryImpl(stub, genericStub)

        val entries = repo.getShoppingList().first()

        val entry = entries.single()
        assertEquals("P7", entry.productName)
        assertEquals("Despensa", entry.groupName)
        assertEquals(3.0, entry.amount)
        // Deficits are in stock units, so the row must carry the stock unit, not the purchase one.
        assertEquals("lata", entry.unitName)
        assertEquals("latas", entry.unitNamePlural)
        assertFalse(entry.done)
    }

    @Test
    fun `a satisfied row is removed on sync`() = runBlocking {
        // The stale row grocy's add-missing-products leaves behind after a purchase.
        val stub = ShoppingListDataSourceStub(
            missing = emptyList(),
            items = mutableListOf(autoRow(id = 21, productId = 1, amount = 3.0, quId = 3)),
        )
        val repo = ShoppingListRepositoryImpl(stub, genericStub)

        val entries = repo.getShoppingList().first()

        assertTrue(entries.isEmpty())
        assertTrue(stub.items.isEmpty())
    }

    @Test
    fun `a stale amount and purchase-unit stamp are corrected on sync`() = runBlocking {
        // Row says 3 packs (grocy's unit mismatch); the real deficit is 1 lata.
        val stub = ShoppingListDataSourceStub(
            missing = listOf(missing(productId = 1, amount = 1.0)),
            items = mutableListOf(autoRow(id = 21, productId = 1, amount = 3.0, quId = 3)),
        )
        val repo = ShoppingListRepositoryImpl(stub, genericStub)

        val entry = repo.getShoppingList().first().single()

        assertEquals(1.0, entry.amount)
        assertEquals("lata", entry.unitName)
    }

    @Test
    fun `manual rows are preserved and crossed-off manual rows are cleared on sync`() = runBlocking {
        val stub = ShoppingListDataSourceStub(
            missing = emptyList(),
            items = mutableListOf(
                autoRow(id = 30, productId = 1, amount = 2.0, quId = 6).copy(note = "krocy:manual"),
                autoRow(id = 31, productId = 2, amount = 1.0, quId = null, done = 1).copy(note = "krocy:manual"),
            ),
        )
        val repo = ShoppingListRepositoryImpl(stub, genericStub)

        val entries = repo.getShoppingList().first()

        // The pending manual row survives although its product has no deficit...
        assertEquals(listOf(30), entries.map { it.id })
        // ...and the crossed-off one was bought: cleared.
        assertEquals(listOf(30), stub.items.map { it.id })
    }

    @Test
    fun `a deficit for a manually listed product does not create a second row`() = runBlocking {
        val stub = ShoppingListDataSourceStub(
            missing = listOf(missing(productId = 1, amount = 3.0)),
            items = mutableListOf(
                autoRow(id = 30, productId = 1, amount = 5.0, quId = 6).copy(note = "krocy:manual"),
            ),
        )
        val repo = ShoppingListRepositoryImpl(stub, genericStub)

        val entries = repo.getShoppingList().first()

        val entry = entries.single()
        assertEquals(30, entry.id)
        assertEquals(5.0, entry.amount)
    }

    @Test
    fun `manual add creates a marked row with the product's stock unit`() = runBlocking {
        val stub = ShoppingListDataSourceStub()
        val repo = ShoppingListRepositoryImpl(stub, genericStub)
        repo.getShoppingList().first()

        repo.addProduct(productId = 1, amount = 2.0)

        val row = stub.items.single()
        assertEquals("krocy:manual", row.note)
        assertEquals(6, row.quId)
        assertEquals(2.0, row.amount)
        assertEquals(2.0, repo.getShoppingList().first().single().amount)
    }

    @Test
    fun `manual add merges into an existing row and claims it as manual`() = runBlocking {
        val stub = ShoppingListDataSourceStub(
            missing = listOf(missing(productId = 1, amount = 3.0)),
            items = mutableListOf(autoRow(id = 21, productId = 1, amount = 3.0, quId = 6)),
        )
        val repo = ShoppingListRepositoryImpl(stub, genericStub)
        repo.getShoppingList().first()

        repo.addProduct(productId = 1, amount = 2.0)

        val row = stub.items.single()
        assertEquals(5.0, row.amount)
        assertEquals("krocy:manual", row.note)
    }

    @Test
    fun `creates a shopping list when the server has none`() = runBlocking {
        val stub = ShoppingListDataSourceStub(lists = emptyList())
        val repo = ShoppingListRepositoryImpl(stub, genericStub)

        repo.getShoppingList().first()

        assertEquals("Lista de la compra", stub.createdListName)
    }

    @Test
    fun `setDone updates the row and patches the cache without refetching`() = runBlocking {
        val stub = ShoppingListDataSourceStub(missing = listOf(missing(productId = 1, amount = 3.0)))
        val repo = ShoppingListRepositoryImpl(stub, genericStub)
        val entryId = repo.getShoppingList().first().single().id
        val fetchesAfterLoad = stub.itemFetches

        repo.setDone(entryId = entryId, done = true)

        assertEquals(listOf(entryId to true), stub.doneCalls)
        assertTrue(repo.getShoppingList().first().single().done)
        assertEquals(fetchesAfterLoad, stub.itemFetches)
    }

    @Test
    fun `rows of other shopping lists are not touched`() = runBlocking {
        val otherListRow = autoRow(id = 40, productId = 1, amount = 9.0, quId = 3)
            .copy(shoppingListId = 7)
        val stub = ShoppingListDataSourceStub(
            missing = emptyList(),
            items = mutableListOf(otherListRow),
        )
        val repo = ShoppingListRepositoryImpl(stub, genericStub)

        val entries = repo.getShoppingList().first()

        assertTrue(entries.isEmpty())
        assertEquals(listOf(40), stub.items.map { it.id })
    }
}
