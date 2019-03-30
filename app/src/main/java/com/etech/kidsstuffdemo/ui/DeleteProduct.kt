package com.etech.kidsstuffdemo.ui

import android.content.Context
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
import com.etech.kidsstuffdemo.helpers.ApiHelper
import com.etech.kidsstuffdemo.helpers.InfiniteScroll
import com.etech.kidsstuffdemo.helpers.SharedPrefHelper
import com.etech.kidsstuffdemo.models.ProductListModel
import kotlinx.android.synthetic.main.activity_delete_product.*
import org.json.JSONObject

class DeleteProduct : AppCompatActivity() {


    private lateinit var mQueue: RequestQueue
    private val TAG = "Kids Stuff delete "
    var authToken = ""
    var userId = ""
    var productList = mutableListOf<ProductListModel>()
    var layoutManager = LinearLayoutManager(this)
    var adaptor = DeleteProductListAdaptor(this, productList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mQueue = Volley.newRequestQueue(this)
        setContentView(R.layout.activity_delete_product)

        getAccessToken()
        loadJson(1)
        Log.e(TAG, "load json from on create called..")

        recyclerView_delete.layoutManager = layoutManager

        recyclerView_delete.adapter = adaptor
        recyclerView_delete.addOnScrollListener(InfiniteScroll(layoutManager) {

            //println("load page $it")
            Toast.makeText(this, "current page is ${it - 1}", Toast.LENGTH_SHORT).show()
            loadJson(it - 1)
            Log.e(TAG, "load json from RV On scroll called..")
            adaptor.notifyDataSetChanged()

        })


        swipe_n_refresh_delete.setOnRefreshListener {
            productList.clear()
            loadJson(1)
            Log.e(TAG, "load json from SWIP in refresh listener called..")
            adaptor.notifyDataSetChanged()

//            recyclerView_delete.addOnScrollListener(InfiniteScroll(layoutManager) {
//
//                println("load page ${it - 1}")
//                //productList.clear()
//                Toast.makeText(this, "current page is ${it - 1}", Toast.LENGTH_SHORT).show()
//                loadJson(it)
//
//            })

            adaptor.notifyDataSetChanged()

            //Toast.makeText(this,"current page is 1",Toast.LENGTH_SHORT).show()
        }

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
        mQueue.add(jsonObjectRequest)


    }

    override fun onResume() {
        super.onResume()
        adaptor.notifyDataSetChanged()
        swipe_n_refresh_delete.isRefreshing = true

    }

}
