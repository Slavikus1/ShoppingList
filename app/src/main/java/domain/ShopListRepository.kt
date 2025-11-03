package domain

interface ShopListRepository {

    fun getShopList(): List<ShopItem>

    fun addShopItem(shopItem: ShopItem)

    fun editShopItem(shopItem: ShopItem)

    fun deleteShopItem(shopItem: ShopItem)

    fun getShopItemById(shopItemId: Int): ShopItem
}