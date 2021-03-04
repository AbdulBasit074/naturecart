package com.example.naturescart.adapters

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.naturescart.R
import com.mikhaellopez.circularimageview.CircularImageView


@BindingAdapter("setCircularImage")
fun setCircularImage(imageView: CircularImageView, src: String?) {
    Glide.with(imageView.context).load(src).into(imageView)
}

@BindingAdapter("setImage")
fun setImage(imageView: ImageView, src: String) {
    Glide.with(imageView.context).load(src).into(imageView)
}

@BindingAdapter("setTextOrder")
fun setTextOrder(textView: TextView, text: String) {
    textView.text = textView.context.getString(R.string.order_no, text)
}

@BindingAdapter("setText")
fun setText(textView: TextView, text: String) {
    textView.text = text
}

@BindingAdapter("setAddressOrder")
fun setAddressOrder(textView: TextView, address: com.example.naturescart.model.Address) {
    textView.text = address.address
}


@BindingAdapter("textPrice")
fun setTextPrice(textView: TextView, price: Float) {
    textView.text = textView.context.getString(R.string.aed_price, String.format("%.2f", price))
}

@BindingAdapter("textNoItems")
fun setTextNoItems(textView: TextView, src: Int) {
    textView.text = textView.context.getString(R.string.no_items, src.toString())
}
