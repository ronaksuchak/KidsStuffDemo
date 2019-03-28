package com.etech.kidsstuffdemo.ui


//import HttpMultipartMode.SimpleMultipartEntity
//import android.util.Log

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.NetworkResponse
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.bikomobile.multipart.Multipart
import com.etech.kidsstuffdemo.R
import com.etech.kidsstuffdemo.databaseHelper.AddProductDao
import com.etech.kidsstuffdemo.databaseHelper.AddProductEntity
import com.etech.kidsstuffdemo.databaseHelper.AppDatabase
import com.etech.kidsstuffdemo.helpers.ApiHelper
import com.etech.kidsstuffdemo.helpers.SharedPrefHelper
import com.etech.kidsstuffdemo.models.AddProductModel
import com.example.easywaylocation.EasyWayLocation
import com.example.easywaylocation.Listener
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_product.*
import java.io.ByteArrayOutputStream


class AddProductActivity : AppCompatActivity() {
    private lateinit var easyWayLocation: EasyWayLocation
    private val TAG = "ADD_PRODUCT"
    var lat: Double = 0.0
    var long: Double = 0.0
    private val RESULT_LOAD_IMAGE = 5
    private var db: AppDatabase? = null
    private var addProductDao: AddProductDao? = null
    lateinit var mQurue: RequestQueue
    var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)
        mQurue = Volley.newRequestQueue(this@AddProductActivity)
        easyWayLocation = EasyWayLocation(this@AddProductActivity)
        getLocation()
        setSpinner()

        imageView3.setOnClickListener {
            getImageFromGallery()
        }

        button_add.setOnClickListener {


            if (isNetworkAvailable()) {
                uploadToServer(getEditTextData())
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

    private fun getEditTextData(): AddProductModel {
        var userId = SharedPrefHelper.getString(this@AddProductActivity, SharedPrefHelper.USER_ID_KEY, "")
        var accessToken = SharedPrefHelper.getString(this@AddProductActivity, SharedPrefHelper.AUTH_TOKEN_KEY, "")
        var productName = editText_product_name.text.toString()
        var description = editText_description.text.toString()
        var category = ApiHelper.CATOGURY_ID
        var price = editText_price.text.toString()
        var forGender = spinner_for_gender.selectedItem.toString()
        var ageGroupId = ApiHelper.AGE_GROUP_ID
        var bitmapLocal = bitmap
        var lati = lat
        var longi = long
        var address = editText_address.text.toString()
        var city = editText_city.text.toString()
        var state = editText_state.text.toString()
        var country = editText_country.text.toString()



        return AddProductModel(
            userId,
            accessToken,
            productName,
            description,
            category,
            price,
            forGender,
            ageGroupId,
            bitmapLocal!!,
            lati.toString(),
            longi.toString(),
            address,
            city,
            state,
            country
        )
    }

    private fun getImageFromGallery() {
        val i = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, RESULT_LOAD_IMAGE)
    }

    private fun uploadToServer(addProductModel: AddProductModel) {

        var multipart = Multipart(this@AddProductActivity)


        var imageName = "IMG${System.currentTimeMillis()}.jpg"
        Log.e(TAG, imageName)


        multipart.addParam("sellerId", addProductModel.sellerId)
        multipart.addParam("accessToken", addProductModel.accessToken)
        multipart.addParam("productName", addProductModel.productName)
        multipart.addParam("description", addProductModel.description)
        multipart.addParam("categoryId", addProductModel.categoryId)
        multipart.addParam("price", addProductModel.price)

        multipart.addParam("forGender", addProductModel.forGender)
        multipart.addParam("ageGroupId", addProductModel.ageGroupId)
        //multipart.addParam("file",addProductModel.productName)

        multipart.addFile("image/jpeg", "file", imageName, bitmapToByteArray(bitmap!!))
        multipart.addParam("latitude", addProductModel.latitude)
        multipart.addParam("longitude", addProductModel.longitude)

        multipart.addParam("address", addProductModel.address)
        multipart.addParam("city", addProductModel.city)
        multipart.addParam("state", addProductModel.state)
        multipart.addParam("country", addProductModel.country)


        multipart.launchRequest(ApiHelper.BASE_URL + ApiHelper.ADD_PRODUCT, {

        Log.e(TAG,it.data.toString())


        }, {
            Log.e(TAG,it.toString())

        })



    }

    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {

        var stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        var byteArray = stream.toByteArray()
        return byteArray
    }


    private fun addToDatabase(addProductEntity: AddProductEntity) {

        Observable.fromCallable {
            db = AppDatabase.getAppDataBase(context = this)

            addProductDao = db?.AddProductDao()
            with(addProductDao) {
                this?.insertGender(addProductEntity)
            }

        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
        Log.e(TAG, "added !! to DB")


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
            bitmap = BitmapFactory.decodeFile(picturePath)
            imageView3.setImageBitmap(bitmap)
        }
    }
}


