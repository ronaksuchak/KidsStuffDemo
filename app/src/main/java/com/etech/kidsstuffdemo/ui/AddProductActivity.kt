package com.etech.kidsstuffdemo.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.error.VolleyError
import com.android.volley.request.SimpleMultiPartRequest
import com.etech.kidsstuffdemo.R
import com.etech.kidsstuffdemo.databaseHelper.AddProductDao
import com.etech.kidsstuffdemo.databaseHelper.AddProductEntity
import com.etech.kidsstuffdemo.databaseHelper.AppDatabase
import com.etech.kidsstuffdemo.helpers.ApiHelper.Companion.ADD_PRODUCT
import com.etech.kidsstuffdemo.helpers.ApiHelper.Companion.BASE_URL
import com.example.easywaylocation.EasyWayLocation
import com.example.easywaylocation.Listener
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_product.*


class AddProductActivity : AppCompatActivity() {
    private lateinit var easyWayLocation: EasyWayLocation
    private val TAG = "ADD_PRODUCT"
    var lat: Double = 0.0
    var long: Double = 0.0
    private val RESULT_LOAD_IMAGE = 5
    private var db: AppDatabase? = null
    private var addProductDao: AddProductDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        easyWayLocation = EasyWayLocation(this@AddProductActivity)
        getLocation()
        setSpinner()
        imageView3.setOnClickListener {
            getImageFromGallery()
        }

        button_add.setOnClickListener {
            if (isNetworkAvailable()) {
                uploadToServer()
            } else {
                addToDatabase(
                    AddProductEntity(
                        1,
                        "id1",
                        "abc2",
                        "beg",
                        "sjalhksje",
                        "3",
                        "66",
                        "him",
                        "sdfg",
                        "asdf",
                        "sdf",
                        "asdf",
                        "dsg",
                        "sdf",
                        "sd"
                    )
                )
            }
        }
    }

    private fun getImageFromGallery() {
        val i = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, RESULT_LOAD_IMAGE)
    }

    private fun uploadToServer() {
        var smr =
            SimpleMultiPartRequest(Request.Method.POST, BASE_URL + ADD_PRODUCT, object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    Log.e(TAG, response)
                }

            }, Response.ErrorListener { error -> Log.e(TAG, error?.message) })
        smr.addStringParam("", "")
        smr.addMultipartParam("", "", "")
    }


    private fun addToDatabase(addProductEntity: AddProductEntity) {

        Observable.fromCallable {
            db = AppDatabase.getAppDataBase(context = this)
            // genderDao = db?.genderDao()

            //            var gender1 = Gender(name = "Male")
            //            var gender2 = Gender(name = "Female")
            //
            //            with(genderDao){
            //                this?.insertGender(gender1)
            //                this?.insertGender(gender2)
            //            }
            //            db?.genderDao()?.getGenders()
            //        }).doOnNext({ list ->
            //            var finalString = ""
            //            list?.map { finalString+= it.name+" - " }
            //            tv_message.text = finalString
            addProductDao = db?.AddProductDao()
            with(addProductDao) {
                this?.insertGender(addProductEntity)
            }

        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()


    }

    private fun setSpinner() {
        ArrayAdapter.createFromResource(
            this,
            com.etech.kidsstuffdemo.R.array.for_gender,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner_for_gender.adapter = adapter
        }
    }


    private fun getLocation() {
        easyWayLocation.setListener(object : Listener {
            override fun locationCancelled() {

            }

            override fun locationOn() {
                Log.e(TAG, "location on..")
                easyWayLocation.beginUpdates()
                lat = easyWayLocation.latitude
                long = easyWayLocation.longitude
                Log.e(TAG, "lat = $lat long= $long ")
                Toast.makeText(this@AddProductActivity, "lat = $lat long= $long ", Toast.LENGTH_SHORT).show()
                textView_latLong.text = "lat = $lat long= $long"

            }

            override fun onPositionChanged() {
                lat = easyWayLocation.latitude
                long = easyWayLocation.longitude
                Log.e(TAG, "lat = $lat long= $long ")
                Toast.makeText(this@AddProductActivity, "lat = $lat long= $long ", Toast.LENGTH_SHORT).show()
                textView_latLong.text = "lat = $lat long= $long"
            }

        })
    }

    private fun isNetworkAvailable(): Boolean {
        val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo
        var isAvailable = false
        if (networkInfo != null && networkInfo.isConnected) {
            // Network is present and connected
            isAvailable = true
        }
        return isAvailable
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            val selectedImage = data.data

            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(selectedImage!!, filePathColumn, null, null, null)
            cursor!!.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            cursor.close()
            var bitmap = BitmapFactory.decodeFile(picturePath)
            imageView3.setImageBitmap(bitmap)
        }
    }
}
