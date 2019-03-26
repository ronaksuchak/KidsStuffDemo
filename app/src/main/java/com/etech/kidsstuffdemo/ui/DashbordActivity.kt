package com.etech.kidsstuffdemo.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.etech.kidsstuffdemo.R
import com.etech.kidsstuffdemo.adaptors.ProductListAdaptor
import com.etech.kidsstuffdemo.helpers.ApiHelper
import com.etech.kidsstuffdemo.helpers.InfiniteScroll
import com.etech.kidsstuffdemo.helpers.SharedPrefHelper
import com.etech.kidsstuffdemo.models.ProductListModel
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_dashbord.*
import kotlinx.android.synthetic.main.app_bar_dashbord.*
import kotlinx.android.synthetic.main.content_dashbord.*
import org.json.JSONObject

class DashbordActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var productList = mutableListOf<ProductListModel>()
    private lateinit var mQueue: RequestQueue
    private val TAG = "Kids Stuff"
    var authToken = ""
    var userId = ""
    var adaptor = ProductListAdaptor(this,productList)
    var layoutManager= LinearLayoutManager(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mQueue = Volley.newRequestQueue(this)

        setContentView(R.layout.activity_dashbord)
        getAccessToken()
        loadJson(1)

        setSupportActionBar(toolbar)
        supportActionBar?.title="Product List"


        recyclerView.layoutManager = layoutManager

        recyclerView.adapter = adaptor
        recyclerView.addOnScrollListener(InfiniteScroll(layoutManager) {

            //println("load page $it")
            Toast.makeText(this,"current page is $it",Toast.LENGTH_SHORT).show()
            loadJson(it)
            adaptor.notifyDataSetChanged()

        })


        swipe_n_refresh.setOnRefreshListener {
            productList.clear()
            loadJson(1)
            adaptor.notifyDataSetChanged()

            recyclerView.addOnScrollListener(InfiniteScroll(layoutManager) {

                println("load page $it")
                //productList.clear()
                Toast.makeText(this,"current page is $it",Toast.LENGTH_SHORT).show()
                loadJson(it)

            })

            adaptor.notifyDataSetChanged()

            Toast.makeText(this,"current page is 1",Toast.LENGTH_SHORT).show()
        }

        fab.setOnClickListener { view ->
            startActivity(Intent(this@DashbordActivity, AddProductActivity::class.java))
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun getAccessToken() {
        userId = SharedPrefHelper.getString(this, SharedPrefHelper.USER_ID_KEY, "")
        authToken = SharedPrefHelper.getString(this, SharedPrefHelper.AUTH_TOKEN_KEY, "")
    }



    private fun loadJson(pageNumber:Int) {

        var requestObject = JSONObject()

        requestObject.put("userId", userId)
        requestObject.put("searchText", "")
        requestObject.put("pageNumber", pageNumber)
        requestObject.put("accessToken", authToken)

        Log.e(TAG, requestObject.toString())

        var jsonObjectRequest = JsonObjectRequest(Request.Method.POST,
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
                    adaptor.notifyDataSetChanged()
                    if(swipe_n_refresh.isRefreshing){
                        swipe_n_refresh.isRefreshing = false
                    }
                }
            },

            Response.ErrorListener {

            })
        mQueue.add(jsonObjectRequest)


    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.dashbord, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_camera -> {
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
