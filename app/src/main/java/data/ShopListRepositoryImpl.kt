package data

import domain.ShopItem
import domain.ShopListRepository

class ShopListRepositoryImpl() : ShopListRepository {
    val shopList = mutableListOf<ShopItem>()

    private var autoIncrementId = 0

    override fun getAllItems(): List<ShopItem> {
        return shopList.toList()
    }

    override fun addShopItem(shopItem: ShopItem) {
        if (shopItem.id == ShopItem.UNDEFINED_ID) {
            shopItem.id == autoIncrementId++
        }
        shopList.add(shopItem)
    }

    override fun editShopItem(shopItem: ShopItem) {
        val oldElement = getShopItemById(shopItem.id)
        deleteShopItem(oldElement)
        addShopItem(shopItem)
    }

    override fun deleteShopItem(shopItem: ShopItem) {
        shopList.remove(shopItem)
    }

    override fun getShopItemById(shopItemId: Int): ShopItem {
        return shopList.find { it.id == shopItemId }
            ?: throw RuntimeException("Item with id $shopItemId not found")
    }
}