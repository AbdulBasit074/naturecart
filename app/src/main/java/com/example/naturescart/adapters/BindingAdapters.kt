package com.example.naturescart.adapters

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.naturescart.R
import com.mikhaellopez.circularimageview.CircularImageView


@BindingAdapter("setCircularImage")
fun setCircularImage(imageView: CircularImageView,src:String)
{
    Glide.with(imageView.context).load(src).into(imageView)
}
@BindingAdapter("setImage")
fun setImage(imageView: ImageView,src:String)
{
    Glide.with(imageView.context).load(src).into(imageView)
}

@BindingAdapter("textPrice")
fun setTextPrice(textView: TextView, src:Int)
{
    textView.text = textView.context.getString(R.string.aed_price,src.toString())
}
