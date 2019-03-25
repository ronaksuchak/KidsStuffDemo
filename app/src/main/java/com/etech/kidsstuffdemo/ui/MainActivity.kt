package com.etech.kidsstuffdemo.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.etech.kidsstuffdemo.R
import com.etech.kidsstuffdemo.helpers.ApiHelper
import com.etech.kidsstuffdemo.helpers.ApiHelper.Companion.BASE_URL
import com.etech.kidsstuffdemo.helpers.ApiHelper.Companion.LOGIN
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity@Kids Stuff"

    private lateinit var mQueue:RequestQueue
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mQueue = Volley.newRequestQueue(this)
        button_login_main.setOnClickListener {
            loadJson(editText_email_main.text.toString(),editText_password_main.text.toString())
             var progress = ProgressDialog.show(this@MainActivity,"Loading","Wait for It..")
            if(flage==1){
                Toast.makeText(this@MainActivity,"Done!!",Toast.LENGTH_LONG).show()
                progress.dismiss()
            }
        }

    }
    var flage = 0
    private fun loadJson(email:String,password:String):Int {

        var requestObject =JSONObject()
        requestObject.put("emailOrPhone",email)
        requestObject.put("password",password)
        Log.e(TAG,requestObject.toString())

        var userRequest = JsonObjectRequest(Request.Method.POST, BASE_URL+ LOGIN,requestObject, Response.Listener<JSONObject> {
            flage=it.getInt("flag")


        }, Response.ErrorListener {
            Log.e(TAG,it.message)
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

        })
        mQueue.add(userRequest)
        return flage
    }
}