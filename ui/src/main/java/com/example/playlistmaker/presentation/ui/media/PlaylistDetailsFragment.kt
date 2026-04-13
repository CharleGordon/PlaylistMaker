package com.example.playlistmaker.presentation.ui.media

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.domain.models.Playlist
import com.example.domain.models.Track
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.example.playlistmaker.presentation.ui.adapters.tracks.TrackAdapter
import com.example.playlistmaker.presentation.ui.player.AudioPlayerFragment
import com.example.playlistmaker.presentation.viewmodel.media.PlaylistDetailsViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.text.isNullOrEmpty

class PlaylistDetailsFragment : Fragment() {

    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private val playlistId by lazy { arguments?.getInt("playlistId") ?: 0 }
    private val viewModel: PlaylistDetailsViewModel by viewModel {
        parametersOf(playlistId)
    }

    private lateinit var trackAdapter: TrackAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupBottomSheet()
        setupListeners()

        viewModel.playlist.observe(viewLifecycleOwner) { playlist ->
            renderPlaylistInfo(playlist)
        }

        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            if (tracks != null) {
                renderTracks(tracks)
            }
        }

        viewModel.getData()

        viewModel.playlistDeleted.observe(viewLifecycleOwner) { isDeleted ->
            if (isDeleted) {
                findNavController().popBackStack()
            }
        }
    }

    private fun setupAdapter() {
        trackAdapter = TrackAdapter(
            tracks = mutableListOf(),
            clickListener = { track ->
                val trackJson = Gson().toJson(track)
                findNavController().navigate(R.id.action_playlistDetailsFragment_to_audioPlayerFragment, AudioPlayerFragment.createArgs(trackJson))
            },
            longClickListener = { track ->
                showDeleteTrackDialog(track)
            }
        )

        binding.tracksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.tracksRecyclerView.adapter = trackAdapter
    }

    private fun setupBottomSheet() {
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.tracksBottomSheet)
        bottomSheetBehavior.isHideable = false

        binding.root.post {
            val screenHeight = binding.root.height
            val buttonsBottom = binding.shareButton.bottom
            bottomSheetBehavior.peekHeight = screenHeight - buttonsBottom - 24
        }

        menuBottomSheetBehavior = BottomSheetBehavior.from(binding.menuBottomSheet.root).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.shareButton.setOnClickListener {
            sharePlaylist()
        }

        binding.menuButton.setOnClickListener {
            val playlist = viewModel.playlist.value ?: return@setOnClickListener

            binding.menuBottomSheet.playlistItem.trackName.text = playlist.title
            binding.menuBottomSheet.playlistItem.tracksCount.text = "${playlist.tracksCount} ${getTracksWord(playlist.tracksCount)}"

            Glide.with(this)
                .load(playlist.imagePath)
                .placeholder(R.drawable.placeholder)
                .into(binding.menuBottomSheet.playlistItem.trackImage)

            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.menuBottomSheet.menuShare.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            sharePlaylist()
        }

        binding.menuBottomSheet.menuEdit.setOnClickListener {
            val bundle = bundleOf("playlistId" to playlistId)
            findNavController().navigate(R.id.action_playlistDetailsFragment_to_editPlaylistFragment, bundle)
        }

        binding.menuBottomSheet.menuDelete.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            showDeletePlaylistDialog()
        }

    }

    private fun renderPlaylistInfo(playlist: Playlist) {
        binding.playlistTitle.text = playlist.title

        if (playlist.description.isNullOrEmpty()) {
            binding.playlistDescription.visibility = View.GONE
        } else {
            binding.playlistDescription.visibility = View.VISIBLE
            binding.playlistDescription.text = playlist.description
        }

        Glide.with(this)
            .load(playlist.imagePath)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .into(binding.playlistCoverLarge)
    }

    private fun renderTracks(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            binding.tracksRecyclerView.visibility = View.GONE
            binding.placeholderWithoutTracks.visibility = View.VISIBLE

            binding.totalDuration.text = "0 ${getMinutesWord(0)}"
            binding.tracksCount.text = "0 ${getTracksWord(0)}"

            trackAdapter.updateTracks(emptyList())
        } else {
            binding.tracksRecyclerView.visibility = View.VISIBLE
            binding.placeholderWithoutTracks.visibility = View.GONE

            trackAdapter.updateTracks(tracks)

            val totalMillis = tracks.sumOf { it.trackTimeMillis }
            val minutes = (totalMillis / 60000).toInt()

            binding.totalDuration.text = "$minutes ${getMinutesWord(minutes)}"
            binding.tracksCount.text = "${tracks.size} ${getTracksWord(tracks.size)}"
        }
    }

    private fun showDeleteTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext(), R.style.MyAlertDialogTheme)
            .setMessage(getString(R.string.delete_track_message))
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.deleteTrack(track.trackId)
            }
            .show()
    }

    private fun showDeletePlaylistDialog() {

        val playlistName = viewModel.playlist.value?.title ?: ""

        MaterialAlertDialogBuilder(requireContext(), R.style.MyAlertDialogTheme)
            .setMessage(getString(R.string.delete_playlist_message, playlistName))
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.deletePlaylist()
                findNavController().popBackStack()
            }
            .show()
    }

    private fun sharePlaylist() {
        val tracks = viewModel.tracks.value ?: emptyList()
        val playlist = viewModel.playlist.value ?: return

        if (tracks.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.empty_playlist_share_error), Toast.LENGTH_SHORT).show()
            return
        }

        val sb = StringBuilder()
        sb.append("${playlist.title}\n")
        if (!playlist.description.isNullOrEmpty()) sb.append("${playlist.description}\n")
        sb.append("${tracks.size} ${getTracksWord(tracks.size)}\n")

        tracks.forEachIndexed { index, track ->
            val time = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
            sb.append("${index + 1}. ${track.artistName} - ${track.trackName} ($time)\n")
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, sb.toString())
        }
        startActivity(Intent.createChooser(intent, "Share Playlist"))
    }

    private fun getMinutesWord(minutes: Int): String {
        val preLastDigit = minutes % 100 / 10
        if (preLastDigit == 1) return "минут"
        return when (minutes % 10) {
            1 -> "минута"
            2, 3, 4 -> "минуты"
            else -> "минут"
        }
    }

    private fun getTracksWord(count: Int): String {
        val preLastDigit = count % 100 / 10
        if (preLastDigit == 1) return "треков"
        return when (count % 10) {
            1 -> "трек"
            2, 3, 4 -> "трека"
            else -> "треков"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}