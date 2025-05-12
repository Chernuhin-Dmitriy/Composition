package com.example.composition.presentation

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.composition.R
import com.example.composition.databinding.FragmentGameBinding
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.Level
import java.util.Locale

class GameFragment : Fragment() {
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[GameViewModel::class.java]
    }
    private lateinit var level: Level
    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get() = _binding ?: throw RuntimeException("GameFinishedFragment == null")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parsArgs()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.startGame(level)
        setupIndicators()
        setupQuestion()
        observeViewModel()
        setupListeners()
    }

    private fun setupListeners() {
        with(binding) {
            tvOption1.setOnClickListener {
                viewModel.question.value?.let { question ->
                    viewModel.chooseAnswer(question.options[0])
                }
            }
            tvOption2.setOnClickListener {
                viewModel.question.value?.let { question ->
                    viewModel.chooseAnswer(question.options[1])
                }
            }
            tvOption3.setOnClickListener {
                viewModel.question.value?.let { question ->
                    viewModel.chooseAnswer(question.options[2])
                }
            }
            tvOption4.setOnClickListener {
                viewModel.question.value?.let { question ->
                    viewModel.chooseAnswer(question.options[3])
                }
            }
            tvOption5.setOnClickListener {
                viewModel.question.value?.let { question ->
                    viewModel.chooseAnswer(question.options[4])
                }
            }
            tvOption6.setOnClickListener {
                viewModel.question.value?.let { question ->
                    viewModel.chooseAnswer(question.options[5])
                }
            }
        }
    }

    private fun setupQuestion() {
        binding.tvSum.text = "${viewModel.question.value?.sum}"
        binding.tvLeftNumber.text = "${viewModel.question.value?.visibleNumber}"
        val viewOptions = listOf(
            binding.tvOption1,
            binding.tvOption2,
            binding.tvOption3,
            binding.tvOption4,
            binding.tvOption5,
            binding.tvOption6
        )
        for (n in 0 until viewModel.question.value!!.options.size) {
            viewOptions[n].text = viewModel.question.value!!.options[n].toString()
        }
    }

    private fun setupIndicators() {
        with(binding) {
            viewModel.minPercent.value?.let {
                progressBar.secondaryProgress = it
            }
            viewModel.minCount.value?.let {
                tvAnswersProgress.text = String.format(
                    Locale.getDefault(),
                    getString(R.string.tv_progress),
                    0,
                    it
                )
            }
        }
    }

    private fun observeViewModel() {
        with(viewModel) {
            gameResult.observe(viewLifecycleOwner) {
                launchGameFinishedFragment(it)
            }
            question.observe(viewLifecycleOwner) {
                setupQuestion()
            }
            formattedTime.observe(viewLifecycleOwner) {
                binding.tvTimer.text = it
            }
            progressAnswers.observe(viewLifecycleOwner) {
                binding.tvAnswersProgress.text = it
            }
            enoughCount.observe(viewLifecycleOwner) {
                if (it) {
                    binding.tvAnswersProgress.setTextColor(Color.GRAY)
                }
            }
            enoughPercent.observe(viewLifecycleOwner) { enough ->
                viewModel.percentOfRightAnswers.value?.let {
                    binding.progressBar.setProgress(it, true)
                }
                if (enough)
                    binding.progressBar.progressTintList = ColorStateList.valueOf(Color.GREEN)
                else
                    binding.progressBar.progressTintList = ColorStateList.valueOf(Color.RED)
            }
            toastMessage.observe(viewLifecycleOwner) { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun launchGameFinishedFragment(gameResult: GameResult) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, GameFinishedFragment.newInstance(gameResult))
            .addToBackStack(null)
            .commit()
    }

    private fun parsArgs() {
        requireArguments().getParcelable<Level>(KEY_LEVEL)?.let {
            level = it
        }
        Toast.makeText(context, "$level", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val KEY_LEVEL = "level"
        const val NAME = "GameFragment"

        fun newInstance(level: Level): GameFragment {
            return GameFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_LEVEL, level)
                }
            }
        }
    }
}