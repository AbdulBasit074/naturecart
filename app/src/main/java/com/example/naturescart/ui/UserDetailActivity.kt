package com.example.naturescart.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.naturescart.R
import com.example.naturescart.databinding.ActivityUserDetailBinding
import com.example.naturescart.helper.*
import com.example.naturescart.model.User
import com.example.naturescart.model.room.NatureDb
import com.example.naturescart.services.Results
import com.example.naturescart.services.auth.AuthService
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import org.greenrobot.eventbus.EventBus
import java.io.ByteArrayOutputStream


class UserDetailActivity : AppCompatActivity(), Results {

    private val galleryImagePick: Int = 2001
    private val cameraImagePick: Int = 2023
    private val uploadAvatar: Int = 1123
    private lateinit var binding: ActivityUserDetailBinding
    private var loggedUser: User? = null
    private val logoutRequest: Int = 222
    private lateinit var loadingView: LoadingDialog
    private val pickImageRequest: Int = 3331
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_detail)
        loadingView = LoadingDialog(this)
        loggedUser = NatureDb.getInstance(this).userDao().getLoggedUser()
        binding.user = loggedUser
        setListeners()
    }

    private fun setListeners() {
        binding.editProfileBtn.setOnClickListener {
            moveTo(EditProfileActivity::class.java)
        }
        binding.changeBtn.setOnClickListener {
            checkPermission()
            binding.pickImageOBS.cameraLayout.setOnClickListener {
                val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(takePicture, cameraImagePick)
            }
            binding.pickImageOBS.galleryLayout.setOnClickListener {
                val takePicture = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(takePicture, galleryImagePick)

            }
        }
        binding.manageAddressBtn.setOnClickListener {
            moveToIntent(AddressActivity.newInstance(this, false))
        }
        binding.backBtn.setOnClickListener { onBackPressed() }
        binding.logout.setOnClickListener {
            loadingView.show()
            AuthService(logoutRequest, this).userLogout(loggedUser!!.accessToken)
        }
        binding.profileBtn.setOnClickListener {
            BottomSheetBehavior.from(binding.pickImageOBS.imagePickOL).state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun checkPermission() {
        /*** check permission and get current location**/
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    BottomSheetBehavior.from(binding.pickImageOBS.imagePickOL).state = BottomSheetBehavior.STATE_EXPANDED
                }

                override fun onPermissionRationaleShouldBeShown(p0: MutableList<PermissionRequest>?, p1: PermissionToken?) {
                    p1?.continuePermissionRequest()
                }
            }).check()
    }

    override fun onSuccess(requestCode: Int, data: String) {
        Handler(Looper.getMainLooper()).postDelayed({
            loadingView.dismiss()
            when (requestCode) {
                logoutRequest -> {
                    NatureDb.getInstance(this).userDao().logOut()
                    EventBus.getDefault().postSticky(LogoutEvent())
                    finish()
                }
                uploadAvatar -> {
                    DialogCustom(this, R.drawable.ic_thumb, "Image Uploaded Successfully").showDialog()
                    val user = Gson().fromJson(data, User::class.java)
                    user.accessToken = loggedUser!!.accessToken
                    NatureDb.getInstance(this).userDao().logOut()
                    NatureDb.getInstance(this).userDao().login(user)
                    loggedUser = NatureDb.getInstance(this).userDao().getLoggedUser()
                    Glide.with(this).load(loggedUser?.avatar).into(binding.profileBtn)
                }
            }
        }, 1000)
    }

    override fun onFailure(requestCode: Int, data: String) {
        loadingView.dismiss()
        showToast(data)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        BottomSheetBehavior.from(binding.pickImageOBS.imagePickOL).state = BottomSheetBehavior.STATE_COLLAPSED
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                galleryImagePick -> {
                    loadingView.show()
                    AuthService(uploadAvatar, this).uploadAvatar(convertToByteCode(data?.data!!), loggedUser!!.accessToken)
                }
                cameraImagePick -> {
                    loadingView.show()
                    AuthService(uploadAvatar, this).uploadAvatar(convertToByteCode(data?.data!!), loggedUser!!.accessToken)
                }
            }
        }
    }

    private fun convertToByteCode(uriPath: Uri): ByteArray {
        val inputStream = contentResolver.openInputStream(uriPath)
        val selectedImageBitmap = BitmapFactory.decodeStream(inputStream)
        return encodedImage(selectedImageBitmap)
    }

    private fun encodedImage(image: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        return baos.toByteArray()
    }
}
