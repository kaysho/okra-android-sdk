package com.okra.widget.activities

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.okra.android.activities.OkraMainActivity
import com.okra.widget.R

class MainActivity : AppCompatActivity() {

    val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback {
        if (it.resultCode == RESULT_OK && it.data != null){
            val okraResult  = it.data!!.getStringExtra(OkraMainActivity.OKRA_RESULT)
            Toast.makeText(this, okraResult, Toast.LENGTH_SHORT).show()
        }
        else{
            val okraResult  = it.data!!.getStringExtra(OkraMainActivity.OKRA_RESULT)
            Toast.makeText(this, okraResult, Toast.LENGTH_SHORT).show()
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val optionBtn = findViewById<Button>(R.id.btn)
        optionBtn.setOnClickListener {
            //With option build
            val dummyOkraObject = DummyOkraObject("key","token", listOf("auth","balance","identity","income", "transactions"),"dev","Kaysho","android" )
            val intent = OkraMainActivity.newIntent(this, dummyOkraObject)
            activityResultLauncher.launch(intent)
        }

        val shortBtn = findViewById<Button>(R.id.btn)
        shortBtn.setOnClickListener {
            //With short-url
            val shortUrlObject = DummyShortUrlObject("uOxqP-u9n","android")
            val intent = OkraMainActivity.newIntent(this, shortUrlObject)
            activityResultLauncher.launch(intent)
        }
    }
}