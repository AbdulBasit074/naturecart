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
import com.example.naturescart.helper.Constants


class AboutFragment : Fragment() {

    private lateinit var mBinding: FragmentAboutBinding
    private lateinit var phone: String
    private lateinit var email: String
    private lateinit var insta: String
    private lateinit var fbPageId: String


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTranslationForMissionSection()

        phone = Constants.getTranslate(requireContext(), "about_phone")
        email = Constants.getTranslate(requireContext(), "email_about")
        insta = Constants.getTranslate(requireContext(), "instagram_about")
        fbPageId = Constants.getTranslate(requireContext(), "fb_page_id")



        mBinding.emailContainer.emailBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:$email")
            try {
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                startActivity(Intent.createChooser(intent, "Send Email"))
            }
        }
        mBinding.whatsAppBtn.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(Constants.getWhatsAppUrl(requireContext()))
            startActivity(intent)
        }
        mBinding.phoneBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse(StringBuilder().append("tel:").append(phone).toString())
            startActivity(intent)
        }
        mBinding.facebookBtn.setOnClickListener {
            openLink("fb://page/$fbPageId")
        }
        mBinding.instagramBtn.setOnClickListener {
            openLink(insta)
        }
    }

    private fun setTranslationForMissionSection() {
        mBinding.ourMissionContainer.aboutTitleMission.text = Constants.getTranslate(requireContext(), "our_mission")
        mBinding.ourMissionContainer.aboutLabelMission.text = Constants.getTranslate(requireContext(), "our_mission_detail")
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