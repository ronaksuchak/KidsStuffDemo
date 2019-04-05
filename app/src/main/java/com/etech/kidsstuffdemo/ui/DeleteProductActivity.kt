package com.etech.kidsstuffdemo.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import jdroidcoder.ua.paginationrecyclerview.OnPageChangeListener
import kotlinx.android.synthetic.main.activity_delete_product.*
import org.json.JSONObject


class DeleteProductActivity : AppCompatActivity() {


    private lateinit var mQueue: RequestQueue
    private val TAG = "Kids Stuff delete "
    var authToken = ""
    var userId = ""
    var productList = mutableListOf<ProductListModel>()
    var productListDb = mutableListOf<ProductEntity>()
    var layoutManager = LinearLayoutManager(this)
    var adaptor = DeleteProductListAdaptor(this, productList)

    private var db: AppDatabase? = null
    private var productDao: ProductDao? = null


    //private var deleteProductDao: DeleteProductDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mQueue = Volley.newRequestQueue(this)
        setContentView(R.layout.activity_delete_product)
        getAccessToken()
        if (isNetworkAvailable(this)) {
//            productList.clear()
            Log.e(TAG, "OnCreate/network aval")
            loadJson(1)
            recyclerView_delete.layoutManager = layoutManager
            recyclerView_delete.adapter = adaptor
            adaptor.notifyDataSetChanged()

        }
//        } else {
//            productList.clear()
//            loadFromDb()
//
//            Log.e(TAG, "offline load")
//        }
        swipe_n_refresh_delete.setOnRefreshListener {
            productList.clear()
            loadJson(1)
            adaptor.notifyDataSetChanged()
            swipe_n_refresh_delete.isRefreshing = false
        }



//        recyclerView_delete.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                visibleItemCount = recyclerView_delete.getChildCount();
//                totalItemCount = layoutManager.getItemCount();
//                firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
//
//                if (loading) {
//                    if (totalItemCount > previousTotal) {
//                        loading = false
//                        previousTotal = totalItemCount
//                    }
//                }
//
//                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
//                    (++current_page)
//                    Log.e(TAG,"current page no.$current_page")
////                    productList.clear()
//                    loadJson((current_page-1))
//                    adaptor.notifyDataSetChanged()
//                    loading = true
//                }
//
//            }
//
//
//        })

        recyclerView_delete.setOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageChange(page: Int) {
                Toast.makeText(this@DeleteProductActivity,"current page $page",Toast.LENGTH_SHORT).show()
                loadJson(page)
                Log.e(TAG,"page number is $page")
                adaptor.notifyDataSetChanged()

            }
        })
    }


    private fun loadFromDb() {
        Log.e(TAG, "load from DB")
        var listOfProduct = listOf<ProductEntity>()
        Observable.fromCallable {
            db = AppDatabase.getAppDataBase(context = this@DeleteProductActivity)

            productDao = db?.productDao()
            with(productDao) {
                listOfProduct = this?.getAllProducts()!!
                for (i in listOfProduct) {
                    productList.add(ProductListModel(i.ProductId, i.productName, i.price, i.imageUrl, i.totalViews))
                    Log.e(TAG, "product added!! ${i.productName}")
                }
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    private fun getAccessToken() {
        userId = SharedPrefHelper.getString(this, SharedPrefHelper.USER_ID_KEY, "")
        authToken = SharedPrefHelper.getString(this, SharedPrefHelper.AUTH_TOKEN_KEY, "")
    }


    private fun loadJson(pageNumber: Int) {
        Log.e(TAG, "load json")
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

                    productListDb.add(
                        ProductEntity(
                            ProductId = product.getString("_id"),
                            productName = product.getString("productName"),
                            price = product.getString("price"),
                            imageUrl = product.getString("image"),
                            totalViews = product.getInt("totalViews")
                        )
                    )
                    Log.e(TAG, "list size ${productList.size}")
                    Log.e(TAG, "name is ${product.getString("productName")}")

//                    addToDb(productListDb)
                    Log.e(TAG, "products added to DB")
                }
                adaptor.notifyDataSetChanged()
                Log.e(TAG, "data set changed!")
            },
            Response.ErrorListener {
                Log.e(TAG, "${it.localizedMessage}", it)
            })

        if (isNetworkAvailable(this)) {
            mQueue.add(jsonObjectRequest)
        }


    }

    fun addToDb(productListDb: MutableList<ProductEntity>) {
        Observable.fromCallable {
            db = AppDatabase.getAppDataBase(context = this)
            productDao = db?.productDao()
            with(productDao) {
                this?.addProduct(productListDb)
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
            isAvailable = true
        }
        return isAvailable
    }

    override fun onResume() {
        super.onResume()

//        productList.clear()
//        Log.e(TAG, "OnResume")
//        loadJson(1)
//
//        adaptor.notifyDataSetChanged()
//        Log.e(TAG, "On Resume : Dataset changed!")
//
//        productList.clear()
//        Log.e(TAG, "List Been Cleared  ${productList.size}")
//
//        if (swipe_n_refresh_delete.isRefreshing) {
//            swipe_n_refresh_delete.isRefreshing = false
//        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.delete_product_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.add_a_product -> {
                startActivity(Intent(this@DeleteProductActivity, AddProductActivity::class.java))
                return true
            }
            com.etech.kidsstuffdemo.R.id.dashbord_menu_item -> {
                startActivity(Intent(this@DeleteProductActivity, DashbordActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}


