package eu.micer.translatetomore.feature.main.ui.adapter

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import eu.micer.translatetomore.R
import eu.micer.translatetomore.feature.main.model.LanguageItem
import eu.micer.translatetomore.feature.main.ui.MainActivity
import eu.micer.translatetomore.feature.main.vm.LanguageListViewModel
import eu.micer.translatetomore.util.afterTextChanged
import eu.micer.translatetomore.util.toEditable
import kotlinx.android.synthetic.main.item_language.view.*
import org.koin.android.architecture.ext.viewModel

class LanguageAdapter(private var items: ArrayList<LanguageItem>, private val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    private val languageListViewModel: LanguageListViewModel by (context as? AppCompatActivity)?.viewModel()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_language, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.etTextToTranslate.text = items[position].text.toEditable()

        if (position > 0) {
            holder.etTextToTranslate.isEnabled = false
            holder.ivEdit.visibility = View.VISIBLE

            holder.ivEdit.setOnClickListener {
                val selectedItem = items[position]
                (context as MainActivity).changeEditableLanguageItem(selectedItem.languageCode)
                notifyDataSetChanged()
            }
        }

        holder.tvLanguageCode.text = items[position].languageCode

        holder.etTextToTranslate.afterTextChanged {
            items[position].text = it
        }
    }

    fun getItemToTranslate(): LanguageItem {
        return items[0]
    }

    fun getLanguageCodesForTranslation(): Array<String> {
        return items.drop(1)
                .map { languageItem -> languageItem.languageCode }
                .toTypedArray()
    }

    fun updateItems(items: ArrayList<LanguageItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        (context as MainActivity).removeItem(items[position].languageCode)
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val etTextToTranslate: EditText = view.et_text_to_translate
    val tvLanguageCode: TextView = view.tv_language_code
    val ivEdit: ImageView = view.iv_edit
}