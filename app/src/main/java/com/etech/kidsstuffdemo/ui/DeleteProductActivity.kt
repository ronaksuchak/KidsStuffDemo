package com.etech.kidsstuffdemo.ui

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.etech.kidsstuffdemo.R
import com.etech.kidsstuffdemo.adaptors.DeleteProductListAdaptor
import com.etech.kidsstuffdemo.databaseHelper.AppDatabase
import com.etech.kidsstuffdemo.databaseHelper.ProductDao
import com.etech.kidsstuffdemo.databaseHelper.ProductEntity
import com.etech.kidsstuffdemo.helpers.ApiHelper
import com.etech.kidsstuffdemo.helpers.InfiniteScroll
import com.etech.kidsstuffdemo.helpers.SharedPrefHelper
import com.etech.kidsstuffdemo.models.ProductListModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_delete_product.*
import org.json.JSONObject

class DeleteProductActivity : AppCompatActivity() {


    private lateinit var mQueue: RequestQueue
    private val TAG = "Kids Stuff delete "
    var authToken = ""
    var userId = ""
    var productList = mutableListOf<ProductListModel>()
    var layoutManager = LinearLayoutManager(this)
    var adaptor = DeleteProductListAdaptor(this, productList)

    private var db: AppDatabase? = null
    private var productDao: ProductDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mQueue = Volley.newRequestQueue(this)
        setContentView(R.layout.activity_delete_product)
        if (swipe_n_refresh_delete.isRefreshing) {
            swipe_n_refresh_delete.isRefreshing = false
        }
        if (!isNetworkAvailable(this@DeleteProductActivity)) {

            loadFromDb()
        }



        getAccessToken()
        loadJson(1)
        Log.e(TAG, "load json from on create called..")

        recyclerView_delete.layoutManager = layoutManager

        recyclerView_delete.adapter = adaptor
        recyclerView_delete.addOnScrollListener(InfiniteScroll(layoutManager) {
            if (isNetworkAvailable(this)) {
                //println("load page $it")
                Toast.makeText(this, "current page is ${it - 1}", Toast.LENGTH_SHORT).show()
                loadJson(it - 1)
                Log.e(TAG, "load json from RV On scroll called..")
                adaptor.notifyDataSetChanged()
            }

        })


        swipe_n_refresh_delete.setOnRefreshListener {
            if (isNetworkAvailable(this)) {
                productList.clear()
                loadJson(1)
                Log.e(TAG, "load json from SWIP in refresh listener called..")
                adaptor.notifyDataSetChanged()

//

                adaptor.notifyDataSetChanged()
            }
        }

    }

    private fun loadFromDb() {
        var listOfProduct = listOf<ProductEntity>()
        Observable.fromCallable {
            db = AppDatabase.getAppDataBase(context = this@DeleteProductActivity)

            productDao = db?.productDao()
            with(productDao) {
                //        listOfAllPendingUploads = this?.getAllPendingUploads()!!
//                android.util.Log.e(com.etech.kidsstuffdemo.brodcastRecivers.ConnectionBrodcastReciver.Companion.TAG, " size of list from db ${listOfAllPendingUploads.size.toString()}")
//                for (i in listOfAllPendingUploads) {
//                    uploadToServer(context, i)
//                    android.util.Log.e(com.etech.kidsstuffdemo.brodcastRecivers.ConnectionBrodcastReciver.Companion.TAG, "${i.productName} will be added to server .. ")
                listOfProduct = this?.getAllProducts()!!
                for (i in listOfProduct) {
                    productList.add(ProductListModel(i._id, i.productName, i.price, i.imageUrl, i.totalViews))
                    Log.e(TAG, "product added!! ${i.productName}")
                }

//                }
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

    }

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

    private fun getAccessToken() {
        userId = SharedPrefHelper.getString(this, SharedPrefHelper.USER_ID_KEY, "")
        authToken = SharedPrefHelper.getString(this, SharedPrefHelper.AUTH_TOKEN_KEY, "")
    }


    public fun loadJson(pageNumber: Int) {
        productList.clear()
        var requestObject = JSONObject()

        requestObject.put("userId", userId)
        requestObject.put("searchText", "")
        requestObject.put("pageNumber", pageNumber)
        requestObject.put("accessToken", authToken)

        Log.e(TAG, requestObject.toString())

        var jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST,
            ApiHelper.BASE_URL + ApiHelper.PRODUCT_LIST, requestObject,
            Response.Listener<JSONObject> {
                Log.e(TAG, it.toString())
                var data = it.getJSONObject("data")
                var productListArray = data.getJSONArray("productList")
                Log.e(TAG, " array length ${productListArray.length()}")
                for (i in 0 until (productListArray.length())) {
                    var product = productListArray.getJSONObject(i)
                    productList.add(
                        ProductListModel(
                            product.getString("_id"),
                            product.getString("productName"),
                            product.getString("price"),
                            product.getString("image"),
                            product.getInt("totalViews")
                        )
                    )
                    Log.e(TAG, "list size ${productList.size}")
                    if (productList.size == 0) {
                        swipe_n_refresh_delete.isRefreshing = false
                    }
                    adaptor.notifyDataSetChanged()
                    if (swipe_n_refresh_delete.isRefreshing) {
                        swipe_n_refresh_delete.isRefreshing = false
                    }
                }
            },

            Response.ErrorListener {

            })
        if (isNetworkAvailable(this)) {
            mQueue.add(jsonObjectRequest)
        }

    }

    override fun onResume() {
        super.onResume()
        adaptor.notifyDataSetChanged()
        swipe_n_refresh_delete.isRefreshing = true

    }

}
