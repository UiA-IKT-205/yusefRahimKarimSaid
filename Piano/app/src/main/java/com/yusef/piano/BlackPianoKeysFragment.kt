package com.yusef.piano

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.yusef.piano.databinding.FragmentBlackPianoKeysBinding
import kotlinx.android.synthetic.main.fragment_white_piano_keys.view.*

class BlackPianoKeysFragment : Fragment() {

    private var _binding: FragmentBlackPianoKeysBinding? = null
    private val binding get() = _binding!!

    private lateinit var note: String

    var onKeyReleased: ((note: String) -> Unit?)? = null
    var onKeyPressed: ((note: String) -> Unit?)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            note = it.getString("NOTE") ?: "no note..."
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentBlackPianoKeysBinding.inflate(inflater)
        var view = binding.root

        view.whitePianoKeyButton.setOnTouchListener(object: View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_UP -> this@BlackPianoKeysFragment.onKeyReleased?.invoke(note)
                    MotionEvent.ACTION_DOWN -> this@BlackPianoKeysFragment.onKeyPressed?.invoke(note)
                }
                return true
            }
        })

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(note: String) = WhitePianoKeysFragment().apply {
            arguments = Bundle().apply {
                putString("NOTE", note)
            }
        }
    }
}
