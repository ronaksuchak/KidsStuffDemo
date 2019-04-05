package com.etech.kidsstuffdemo.brodcastRecivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bikomobile.multipart.Multipart
import com.etech.kidsstuffdemo.databaseHelper.*
import com.etech.kidsstuffdemo.helpers.ApiHelper
import com.etech.kidsstuffdemo.helpers.SharedPrefHelper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.nio.charset.Charset

class ConnectionBrodcastReciver : BroadcastReceiver() {
    private var db: AppDatabase? = null
    private var addProductDao: AddProductDao? = null
    private var deleteProductDao: DeleteProductDao? = null

    override fun onReceive(context: Context, intent: Intent) {

        Handler().postDelayed({
            Log.e(TAG, "Waitting....")
            if (isNetworkAvailable(context)) {
                Log.e(TAG, "onReceive....")
//            UploadTask(context, getAllPendingUpload(context)).execute()
                Log.e(TAG, "Network is back On line ")

                getAllPendingUpload(context)
                getAllPendingDels(context)
                //Log.e(TAG,"size of list to upload ${list.size}")


            } else {
                Log.e(TAG, "Network is offline line ")
                Toast.makeText(context, "network is not avalable ..", Toast.LENGTH_SHORT).show()
            }
        }, 5000)


    }

    private fun getAllPendingDels(context: Context) {
        var listOfAllPendingDelets = listOf<DeleteProductEntity>()
        Observable.fromCallable {
            db = AppDatabase.getAppDataBase(context = context)

            deleteProductDao = db?.deleteProductDao()
            with(deleteProductDao) {
                listOfAllPendingDelets = this?.getAll()!!
                Log.e(TAG, " size of list from db ${listOfAllPendingDelets.size}")
                for (i in listOfAllPendingDelets) {
                    deleteProduct(context, i.id)
                    Log.e(TAG," id is ${i.id}  _id is${i._id} ")

                }
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

    }

    private fun deleteProduct(context: Context, productId: String) {
        var mQueue = Volley.newRequestQueue(context)
        var userId = SharedPrefHelper.getString(context, SharedPrefHelper.USER_ID_KEY, "")
        var authToken = SharedPrefHelper.getString(context, SharedPrefHelper.AUTH_TOKEN_KEY, "")
        mQueue = Volley.newRequestQueue(context)
        var requestParam: JSONObject = JSONObject()
        requestParam.put("userId", userId)
        requestParam.put("accessToken", authToken)
        requestParam.put("productId", productId)


        var request =
            JsonObjectRequest(
                Request.Method.POST,
                ApiHelper.BASE_URL + ApiHelper.DELETE_PRODUCTS,
                requestParam,
                Response.Listener {
                    Toast.makeText(context, it.getString("message"), Toast.LENGTH_SHORT).show()
                    Log.e(TAG,"${it.getString("message")}  $requestParam ")

                },
                Response.ErrorListener {

                })
        mQueue.add(request)
        deleteAllDelFromDb(context)

    }


//

    private fun isNetworkAvailable(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo
        var isAvailable = false
        if (networkInfo != null && networkInfo.isConnected) {
            // Network is present and connected
            isAvailable = true
        }
        return isAvailable
    }

    private fun getAllPendingUpload(context: Context) {
        var listOfAllPendingUploads = listOf<AddProductEntity>()

        Observable.fromCallable {
            db = AppDatabase.getAppDataBase(context = context)

            addProductDao = db?.addProductDao()
            with(addProductDao) {
                listOfAllPendingUploads = this?.getAllPendingUploads()!!
                Log.e(TAG, " size of list from db ${listOfAllPendingUploads.size.toString()}")
                for (i in listOfAllPendingUploads) {
                    uploadToServer(context, i)
                    Log.e(TAG, "${i.productName} will be added to server .. ")
                }
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
        deleteAllFromDb(context)


    }


    private fun deleteAllFromDb(context: Context) {
        Observable.fromCallable {
            db = AppDatabase.getAppDataBase(context = context)

            addProductDao = db?.addProductDao()
            with(addProductDao) {
                this?.deleteAll()
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    private fun deleteAllDelFromDb(context: Context) {
        Observable.fromCallable {
            db = AppDatabase.getAppDataBase(context = context)

            deleteProductDao = db?.deleteProductDao()
            with(deleteProductDao) {
                this?.deleteAll()
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }


    /*
    *
*/


    private fun uploadToServer(context: Context, addProductModel: AddProductEntity) {
        Log.e(TAG, "in Upload to Server")
        var multipart = Multipart(context)


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

        multipart.addFile("image/jpeg", "file", imageName, addProductModel.bitmap)
        Log.e(TAG, "bitmap is ${addProductModel.bitmap}")
        Log.e(TAG, "bitmap is byte array is  ${addProductModel.bitmap}")
        multipart.addParam("latitude", addProductModel.latitude)
        multipart.addParam("longitude", addProductModel.longitude)

        multipart.addParam("address", addProductModel.address)
        multipart.addParam("city", addProductModel.city)
        multipart.addParam("state", addProductModel.state)
        multipart.addParam("country", addProductModel.country)


        multipart.launchRequest(ApiHelper.BASE_URL + ApiHelper.ADD_PRODUCT, {
            result = it.data.toString()
            Log.e(TAG, "in launch request success")
            Toast.makeText(context, "Uploades Sucess!! ", Toast.LENGTH_SHORT).show()
            Log.e(TAG, it.data.toString())


        }, {

            Log.e(TAG, "in launch request failer ")
            Log.e(TAG, it.toString())

        })


    }


    private fun stringToByteArray(string: String): ByteArray {
        return string.toByteArray(Charset.defaultCharset())
    }

    companion object {
        val TAG = "kids stuff Reciver"
        var result = ""
    }

}