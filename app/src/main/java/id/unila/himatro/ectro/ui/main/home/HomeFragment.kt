package id.unila.himatro.ectro.ui.main.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import id.unila.himatro.ectro.R
import id.unila.himatro.ectro.databinding.FragmentHomeBinding
import id.unila.himatro.ectro.databinding.FragmentProfileBinding
import id.unila.himatro.ectro.ui.auth.LoginActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            Glide.with(requireContext())
                .load(R.drawable.ic_default_profile)
                .into(ivUserPhoto)

            Glide.with(requireContext())
                .load(R.drawable.sample_announcement_image)
                .into(announcement)

            ivUserPhoto.setOnClickListener {

            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}