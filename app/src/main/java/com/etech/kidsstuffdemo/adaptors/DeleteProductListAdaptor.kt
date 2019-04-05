package com.etech.kidsstuffdemo.adaptors

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.etech.kidsstuffdemo.R
import com.etech.kidsstuffdemo.databaseHelper.*
import com.etech.kidsstuffdemo.helpers.ApiHelper
import com.etech.kidsstuffdemo.helpers.SharedPrefHelper
import com.etech.kidsstuffdemo.models.ProductListModel
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.delete_product_list_item.view.*
import org.json.JSONObject


class DeleteProductListAdaptor(private val context: Context, private val productList: MutableList<ProductListModel>) :
    RecyclerView.Adapter<DeleteProductListAdaptor.DeleteProductListViewHolder>() {

    var authToken = ""
    var userId = ""
    lateinit var mQueue: RequestQueue
    private var db: AppDatabase? = null
    private var deleteProductDao: DeleteProductDao? = null
    private var productDao: ProductDao? = null
    val TAG = "ADAPTOR/KIDS STUFF"

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DeleteProductListAdaptor.DeleteProductListViewHolder {

        return DeleteProductListViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.delete_product_list_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return productList.size
    }


    override fun onBindViewHolder(holder: DeleteProductListViewHolder, position: Int) {

        var productId = ""
        holder.mTextViewProductName.text = "Name: ${productList[position].productName}"
        holder.mTextViewPrice.text = "Price: ${productList[position].price}"
        holder.mTextViewView.text = "Total Views: ${productList[position].totalViews.toString()}"
        Picasso.get().load(productList[position].imageUrl).into(holder.mImageView)
        holder.mButtonDelete.setOnClickListener {
            if (isNetworkAvailable(context)) {
                productId = productList[position].id
                deleteProduct(productList[position].id)
                productList.removeAt(position)
                deleteProductFromDb(productId)
                removeProductFromProductDb(productList[position].id)
                notifyDataSetChanged()
            }else{

                addToPandingList(productList[position].id)
                deleteProductFromDb(productId)
                Log.e(TAG,"added to pending list")
                productList.removeAt(position)
                notifyDataSetChanged()
            }
        }

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

    private fun deleteProductFromDb(id: String) {


        Observable.fromCallable {
            db = AppDatabase.getAppDataBase(context = context)

            deleteProductDao = db?.deleteProductDao()
            with(deleteProductDao) {
                this?.removeProduct(id)
                Toast.makeText(context, "Delete From Db Done!!!!", Toast.LENGTH_SHORT).show()
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()


    }

    private fun removeProductFromProductDb(id: String) {
        Observable.fromCallable {
            db = AppDatabase.getAppDataBase(context = context)

            productDao = db?.productDao()
            with(productDao) {
                this?.removeProduct(id)

            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    private fun addToPandingList(id: String) {
        Observable.fromCallable {
            db = AppDatabase.getAppDataBase(context = context)

            deleteProductDao = db?.deleteProductDao()
            with(deleteProductDao) {
                this?.addproduct(DeleteProductEntity(System.currentTimeMillis().toInt(), id))
                Toast.makeText(context, "Delete From Db Done!!!!", Toast.LENGTH_SHORT).show()
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()


    }


    private fun deleteProduct(productId: String) {
        userId = SharedPrefHelper.getString(context, SharedPrefHelper.USER_ID_KEY, "")
        authToken = SharedPrefHelper.getString(context, SharedPrefHelper.AUTH_TOKEN_KEY, "")
        mQueue = Volley.newRequestQueue(context)
        var requestParam: JSONObject = JSONObject()
        requestParam.put("userId", userId)
        requestParam.put("accessToken", authToken)
        requestParam.put("productId", productId)


        var request = JsonObjectRequest(
            Request.Method.POST,
            ApiHelper.BASE_URL + ApiHelper.DELETE_PRODUCTS,
            requestParam,
            Response.Listener {
                Toast.makeText(context, it.getString("message"), Toast.LENGTH_SHORT).show()


            },
            Response.ErrorListener {

            })
        mQueue.add(request)

    }


    class DeleteProductListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val mTextViewProductName = view.textViewProductNameRV
        val mTextViewPrice = view.textViewPrice
        val mTextViewView = view.textViewView
        val mImageView = view.imageView2
        val mButtonDelete = view.button2
    }
}