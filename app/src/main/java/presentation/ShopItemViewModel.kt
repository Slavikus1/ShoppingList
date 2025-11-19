package presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import data.ShopListRepositoryImpl
import domain.AddShopItemUseCase
import domain.EditShopItemUseCase
import domain.GetShopItemUseCase
import domain.ShopItem

class ShopItemViewModel : ViewModel() {

    private val repository = ShopListRepositoryImpl()
    private val getShopItemUseCase = GetShopItemUseCase(repository)
    private val editShopItemUseCase = EditShopItemUseCase(repository)
    private val addShopItemUseCase = AddShopItemUseCase(repository)

    private val _errorInputName: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _errorInputCount: MutableLiveData<Boolean> = MutableLiveData(false)

    private val _shopItem = MutableLiveData<ShopItem>()

    private val _shouldCloseScreen = MutableLiveData(false)

    val shouldCloseScreen: LiveData<Boolean>
        get() = _shouldCloseScreen

    val shopItem: LiveData<ShopItem>
        get() = _shopItem

    fun setShopItem(item: ShopItem) {
        _shopItem.value = item
    }

    val errorInputName: LiveData<Boolean>
        get() = _errorInputName

    val errorInputCount: LiveData<Boolean>
        get() = _errorInputCount

    fun editShopItem(newName: String?, newCount: String?) {
        val name = parseName(newName)
        val count = parseCount(newCount)
        val fieldsValid = validateInput(name, count)
        if (fieldsValid) {
            _shopItem.value?.let {
                val item = it.copy(name = name, count = count)
                editShopItemUseCase.editShopItem(item)
                finishWork()
            }
        }

    }

    fun getShopItem(itemId: Int) {
        android.util.Log.d("ShopItemViewModel", "getShopItem called with id: $itemId")
        try {
            val shopItem = getShopItemUseCase.getShopItemById(itemId)
            android.util.Log.d("ShopItemViewModel", "ShopItem found: ${shopItem.name}")
            _shopItem.value = shopItem
        } catch (e: Exception) {
            android.util.Log.e("ShopItemViewModel", "Error getting shop item: ${e.message}", e)
            // Не выбрасываем исключение, чтобы Activity не крашилась
            // Вместо этого можно показать ошибку пользователю или использовать дефолтные значения
        }
    }

    fun addShopItem(inputName: String?, inputCount: String?) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val fieldsValid = validateInput(name, count)

        if (fieldsValid) {
            addShopItemUseCase.addShopItem(ShopItem(name, count, true))
            finishWork()
        }
    }

    private fun validateInput(name: String, count: Int): Boolean {
        var result = true
        if (name.isBlank()) {
            result = false
            _errorInputName.value = true
        } else if (count <= 0) {
            result = false
            _errorInputCount.value = true
        }
        return result
    }

    private fun parseName(name: String?): String {
        return name?.trim() ?: ""
    }

    private fun parseCount(count: String?): Int {
        return count?.toIntOrNull() ?: 0
    }

    private fun finishWork() {
        _shouldCloseScreen.value = true
    }

    fun resetErrorInputName() {
        _errorInputName.value = false
    }

    fun resetErrorInputCount() {
        _errorInputName.value = false
    }
}