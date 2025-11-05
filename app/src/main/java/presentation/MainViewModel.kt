package presentation

import androidx.lifecycle.ViewModel
import data.ShopListRepositoryImpl
import domain.DeleteShopItemUseCase
import domain.EditShopItemUseCase
import domain.GetShopListUseCase
import domain.ShopItem
import domain.ShopListRepository

class MainViewModel : ViewModel() {

    private val repository: ShopListRepository = ShopListRepositoryImpl()

    private val getShopListUseCase = GetShopListUseCase(repository)
    private val deleteShopItemUseCase = DeleteShopItemUseCase(repository)
    private val editShopItemUseCase = EditShopItemUseCase(repository)

    var shopList = getShopListUseCase.getShopList()

    fun deleteShopItem(item: ShopItem) {
        deleteShopItemUseCase.deleteShopItem(item)
    }

    fun changeEnableState(shopItem: ShopItem) {
        val newElement = shopItem.copy(isEnabled = !shopItem.isEnabled)
        editShopItemUseCase.editShopItem(newElement)
    }
}