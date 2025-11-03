package domain

class GetAllItemsUseCase(private val repository: ShopListRepository) {
    fun getAllItems(): List<ShopItem> {
        return repository.getAllItems()
    }
}