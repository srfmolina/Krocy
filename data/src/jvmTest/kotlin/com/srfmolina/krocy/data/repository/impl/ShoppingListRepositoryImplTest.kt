package com.srfmolina.krocy.data.repository.impl

import com.srfmolina.krocy.data.datasource.remote.generic.GenericEntityDataSource
import com.srfmolina.krocy.data.datasource.remote.shoppinglist.ShoppingListDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.openapitools.client.models.ObjectsEntityGet200ResponseInner
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ShoppingListRepositoryImplTest {

    private val genericStub = object : GenericEntityDataSource {
        override suspend fun getQuantityUnits() = Result.success(listOf(
            ObjectsEntityGet200ResponseInner(id = 3, name = "brik", namePlural = "briks"),
        ))
        override suspend fun getLocations() = Result.success(emptyList<ObjectsEntityGet200ResponseInner>())
        override suspend fun getProductGroups() = Result.success(listOf(
            ObjectsEntityGet200ResponseInner(id = 4, name = "Lácteos"),
        ))
        override suspend fun createProduct(body: ObjectsEntityGet200ResponseInner) = Result.success(1)
        override suspend fun getProducts() = Result.success(listOf(
            ObjectsEntityGet200ResponseInner(id = 1, name = "Leche entera", productGroupId = 4, quIdStock = 3),
            ObjectsEntityGet200ResponseInner(id = 2, name = "Pan de molde"),
        ))
        override suspend fun getShoppingLocations() = Result.success(emptyList<ObjectsEntityGet200ResponseInner>())
    }

    private class ShoppingListDataSourceStub(
        var lists: List<ObjectsEntityGet200ResponseInner> = listOf(ObjectsEntityGet200ResponseInner(id = 2)),
    ) : ShoppingListDataSource {
        var createdListName: String? = null
        var missingProductsListId: Int? = null
        val doneCalls = mutableListOf<Pair<Int, Boolean>>()
        val addedProducts = mutableListOf<Triple<Int, Int, Double>>()
        var itemFetches = 0
        var items = listOf(
            ObjectsEntityGet200ResponseInner(id = 10, productId = 1, amount = 5.0, quId = 3, shoppingListId = 2, done = 0),
            ObjectsEntityGet200ResponseInner(id = 11, productId = 2, amount = 1.0, shoppingListId = 2, done = 1),
            // Another list's row and a note-only row: both must be ignored.
            ObjectsEntityGet200ResponseInner(id = 12, productId = 1, amount = 2.0, shoppingListId = 9, done = 0),
            ObjectsEntityGet200ResponseInner(id = 13, note = "solo nota", shoppingListId = 2, done = 0),
        )

        override suspend fun getShoppingLists() = Result.success(lists)

        override suspend fun createShoppingList(name: String): Result<Int> {
            createdListName = name
            return Result.success(2)
        }

        override suspend fun getItems(): Result<List<ObjectsEntityGet200ResponseInner>> {
            itemFetches++
            return Result.success(items)
        }

        override suspend fun setDone(itemId: Int, done: Boolean): Result<Unit> {
            doneCalls += itemId to done
            return Result.success(Unit)
        }

        override suspend fun addMissingProducts(listId: Int): Result<Unit> {
            missingProductsListId = listId
            return Result.success(Unit)
        }

        override suspend fun addProduct(listId: Int, productId: Int, amount: Double): Result<Unit> {
            addedProducts += Triple(listId, productId, amount)
            return Result.success(Unit)
        }
    }

    @Test
    fun `first collection syncs missing products and joins master data`() = runBlocking {
        val stub = ShoppingListDataSourceStub()
        val repo = ShoppingListRepositoryImpl(stub, genericStub)

        val entries = repo.getShoppingList().first()

        assertEquals(2, stub.missingProductsListId)
        assertEquals(2, entries.size)

        val milk = entries.first { it.id == 10 }
        assertEquals("Leche entera", milk.productName)
        assertEquals("Lácteos", milk.groupName)
        assertEquals(5.0, milk.amount)
        assertEquals("brik", milk.unitName)
        assertEquals("briks", milk.unitNamePlural)
        assertFalse(milk.done)

        val bread = entries.first { it.id == 11 }
        assertEquals("Pan de molde", bread.productName)
        assertEquals(null, bread.groupName)
        // No qu_id on the row and no stock unit on the product: generic fallback names.
        assertEquals("ud", bread.unitName)
        assertTrue(bread.done)
    }

    @Test
    fun `creates a shopping list when the server has none`() = runBlocking {
        val stub = ShoppingListDataSourceStub(lists = emptyList())
        val repo = ShoppingListRepositoryImpl(stub, genericStub)

        repo.getShoppingList().first()

        assertEquals("Lista de la compra", stub.createdListName)
        assertEquals(2, stub.missingProductsListId)
    }

    @Test
    fun `setDone updates the row and patches the cache without refetching`() = runBlocking {
        val stub = ShoppingListDataSourceStub()
        val repo = ShoppingListRepositoryImpl(stub, genericStub)
        repo.getShoppingList().first()

        repo.setDone(entryId = 10, done = true)

        assertEquals(listOf(10 to true), stub.doneCalls)
        assertTrue(repo.getShoppingList().first().first { it.id == 10 }.done)
        assertEquals(1, stub.itemFetches)
    }

    @Test
    fun `addProduct forwards the request and refreshes the list`() = runBlocking {
        val stub = ShoppingListDataSourceStub()
        val repo = ShoppingListRepositoryImpl(stub, genericStub)
        repo.getShoppingList().first()

        repo.addProduct(productId = 2, amount = 3.0)

        assertEquals(listOf(Triple(2, 2, 3.0)), stub.addedProducts)
        assertEquals(2, stub.itemFetches)
    }
}
