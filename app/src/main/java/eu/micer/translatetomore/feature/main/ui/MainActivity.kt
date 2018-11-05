package eu.micer.translatetomore.feature.main.ui

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import eu.micer.translatetomore.R
import eu.micer.translatetomore.base.BaseActivity
import eu.micer.translatetomore.base.BaseViewModel
import eu.micer.translatetomore.feature.main.model.LanguageItem
import eu.micer.translatetomore.feature.main.ui.adapter.LanguageAdapter
import eu.micer.translatetomore.feature.main.vm.MainViewModel
import eu.micer.translatetomore.util.recyclerview.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.architecture.ext.viewModel


class MainActivity : BaseActivity() {
    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    private val mainViewModel: MainViewModel by viewModel()
    private lateinit var languageAdapter: LanguageAdapter

    override fun getViewModel(): BaseViewModel {
        return mainViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewModel.initLanguageItemList()

        setupViews()

        // load available languages codes for translation from the server
        mainViewModel.loadLanguages(getString(R.string.yandex_api_key))

        // LiveData observers
        mainViewModel.languageItemsMap.observe(this, Observer {
            languageAdapter.updateItems(mainViewModel.getLanguageItemList())
        })

        mainViewModel.languageList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                setupFabAndNewLanguageSpinner(it)
            }
        })
    }

    fun changeEditableLanguageItem(languageCode: String) {
        mainViewModel.changeEditableLanguageItem(languageCode)
    }

    fun removeItem(code: String) {
        mainViewModel.removeLanguage(code)
    }

    private fun setupViews() {
        rv_language_list.layoutManager = LinearLayoutManager(this)

        languageAdapter = LanguageAdapter(mainViewModel.getLanguageItemList(), this)
        rv_language_list.adapter = languageAdapter

        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                languageAdapter.removeAt(viewHolder.adapterPosition)
            }

        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(rv_language_list)

        btn_translate.setOnClickListener {
            val item = getItemToTranslate()
            val codes = languageAdapter.getLanguageCodesForTranslation()
            codes.forEach { code ->
                mainViewModel.clearText(code)
                mainViewModel.getTranslation(item, code, getString(R.string.yandex_api_key))
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tv_powered_by.text = Html.fromHtml(getString(R.string.powered_by_yandex_translate), Html.FROM_HTML_MODE_LEGACY)
        } else {
            tv_powered_by.text = Html.fromHtml(getString(R.string.powered_by_yandex_translate))
        }
        tv_powered_by.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setupFabAndNewLanguageSpinner(languageList: List<String>) {
        // FAB & add new language spinner
        val dataAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, languageList)

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner_add_language.adapter = dataAdapter

        spinner_add_language.setSelection(0, false)

        fab_add_language.setOnClickListener {
            spinner_add_language.performClick()
        }

        spinner_add_language.post {
            spinner_add_language.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    mainViewModel.addLanguage(parent?.getItemAtPosition(position).toString())
                }
            }
        }
    }

    private fun getItemToTranslate(): LanguageItem {
        return (rv_language_list.adapter as LanguageAdapter).getItemToTranslate()
    }
}
