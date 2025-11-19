package presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.shoppinglist.R
import com.google.android.material.textfield.TextInputLayout
import domain.ShopItem

class ShopItemActivity : AppCompatActivity() {
    private lateinit var tilName: TextInputLayout
    private lateinit var tilCount: TextInputLayout
    private lateinit var etName: EditText
    private lateinit var etCount: EditText

    private lateinit var buttonAdd: Button
    private lateinit var viewModel: ShopItemViewModel

    private var screenMode: String? = UNKNOWN_EXTRA_MODE
    private var shopItemId = ShopItem.UNDEFINED_ID
    private var shopItem: ShopItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        android.util.Log.d("ShopItemActivity", "onCreate started")
        try {
            parseIntent()
            android.util.Log.d("ShopItemActivity", "Intent parsed successfully, mode: $screenMode")
            setContentView(R.layout.activity_shop_item)
            android.util.Log.d("ShopItemActivity", "Content view set")
            viewModel = ViewModelProvider(this)[ShopItemViewModel::class.java]
            android.util.Log.d("ShopItemActivity", "ViewModel created")
            initViews()
            android.util.Log.d("ShopItemActivity", "Views initialized")
            setUpOnTextChangeListeners()
            launchRightMode()
            observeViewModel()
            android.util.Log.d("ShopItemActivity", "onCreate completed")
        } catch (e: Exception) {
            android.util.Log.e("ShopItemActivity", "Error in onCreate", e)
            throw e
        }
    }

    override fun onStart() {
        super.onStart()
        android.util.Log.d("ShopItemActivity", "onStart called")
    }

    override fun onResume() {
        super.onResume()
        android.util.Log.d("ShopItemActivity", "onResume called")
    }

    override fun onPause() {
        super.onPause()
        android.util.Log.d("ShopItemActivity", "onPause called")
    }

    override fun onStop() {
        super.onStop()
        android.util.Log.d("ShopItemActivity", "onStop called")
    }

    override fun onDestroy() {
        super.onDestroy()
        android.util.Log.d("ShopItemActivity", "onDestroy called")
    }

    private fun observeViewModel() {
        viewModel.errorInputCount.observe(this) {
            val message = if (it) {
                getString(R.string.invalid_count_error)
            } else {
                null
            }
            tilCount.error = message
        }
        viewModel.errorInputName.observe(this) {
            val message = if (it) {
                getString(R.string.invalid_name_error)
            } else {
                null
            }
            tilName.error = message
        }
        viewModel.shouldCloseScreen.observe(this) {
            android.util.Log.d("ShopItemActivity", "shouldCloseScreen observed: $it")
            if (it) {
                android.util.Log.d("ShopItemActivity", "Finishing activity")
                finish()
            }
        }
    }

    private fun launchRightMode() {
        android.util.Log.d("ShopItemActivity", "launchRightMode called, screenMode: $screenMode")
        when (screenMode) {
            MODE_EDIT_ITEM -> {
                android.util.Log.d("ShopItemActivity", "Launching edit screen")
                launchEditScreen()
            }
            MODE_ADD_ITEM -> {
                android.util.Log.d("ShopItemActivity", "Launching add screen")
                launchAddScreen()
            }
            else -> {
                android.util.Log.e("ShopItemActivity", "Unknown screen mode: $screenMode")
            }
        }
    }

    private fun setUpOnTextChangeListeners() {
        etName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                viewModel.resetErrorInputName()
            }

        })
        etCount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                viewModel.resetErrorInputCount()
            }

        })
    }

    private fun launchAddScreen() {
        buttonAdd.setOnClickListener {
            viewModel.addShopItem(etName.text.toString(), etCount.text.toString())
        }
    }

    private fun launchEditScreen() {
        android.util.Log.d("ShopItemActivity", "launchEditScreen started, shopItemId: $shopItemId")
        try {
            // Если ShopItem уже получен из Intent, используем его
            if (shopItem != null) {
                android.util.Log.d("ShopItemActivity", "Using shopItem from intent: ${shopItem!!.name}")
                etName.setText(shopItem!!.name)
                etCount.setText(shopItem!!.count.toString())
                // Устанавливаем ShopItem в ViewModel для дальнейшего редактирования
                viewModel.setShopItem(shopItem!!)
            } else {
                // Иначе пытаемся получить из репозитория
                viewModel.getShopItem(shopItemId)
                android.util.Log.d("ShopItemActivity", "getShopItem called")
                viewModel.shopItem.observe(this) {
                    android.util.Log.d("ShopItemActivity", "ShopItem received: ${it.name}, count: ${it.count}")
                    etName.setText(it.name)
                    etCount.setText(it.count.toString())
                }
            }
            buttonAdd.setOnClickListener {
                viewModel.editShopItem(etName.text.toString(), etCount.text.toString())
            }
            android.util.Log.d("ShopItemActivity", "launchEditScreen completed")
        } catch (e: Exception) {
            android.util.Log.e("ShopItemActivity", "Error in launchEditScreen", e)
            e.printStackTrace()
        }
    }

    private fun parseIntent() {
        android.util.Log.d("ShopItemActivity", "parseIntent started")
        android.util.Log.d("ShopItemActivity", "Intent extras: ${intent.extras?.keySet()}")
        if (!intent.hasExtra(EXTRA_MODE)) {
            android.util.Log.e("ShopItemActivity", "Extra mode is absent")
            throw RuntimeException("Extra mode is absent")
        }
        val mode = intent.getStringExtra(EXTRA_MODE)
        android.util.Log.d("ShopItemActivity", "Mode from intent: $mode")
        if (mode != MODE_EDIT_ITEM && mode != MODE_ADD_ITEM) {
            android.util.Log.e("ShopItemActivity", "Invalid extra mode: $mode")
            throw RuntimeException("Invalid extra mode $mode")
        }
        screenMode = mode
        if (mode == MODE_EDIT_ITEM) {
            // Пытаемся получить ShopItem напрямую из Intent
            shopItem = intent.getParcelableExtra(EXTRA_SHOP_ITEM)
            if (shopItem == null) {
                // Fallback на старый способ через ID (для обратной совместимости)
                if (intent.hasExtra(EXTRA_SHOP_ITEM_ID)) {
                    shopItemId = intent.getIntExtra(EXTRA_SHOP_ITEM_ID, ShopItem.UNDEFINED_ID)
                    android.util.Log.d("ShopItemActivity", "ShopItem ID from intent: $shopItemId")
                } else {
                    android.util.Log.e("ShopItemActivity", "Neither shopItem nor shopItemId found in intent")
                    throw RuntimeException("Invalid shopItem data")
                }
            } else {
                shopItemId = shopItem!!.id
                android.util.Log.d("ShopItemActivity", "ShopItem from intent: ${shopItem!!.name}, id: ${shopItem!!.id}")
            }
        }
        android.util.Log.d("ShopItemActivity", "parseIntent completed successfully")
    }

    private fun initViews() {
        buttonAdd = findViewById<Button>(R.id.save_button)
        tilName = findViewById<TextInputLayout>(R.id.til_name)
        tilCount = findViewById<TextInputLayout>(R.id.til_count)
        etName = findViewById<EditText>(R.id.et_name)
        etCount = findViewById<EditText>(R.id.et_count)
    }

    companion object {
        private const val UNKNOWN_EXTRA_MODE = ""
        private const val EXTRA_MODE = "extra_mode"
        private const val MODE_ADD_ITEM = "mode_add"
        private const val MODE_EDIT_ITEM = "mode_edit"
        private const val EXTRA_SHOP_ITEM_ID = "extra_shop_item_id"
        private const val EXTRA_SHOP_ITEM = "extra_shop_item"

        fun newIntentAddItem(context: Context): Intent {
            val intent = Intent(context, ShopItemActivity::class.java)
            intent.putExtra(EXTRA_MODE, MODE_ADD_ITEM)
            android.util.Log.d("ShopItemActivity", "Creating add intent with mode: $MODE_ADD_ITEM")
            return intent
        }

        fun newIntentEditItem(context: Context, shopItem: ShopItem): Intent {
            val intent = Intent(context, ShopItemActivity::class.java)
            intent.putExtra(EXTRA_MODE, MODE_EDIT_ITEM)
            intent.putExtra(EXTRA_SHOP_ITEM, shopItem)
            android.util.Log.d("ShopItemActivity", "Creating edit intent with mode: $MODE_EDIT_ITEM, item: ${shopItem.name}")
            return intent
        }
    }
}