package com.yusef.piano

import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import com.yusef.piano.databinding.FragmentPianoLayoutBinding
import kotlinx.android.synthetic.main.fragment_piano_layout.*
import kotlinx.android.synthetic.main.fragment_piano_layout.view.*
import java.io.File
import java.io.FileOutputStream

class PianoLayout : Fragment() {

    var onSave: ((File: Uri) -> Unit)? = null

    private var _binding: FragmentPianoLayoutBinding? = null
    private val binding get() = _binding!!

    private val naturalTones = listOf("C", "D", "E", "F", "G", "A", "B", "C2", "D2", "E2", "F2", "G2", "A2", "B2")
    private val semiTones = listOf("C#", "D#", "F#", "G#", "A#", "C#2", "D#2", "F#2", "G#2", "A#2")

    private var pianoKeysList: MutableList<PianoSheet> = mutableListOf<PianoSheet>()

    private var isRecording: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPianoLayoutBinding.inflate(layoutInflater)
        val view = binding.root

        val fragmentManager = childFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        naturalTones.forEach {
            val naturalTonePianoKey = WhitePianoKeysFragment.newInstance(it)

            var noteStartTime: Long = 0

            naturalTonePianoKey.onKeyPressed = {
                noteStartTime = SystemClock.elapsedRealtime() - recordingTimeChronometer.base
                println("White piano key pressed: $it")
            }
            naturalTonePianoKey.onKeyReleased = {
                var noteTimeDuration =
                    SystemClock.elapsedRealtime() - recordingTimeChronometer.base - noteStartTime
                val note = PianoSheet(it, noteStartTime, noteTimeDuration)
                pianoKeysList.add(note)

                println("White piano key released: $it")
            }

            fragmentTransaction.add(view.whitePianoKeysLayout.id, naturalTonePianoKey, "note_$it")
        }

        semiTones.forEach {
            val semiTone = WhitePianoKeysFragment.newInstance(it)

            var noteStartTime: Long = 0

            semiTone.onKeyPressed = {
                noteStartTime = SystemClock.elapsedRealtime() - recordingTimeChronometer.base
                println("White piano key pressed: $it")
            }
            semiTone.onKeyReleased = {
                var noteTimeDuration =
                    SystemClock.elapsedRealtime() - recordingTimeChronometer.base - noteStartTime
                val note = PianoSheet(it, noteStartTime, noteTimeDuration)
                pianoKeysList.add(note)

                println("White piano key released: $it")
            }

            fragmentTransaction.add(view.blackPianoKeysLayout.id, semiTone, "note_$it")
        }

        fragmentTransaction.commit()

        view.startStopRecording.setOnClickListener {
            if (!isRecording) {
                pianoKeysList.clear()
                startRecordingTimer()
                startStopRecording.text = "Stop REC"
            } else {
                stopRecordingTimer()
                startStopRecording.text = "Reset REC"
            }
        }

        view.saveMusicSheet.setOnClickListener {
            var fileName = view.fileNameInput.text.toString()

            if (pianoKeysList.count() > 0 && fileName.isNotEmpty()) {
                fileName = "$fileName.music"
                val content: String = pianoKeysList.map {
                    it.toString()
                }.reduce { acc, s -> acc + s + "\n" }
                saveFile(fileName, content)
            }
        }
        return view
    }

    private fun saveFile(fileName: String, content: String) {
        val path = this.activity?.getExternalFilesDir(null)

        if (path != null) {
            val file = File(path, fileName)
            FileOutputStream(file, true).bufferedWriter().use { writer ->
                writer.write(content)
            }

            this.onSave?.invoke(file.toUri())
        }
    }

    private fun startRecordingTimer(){
        if (!isRecording){
            recordingTimeChronometer.base = SystemClock.elapsedRealtime()
            recordingTimeChronometer.start()
            isRecording = true
        }
    }

    private fun stopRecordingTimer(){
        if (isRecording){
            recordingTimeChronometer.stop()
            isRecording = false
        }
    }

}