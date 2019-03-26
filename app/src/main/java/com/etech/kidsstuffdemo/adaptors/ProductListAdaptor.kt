package com.etech.kidsstuffdemo.adaptors

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.etech.kidsstuffdemo.R
import com.etech.kidsstuffdemo.helpers.ApiHelper
import com.etech.kidsstuffdemo.models.ProductListModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.product_list_item.view.*

class ProductListAdaptor(private val context:Context, private val productList:List<ProductListModel>): RecyclerView.Adapter<ProductListAdaptor.ProductListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder {
        return ProductListViewHolder(LayoutInflater.from(context).inflate(R.layout.product_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int) {
        holder.mTextViewProductName.text= "Name: ${productList[position].productName}"
        holder.mTextViewPrice.text = "Price: ${productList[position].price}"
        holder.mTextViewView.text = "Total Views: ${productList[position].totalViews.toString()}"
        Picasso.get().load(productList[position].imageUrl).into(holder.mImageView)
//        Picasso.get().load("${ApiHelper.BASE_URL}uploads/product_media/1553511700528_add-to-queue-button.png").into(holder.mImageView)
    }

    class ProductListViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val mTextViewProductName= view.textViewProductNameRV
        val mTextViewPrice = view.textViewPrice
        val mTextViewView = view.textViewView
        val mImageView = view.imageView2
    }
}