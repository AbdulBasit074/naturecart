package com.example.naturescart.adapters

import android.widget.*
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.naturescart.R
import com.example.naturescart.helper.TranslationsHelper
import com.mikhaellopez.circularimageview.CircularImageView
import com.skydoves.powerspinner.PowerSpinnerView


@BindingAdapter("setCircularImage")
fun setCircularImage(imageView: CircularImageView, src: String?) {
    Glide.with(imageView.context).load(src).into(imageView)
}
@BindingAdapter("translationText")
fun translationText(textView: TextView, key: String) {
    textView.text = TranslationsHelper.getInstance(textView.context).getTranslation(key)
}
@BindingAdapter("translationRadioButton")
fun translationRadioButton(radioBtn: RadioButton, key: String) {
    radioBtn.text = TranslationsHelper.getInstance(radioBtn.context).getTranslation(key)
}

@BindingAdapter("translationTextButton")
fun translationTextButton(btn: Button, key: String) {
    btn.text = TranslationsHelper.getInstance(btn.context).getTranslation(key)
}
@BindingAdapter("translationTextHint")
fun translationTextHint(textView: TextView, key: String) {
    textView.hint= TranslationsHelper.getInstance(textView.context).getTranslation(key)
}
@BindingAdapter("translationEditTextHint")
fun translationEditTextHint(textView: EditText, key: String) {
    textView.hint= TranslationsHelper.getInstance(textView.context).getTranslation(key)
}
@BindingAdapter("translationSpinnerTextHint")
fun translationEditTextHint(spinner: PowerSpinnerView, key: String) {
    spinner.hint= TranslationsHelper.getInstance(spinner.context).getTranslation(key)
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
@BindingAdapter("setUnit")
fun setUnit(textView: TextView,unit : String) {
    textView.text = textView.context.getString(R.string.unit_text,unit)
}


@BindingAdapter("textNoItems")
fun setTextNoItems(textView: TextView, src: Int) {
    textView.text = textView.context.getString(R.string.no_items, src.toString())
}
