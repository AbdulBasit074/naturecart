package com.example.naturescart.adapters

import android.graphics.Paint
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
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
    textView.hint = TranslationsHelper.getInstance(textView.context).getTranslation(key)
}

@BindingAdapter("translationEditTextHint")
fun translationEditTextHint(textView: EditText, key: String) {
    textView.hint = TranslationsHelper.getInstance(textView.context).getTranslation(key)
}

@BindingAdapter("translationSpinnerTextHint")
fun translationEditTextHint(spinner: PowerSpinnerView, key: String) {
    spinner.hint = TranslationsHelper.getInstance(spinner.context).getTranslation(key)
}


@BindingAdapter("setImage")
fun setImage(imageView: ImageView, src: String) {
    Glide.with(imageView.context).load(src).into(imageView)
}

@BindingAdapter("setSave")
fun setSave(textView: TextView, src: String) {
    textView.text = textView.context.getString(R.string.save, src)
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

@BindingAdapter("textPriceAdapter")
fun setTextPriceSlashAdapter(textView: TextView, price: Float) {
    textView.text = textView.context.getString(R.string.aed_price, String.format("%.2f", price))
    textView.setTextColor(ContextCompat.getColor(textView.context, R.color.black))
    textView.paintFlags = 0
    textView.textSize = textView.context.resources.getDimension(R.dimen._3ssp).toFloat()
    (textView.layoutParams as ConstraintLayout.LayoutParams).apply {
        marginStart = textView.context.resources.getDimension(R.dimen._10sdp).toInt()
    }

}

@BindingAdapter("setUnit")
fun setUnit(textView: TextView, unit: String) {
    textView.text = textView.context.getString(R.string.unit_text, unit)
}


@BindingAdapter("textNoItems")
fun setTextNoItems(textView: TextView, src: Int) {
    textView.text = textView.context.getString(R.string.no_items, src.toString())
}

@BindingAdapter("textSlashPrice")
fun setTextSlashPrice(textView: TextView, src: String) {
    textView.text = src
    textView.setTextColor(ContextCompat.getColor(textView.context, R.color.transparent_black))
    textView.textSize = textView.context.resources.getDimension(R.dimen._3ssp).toFloat()
    textView.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
    (textView.layoutParams as ConstraintLayout.LayoutParams).apply {
        marginStart = textView.context.resources.getDimension(R.dimen._3sdp).toInt()
    }
}

@BindingAdapter("textSlashCartPrice")
fun setTextSlashCartPrice(textView: TextView, src: Float) {
    textView.text = textView.context.getString(R.string.aed_price, String.format("%.2f", src))
    textView.setTextColor(ContextCompat.getColor(textView.context, R.color.transparent_black))
    textView.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
    textView.textSize = textView.context.resources.getDimension(R.dimen._3ssp).toFloat()
    (textView.layoutParams as ConstraintLayout.LayoutParams).apply {
        marginStart = textView.context.resources.getDimension(R.dimen._3sdp).toInt()
    }
}

@BindingAdapter("textSlashCartPriceAdapter")
fun setTextSlashCartPriceAdapter(textView: TextView, src: Float) {
    textView.text = textView.context.getString(R.string.aed_price, String.format("%.2f", src))
    textView.setTextColor(ContextCompat.getColor(textView.context, R.color.black))
    textView.paintFlags = 0
    textView.textSize = textView.context.resources.getDimension(R.dimen._3ssp).toFloat()
    (textView.layoutParams as ConstraintLayout.LayoutParams).apply {
        marginStart = textView.context.resources.getDimension(R.dimen._1sdp).toInt()
    }

}









