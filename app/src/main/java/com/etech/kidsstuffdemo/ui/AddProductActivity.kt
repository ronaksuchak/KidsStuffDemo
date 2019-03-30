package com.etech.kidsstuffdemo.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.bikomobile.multipart.Multipart
import com.etech.kidsstuffdemo.brodcastRecivers.ConnectionBrodcastReciver
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
import java.nio.charset.Charset


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
        registorReciver()

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
                var addProductModel = getEditTextData()

                var newAddProductEntity = arrayListOf<AddProductEntity>()
                newAddProductEntity.add(
                    AddProductEntity(
                        System.currentTimeMillis().toInt(),
                        addProductModel.sellerId,
                        addProductModel.accessToken,
                        addProductModel.productName,
                        addProductModel.description,
                        addProductModel.categoryId,
                        addProductModel.price,
                        addProductModel.forGender,
                        addProductModel.ageGroupId,
                        bitmapToByteArray(addProductModel.file),
                        addProductModel.latitude,
                        addProductModel.longitude,
                        addProductModel.address,
                        addProductModel.city,
                        addProductModel.state,
                        addProductModel.country
                    )
                )
                addToDatabase(newAddProductEntity)
            }
        }


    }


    fun byteArrayToString(byteArray: ByteArray): String {
        return byteArray.toString(Charset.defaultCharset())
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

            Log.e(TAG, it.data.toString())


        }, {
            Log.e(TAG, it.toString())

        })


    }


    private fun addToDatabase(addProductEntityArray: List<AddProductEntity>) {


        Toast.makeText(this@AddProductActivity, "Product will added when you will back online!! ", Toast.LENGTH_SHORT)
            .show()

        registerReceiver(
            ConnectionBrodcastReciver(),
            IntentFilter("android.net.wifi.WIFI_STATE_CHANGED")
        )


        for (i in addProductEntityArray) {

            Observable.fromCallable {
                db = AppDatabase.getAppDataBase(context = this)

                addProductDao = db?.addProductDao()
                with(addProductDao) {
                    this?.insertGender(i)
                }
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
            Log.e(TAG, "added !! to DB")
        }
    }


    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {

        var stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        var byteArray = stream.toByteArray()
        return byteArray
    }

    private fun getEditTextData(): AddProductModel {
        val userId = SharedPrefHelper.getString(this@AddProductActivity, SharedPrefHelper.USER_ID_KEY, "")
        val accessToken = SharedPrefHelper.getString(this@AddProductActivity, SharedPrefHelper.AUTH_TOKEN_KEY, "")
        val productName = editText_product_name.text.toString()
        val description = editText_description.text.toString()
        val category = ApiHelper.CATOGURY_ID
        val price = editText_price.text.toString()
        val forGender = spinner_for_gender.selectedItem.toString()
        val ageGroupId = ApiHelper.AGE_GROUP_ID
        val bitmapLocal = bitmap
        val lati = lat
        val longi = long
        val address = editText_address.text.toString()
        val city = editText_city.text.toString()
        val state = editText_state.text.toString()
        val country = editText_country.text.toString()



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

    fun registorReciver() {
        var intentFilter = IntentFilter()
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED")
        var reviver = ConnectionBrodcastReciver()
        registerReceiver(reviver, intentFilter)
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

