package domain

class GetShopItemUseCase(private val repository: ShopListRepository) {
    fun getShopItemById(shopItemId: Int): ShopItem {
        return repository.getShopItemById(shopItemId)
    }
}