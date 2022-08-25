package id.ac.unila.ee.himatro.ectro.ui.profile

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.databinding.ActivityEditProfileBinding
import id.ac.unila.ee.himatro.ectro.ui.main.MainActivity
import id.ac.unila.ee.himatro.ectro.viewmodel.UserViewModel
import java.io.ByteArrayOutputStream


@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity() {

    private val binding: ActivityEditProfileBinding by lazy {
        ActivityEditProfileBinding.inflate(layoutInflater)
    }

    private val viewModel: UserViewModel by viewModels()

//    private lateinit var scannedBitmap: Bitmap
//    private var bitmapState: Bitmap? = null

    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }

    private val db: FirebaseFirestore by lazy {
        Firebase.firestore
    }
    private var fileSelected: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        observeUserData()
        observeIsLoading()

        binding.apply {
            btnCaptureProfile.setOnClickListener {
                checkGalleryPermission()

                // TODO: REOPEN WHEN ALREADY CAN SAVE CAPTURED IMAGE FROM CAMERA TO GALLERY
//                val pictureDialog = AlertDialog.Builder(this@EditProfileActivity)
//                pictureDialog.setTitle(getString(R.string.choose_action))
//                val pictureDialogItem = arrayOf(
//                    getString(R.string.get_picture_from_gallery),
//                    getString(R.string.take_picture_with_camera)
//                )
//                pictureDialog.setItems(pictureDialogItem) { _, which ->
//                    when (which) {
//                        0 -> checkGalleryPermission()
//                        1 -> checkCameraPermission()
//                    }
//                }
//                pictureDialog.show()
            }

            btnSave.setOnClickListener { editProfile() }
            btnBack.setOnClickListener { onBackPressed() }
        }
    }

    private fun checkGalleryPermission() {
        Dexter.withContext(this).withPermission(
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object : PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                gallery()
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                Toast.makeText(
                    this@EditProfileActivity,
                    getString(R.string.permission_to_get_picture_note_given),
                    Toast.LENGTH_SHORT
                ).show()
                showRotationalDialogForPermission()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?, p1: PermissionToken?
            ) {
                showRotationalDialogForPermission()
            }
        }).onSameThread().check()
    }


    // open gallery
    private fun gallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun checkCameraPermission() {
        Dexter.withContext(this)
            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
            ).withListener(

                object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {
                            if (report.areAllPermissionsGranted()) {
                                camera()
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?
                    ) {
                        showRotationalDialogForPermission()
                    }
                }
            ).onSameThread().check()
    }

    // open camera
    private fun camera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    private fun showRotationalDialogForPermission() {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.not_given_permission_explainantion))

            .setPositiveButton(getString(R.string.settings)) { _, _ ->

                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)

                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }

            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val bitmap = data?.extras?.get("data") as Bitmap

                    Glide.with(this)
                        .load(bitmap)
                        .into(binding.ivUserPhoto)

                    // TODO: SAVE BITMAP TO DEVICE AND GET THE URI
                }

                GALLERY_REQUEST_CODE -> {
                    if (data?.data != null) {
                        val file: Uri? = data.data
                        fileSelected = data.data

                        Glide.with(this)
                            .load(data.data)
                            .into(binding.ivUserPhoto)
                    }
                }
            }
        }
    }

    private fun editProfile() {
        binding.apply {
            val name = edtFullName.text.toString().trim()
            val npm = edtStudentId.text.toString().trim()
            val instagram = edtInstagram.text.toString().trim()
            val linkedin = edtLinkedin.text.toString().trim()

            val isValid = checkInputValidation(name, npm)

            if (isValid) {
                if (fileSelected != null) {
                    val imageExtension = MimeTypeMap.getSingleton()
                        .getExtensionFromMimeType(
                            contentResolver.getType(
                                fileSelected!!
                            )
                        )

                    val path = "userPhotoProfiles/images-" + System.currentTimeMillis() + "." + imageExtension
                    viewModel.updateUserProfile(name, npm, instagram, linkedin, fileSelected!!, path)
                } else {
                    viewModel.updateUserProfile(name, npm, instagram, linkedin)
                }

                viewModel.isUpdateSuccess.observe(this@EditProfileActivity) { isUpdateSuccess ->
                    if (!isUpdateSuccess) {
                        viewModel.responseMessage.observe(this@EditProfileActivity) {
                            if (it != null) {
                                it.getContentIfNotHandled()?.let { msg ->
                                    Toast.makeText(
                                        this@EditProfileActivity,
                                        msg,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@EditProfileActivity,
                            getString(R.string.successfully_update_data),
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(
                            Intent(
                                this@EditProfileActivity,
                                MainActivity::class.java
                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        )
                        finishAffinity()
                    }
                }
            }
        }
    }

    private fun observeUserData() {
        viewModel.observeLoggedUserData()
        viewModel.loggedUserData.observe(this) { user ->
            if (user != null) {
                binding.apply {
                    edtFullName.setText(user.name)
                    edtStudentId.setText(user.npm)
                    edtInstagram.setText(user.instagram)
                    edtLinkedin.setText(user.linkedin)

                    if (user.photoUrl.isEmpty()) {
                        Glide.with(this@EditProfileActivity)
                            .load(R.drawable.ic_default_profile)
                            .into(ivUserPhoto)
                    } else {
                        Glide.with(this@EditProfileActivity)
                            .load(user.photoUrl)
                            .placeholder(R.drawable.ic_image_loading)
                            .error(R.drawable.ic_image_default)
                            .into(ivUserPhoto)
                    }
                }
            }
        }

    }

    private fun checkInputValidation(name: String, npm: String): Boolean {
        binding.apply {
            var isValid = true

            if (TextUtils.isEmpty(name)) {
                edtFullName.error = getString(R.string.empty_name)
                isValid = false
            } else if (name.length < 3) {
                edtFullName.error = getString(R.string.name_too_short)
                isValid = false
            }

            if (!validStudentId(npm)) {
                isValid = false
                edtStudentId.error = getString(R.string.invalid_id)
            }

            return isValid
        }
    }

    private fun validStudentId(npm: String): Boolean {
        // example of valid NPM: 2015061057
        val validDepartment = "15"
        val electricalPrograms = "031"
        val informaticsPrograms = "061"

        val isValid: Boolean = when {
            npm.length > 10 -> false
            npm.length < 10 -> false
            npm.length == 10 -> {
                val currentDepartment = npm.substring(2, 4)
                val currentPrograms = npm.substring(4, 7)

                if (currentDepartment == validDepartment) {
                    currentPrograms == electricalPrograms || currentPrograms == informaticsPrograms
                } else {
                    false
                }
            }
            else -> true
        }

        return isValid
    }

    private fun observeIsLoading() {
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.loadingIndicator.visibility = View.VISIBLE
            } else {
                binding.loadingIndicator.visibility = View.GONE
            }
        }
    }


    override fun onResume() {
        super.onResume()
//        binding.tvResult.clearFocus()
//        if (bitmapState == null){
//            binding.btnCopy.isEnabled = false
//            binding.btnDetectText.isEnabled = false
//            binding.btnSave.isEnabled = false
//        } else {
//            binding.btnDetectText.isEnabled = true
//        }
    }

    companion object {
        private const val TAG = "EditProfileActivity"
        private const val CAMERA_REQUEST_CODE = 1
        private const val GALLERY_REQUEST_CODE = 2
    }
}