package id.ac.unila.ee.himatro.ectro.ui.event.notes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.databinding.ActivityAddNoteBinding
import id.ac.unila.ee.himatro.ectro.ui.event.participant.ParticipantListActivity
import id.ac.unila.ee.himatro.ectro.ui.main.MainActivity
import id.ac.unila.ee.himatro.ectro.ui.profile.DetailUserActivity
import id.ac.unila.ee.himatro.ectro.viewmodel.NoteViewModel
import id.ac.unila.ee.himatro.ectro.viewmodel.UserViewModel

@AndroidEntryPoint
class AddNoteActivity : AppCompatActivity() {

    private val binding: ActivityAddNoteBinding by lazy {
        ActivityAddNoteBinding.inflate(layoutInflater)
    }

    private val noteViewModel: NoteViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val eventId = intent.getStringExtra(EXTRA_EVENT_ID)

        if (eventId != null) {
            observeNote(eventId)
        }

        binding.apply {
            btnBack.setOnClickListener { onBackPressed() }

            btnAddNote.setOnClickListener {
                var isValid = true
                val text = edtNote.text.toString().trim()

                if (text.isEmpty()) {
                    edtNote.error = "Anda belum mengisi catatan"
                    isValid = false
                }

                if (isValid && eventId != null) {
                    noteViewModel.addNote(text, eventId)
                    noteViewModel.isError.observe(this@AddNoteActivity) { isError ->
                        if (isError) {
                            noteViewModel.responseMessage.observe(this@AddNoteActivity) {
                                if (it != null) {
                                    it.getContentIfNotHandled()?.let { msg ->
                                        Toast.makeText(
                                            this@AddNoteActivity,
                                            msg,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(
                                this@AddNoteActivity,
                                getString(R.string.successfully_add_note),
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(
                                Intent(
                                    this@AddNoteActivity,
                                    MainActivity::class.java
                                )
                            )
                            finishAffinity()
                        }
                    }
                }
            }
        }
    }

    private fun observeNote(eventId: String) {
        noteViewModel.checkNoteByEventId(eventId)
        noteViewModel.noteAlreadyAdded.observe(this) { alreadyAdded ->
            if (alreadyAdded) {
                binding.layoutCreateNote.visibility = View.GONE
                binding.layoutNoteInfo.visibility = View.VISIBLE
                noteViewModel.noteEntity.observe(this) { noteEntity ->
                    binding.tvNoteContent.text = noteEntity.noteContent
                    observeUser(noteEntity.userId)
                }
            } else {
                binding.layoutCreateNote.visibility = View.VISIBLE
                binding.layoutNoteInfo.visibility = View.GONE
            }
        }
    }

    private fun observeUser(userId: String) {
        userViewModel.getUserDataByUid(userId).observe(this) { user ->
            binding.apply {
                tvUserName.text = user.name

                if (user.photoUrl.isNotEmpty()) {
                    Glide.with(applicationContext)
                        .load(user.photoUrl)
                        .into(ivUserPhoto)
                } else {
                    Glide.with(applicationContext)
                        .load(R.drawable.ic_default_profile)
                        .into(ivUserPhoto)
                }

                layoutCreatedBy.setOnClickListener {
                    startActivity(
                        Intent(this@AddNoteActivity, DetailUserActivity::class.java)
                            .putExtra(DetailUserActivity.EXTRA_USER_ID, userId)
                    )
                }
            }
        }
    }

    companion object {
        const val EXTRA_EVENT_ID = "extra_event_id"
    }
}