package eu.micer.translatetomore.base

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

abstract class BaseActivity : AppCompatActivity() {

    abstract fun getViewModel() : BaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getViewModel().showError.observe(this, Observer {
            it?.getContentIfNotHandled()?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })
    }
}