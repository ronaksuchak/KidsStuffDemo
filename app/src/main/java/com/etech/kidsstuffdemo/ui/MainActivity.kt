package com.etech.kidsstuffdemo.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.etech.kidsstuffdemo.R
import com.etech.kidsstuffdemo.databinding.ActivityMainBinding
import com.etech.kidsstuffdemo.helpers.ApiHelper.Companion.BASE_URL
import com.etech.kidsstuffdemo.helpers.ApiHelper.Companion.LOGIN
import com.etech.kidsstuffdemo.helpers.SharedPrefHelper
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private val TAG = "Kids Stuff"
    private lateinit var mQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mQueue = Volley.newRequestQueue(this)
        button_login_main.setOnClickListener {
            loadJson(editText_email_main.text.toString(), editText_password_main.text.toString())

            var progressDiloge = ProgressDialog(this@MainActivity)
            progressDiloge.setTitle("Wait...")
            progressDiloge.show()



            Handler().postDelayed({
                var flage = SharedPrefHelper.getInt(this@MainActivity, SharedPrefHelper.LOGIN_PREF_KEY, 0)
                if (flage == 1) {
                    startActivity(Intent(this@MainActivity, DashbordActivity::class.java))
                    Toast.makeText(this@MainActivity, "success!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@MainActivity, "Failed!", Toast.LENGTH_LONG).show()
                }
                progressDiloge.dismiss()
            }, 2000)

        }

    }

    private fun loadJson(email: String, password: String) {

        var requestObject = JSONObject()
        requestObject.put("emailOrPhone", email)
        requestObject.put("password", password)
        Log.e(TAG, requestObject.toString())

        var userRequest =
            JsonObjectRequest(Request.Method.POST, BASE_URL + LOGIN, requestObject, Response.Listener<JSONObject> {

                Log.e(TAG, it.toString())
                var data = it.getJSONObject("data")
                var userDetail = data.getJSONObject("userDetail")

                SharedPrefHelper.add(this@MainActivity, SharedPrefHelper.LOGIN_PREF_KEY, it.getInt("flag"))
                SharedPrefHelper.add(this@MainActivity, SharedPrefHelper.USER_ID_KEY, userDetail.getString("_id"))
                SharedPrefHelper.add(
                    this@MainActivity,
                    SharedPrefHelper.AUTH_TOKEN_KEY,
                    userDetail.getString("accessToken")
                )


                SharedPrefHelper.add(this@MainActivity, SharedPrefHelper.EMAIL_KEY, userDetail.getString("email"))
                SharedPrefHelper.add(this@MainActivity, SharedPrefHelper.USER_NAME_KEY, userDetail.getString("name"))

            }, Response.ErrorListener {
                Log.e(TAG, it.message)

            })
        mQueue.add(userRequest)
//        return flage
    }
}