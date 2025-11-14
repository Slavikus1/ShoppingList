package data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import domain.ShopItem
import domain.ShopListRepository
import kotlin.random.Random

class ShopListRepositoryImpl : ShopListRepository {
    private val _shopList = mutableListOf<ShopItem>()
    private val shopList = MutableLiveData<List<ShopItem>>()

    private var autoIncrementId = 0

    init {
        for (i in 1..1000) {
            val item = ShopItem("item $i", i, Random.nextBoolean())
            addShopItem(item)
        }
    }

    override fun getShopList(): LiveData<List<ShopItem>> {
        return shopList
    }

    override fun addShopItem(shopItem: ShopItem) {
        if (shopItem.id == ShopItem.UNDEFINED_ID) {
            shopItem.id = autoIncrementId++
        }
        _shopList.add(shopItem)
        updateShopList()
    }

    override fun editShopItem(shopItem: ShopItem) {
        val shopIndex = _shopList.indexOfFirst { it.id == shopItem.id }
        if (shopIndex == -1)
            throw RuntimeException("Item with id ${shopItem.id} not found")
        _shopList[shopIndex] = shopItem
        updateShopList()
    }

    override fun deleteShopItem(shopItem: ShopItem) {
        _shopList.remove(shopItem)
        updateShopList()
    }

    override fun getShopItemById(shopItemId: Int): ShopItem {
        return _shopList.find { it.id == shopItemId }
            ?: throw RuntimeException("Item with id $shopItemId not found")
    }

    private fun updateShopList() {
        shopList.value = _shopList.toList()
    }
}