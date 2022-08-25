package id.ac.unila.ee.himatro.ectro.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.data.model.User
import id.ac.unila.ee.himatro.ectro.databinding.ActivityDetailUserBinding
import id.ac.unila.ee.himatro.ectro.utils.Constants
import id.ac.unila.ee.himatro.ectro.viewmodel.UserViewModel

@AndroidEntryPoint
class DetailUserActivity : AppCompatActivity() {

    private val binding: ActivityDetailUserBinding by lazy {
        ActivityDetailUserBinding.inflate(layoutInflater)
    }

    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        val userId = intent.getStringExtra(EXTRA_USER_ID)
        observeUser(userId)
        observeIsLoading()
        binding.btnBack.setOnClickListener { onBackPressed() }
    }

    private fun observeUser(userId: String?) {
        if (userId != null){
            viewModel.getUserDataByUid(userId).observe(this) { user ->
                setupUi(user)
            }
        }
    }

    private fun setupUi(user: User) {
        binding.apply {
            tvUserName.text = user.name
            tvUserInstagram.text = user.instagram.ifEmpty { "-" }
            tvUserLinkedin.text = user.linkedin.ifEmpty { "-" }

            if (user.role.division.isEmpty() && user.role.position.isEmpty()) {
                tvPosition.visibility = View.GONE
                tvDummyPosition.visibility = View.GONE
            } else {
                tvPosition.text = "${user.role.position} ${user.role.division}"
                tvDummyPosition.text = "${user.role.position} ${user.role.division}"
                tvPosition.visibility = View.VISIBLE
                tvDummyPosition.visibility = View.VISIBLE
            }

            if (user.role.department.isEmpty()) {
                tvDepartment.visibility = View.GONE
                tvDummyDepartment.visibility = View.GONE
            } else {
                tvDepartment.text = user.role.department
                tvDummyDepartment.text = user.role.department
                tvDepartment.visibility = View.VISIBLE
                tvDummyDepartment.visibility = View.VISIBLE
            }

            if (user.photoUrl.isEmpty()) {
                Glide.with(this@DetailUserActivity)
                    .load(R.drawable.ic_default_profile)
                    .into(ivUserPhoto)
            } else {
                Glide.with(this@DetailUserActivity)
                    .load(user.photoUrl)
                    .placeholder(R.drawable.ic_image_loading)
                    .error(R.drawable.ic_image_default)
                    .into(ivUserPhoto)
            }

            tvUserInstagram.setOnClickListener {
                val uri: Uri = Uri.parse(Constants.INSTAGRAM_BASE_LINK + user.instagram)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }

            tvUserLinkedin.setOnClickListener {
                val uri: Uri = Uri.parse(Constants.LINKEDIN_BASE_LINK + user.linkedin)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }
    }


    private fun observeIsLoading() {
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading){
                binding.loadingIndicator.visibility = View.VISIBLE
            } else {
                binding.loadingIndicator.visibility = View.GONE
            }
        }
    }


    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
    }
}