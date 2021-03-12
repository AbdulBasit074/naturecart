package com.example.naturescart.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.naturescart.R
import com.example.naturescart.databinding.FragmentAboutBinding


class AboutFragment : Fragment() {

    private lateinit var mBinding: FragmentAboutBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.emailBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:customercare@naturescart.ae")
            try {
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                startActivity(Intent.createChooser(intent, "Send Email"))
            }
        }
        mBinding.phoneBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse(StringBuilder().append("tel:").append("+971509341258").toString())
            startActivity(intent)
        }
        mBinding.facebookBtn.setOnClickListener {
            openLink("https://www.facebook.com/Naturescartuae")
        }
        mBinding.instagramBtn.setOnClickListener {
            openLink("https://www.instagram.com/naturescartuae/")
        }
    }

    private fun openLink(link: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(link)
        try {
            startActivity(i)
        } catch (e: Exception) {
            e.printStackTrace()
            startActivity(Intent.createChooser(i, "Open Link"))
        }
    }

}