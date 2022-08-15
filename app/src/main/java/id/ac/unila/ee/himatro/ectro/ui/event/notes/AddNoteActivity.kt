package id.ac.unila.ee.himatro.ectro.ui.event.notes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unila.ee.himatro.ectro.R
import id.ac.unila.ee.himatro.ectro.databinding.ActivityAddNoteBinding
import id.ac.unila.ee.himatro.ectro.ui.event.participant.ParticipantListActivity
import id.ac.unila.ee.himatro.ectro.ui.main.MainActivity
import id.ac.unila.ee.himatro.ectro.ui.profile.DetailUserActivity
import id.ac.unila.ee.himatro.ectro.viewmodel.NoteViewModel
import id.ac.unila.ee.himatro.ectro.viewmodel.UserViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AddNoteActivity : AppCompatActivity() {

    private val binding: ActivityAddNoteBinding by lazy {
        ActivityAddNoteBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var auth: FirebaseAuth

    private val noteViewModel: NoteViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        observeIsLoading()

        val eventId = intent.getStringExtra(EXTRA_EVENT_ID)

        if (eventId != null) {
            observeNote(eventId)
        }


        binding.apply {


            btnBack.setOnClickListener { onBackPressed() }

        }
    }

    private fun observeIsLoading() {
        noteViewModel.isLoading.observe(this){ isLoading ->
            if (isLoading) {
                binding.loadingIndicator.visibility = View.VISIBLE
            } else {
                binding.loadingIndicator.visibility = View.GONE
            }
        }

        // TODO: CREATE SHIMMER LOADING
        userViewModel.isLoading.observe(this) { isLoding ->

        }
    }

    private fun observeNote(eventId: String) {
        noteViewModel.checkNoteByEventId(eventId)

        binding.apply {
            noteViewModel.noteAlreadyAdded.observe(this@AddNoteActivity) { alreadyAdded ->
                if (alreadyAdded) {
                    layoutCreateNote.visibility = View.GONE
                    layoutNoteInfo.visibility = View.VISIBLE
                    noteViewModel.noteEntity.observe(this@AddNoteActivity) { noteEntity ->
                        tvNoteContent.text = noteEntity.noteContent
                        observeUser(noteEntity.userId)

                        val loggedUser = auth.currentUser
                        if (loggedUser?.uid == noteEntity.userId) {
                            btnEdit.visibility = View.VISIBLE

                            // if edit button clicked
                            btnEdit.setOnClickListener {
                                btnAddNote.text = getString(R.string.update)
                                edtNote.setText(noteEntity.noteContent)
                                layoutCreateNote.visibility = View.VISIBLE
                                layoutNoteInfo.visibility = View.GONE

                                btnAddNote.setOnClickListener {
                                    updateNote(noteEntity.noteId)
                                }
                            }
                        } else {
                            btnEdit.visibility = View.GONE
                        }

                    }
                } else {

                    layoutCreateNote.visibility = View.VISIBLE
                    layoutNoteInfo.visibility = View.GONE

                    btnAddNote.setOnClickListener {
                        addNewNote(eventId)
                    }
                }
            }
        }
    }

    private fun addNewNote(eventId: String) {
        binding.apply {
            var isValid = true
            val text = edtNote.text.toString().trim()

            if (text.isEmpty()) {
                edtNote.error = getString(R.string.empty_note)
                isValid = false
            }

            if (isValid) {
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

    private fun updateNote(noteId: String) {
        binding.apply {
            var isValid = true
            val text = edtNote.text.toString().trim()

            if (text.isEmpty()) {
                edtNote.error = getString(R.string.empty_note)
                isValid = false
            }

            if (isValid) {
                noteViewModel.updateNote(text, noteId)

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
                            getString(R.string.successfully_update_note),
                            Toast.LENGTH_SHORT
                        ).show()
                        tvNoteContent.text = text
                        layoutCreateNote.visibility = View.GONE
                        layoutNoteInfo.visibility = View.VISIBLE
                    }
                }
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