package domain

class DeleteShopItemUseCase(private val repository: ShopListRepository) {
    fun deleteShopItem(shopItem: ShopItem) {
        repository.deleteShopItem(shopItem)
    }
}