package com.example.composition.presentation

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

class GameFragment : Fragment() {
    private lateinit var viewModel: GameViewModel
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
        viewModel = ViewModelProvider(this)[GameViewModel::class.java]
        viewModel.createQuestion(level)  // генерирует вопрос
        setupBinding()
        observeViewModel()
        viewModel.startTimer(level)
        setupListeners()
    }

    private fun setupListeners() {
        with(binding){
            tvOption1.setOnClickListener {
                viewModel.checkAnswer(viewModel.question.options[0])
            }
            tvOption2.setOnClickListener {
                viewModel.checkAnswer(viewModel.question.options[1])
            }
            tvOption3.setOnClickListener {
                viewModel.checkAnswer(viewModel.question.options[2])
            }
            tvOption4.setOnClickListener {
                viewModel.checkAnswer(viewModel.question.options[3])
            }
            tvOption5.setOnClickListener {
                viewModel.checkAnswer(viewModel.question.options[4])
            }
            tvOption6.setOnClickListener {
                viewModel.checkAnswer(viewModel.question.options[5])
            }
        }
    }

    private fun setupBinding() {
        val viewOptions = listOf(binding.tvOption1, binding.tvOption2, binding.tvOption3, binding.tvOption4, binding.tvOption5, binding.tvOption6)
        with(binding){
            tvAnswersProgress.text = "Правильных ответов ${viewModel.countOfRightAnswers} (минимум ${viewModel.getGameSettings(level).minCountOfRightAnswers})"
            tvSum.text = "${viewModel.question.sum}"
            tvLeftNumber.text = "${viewModel.question.visibleNumber}"
        }
        for(n in 0 until viewModel.question.options.size) {
            viewOptions[n].text = viewModel.question.options[n].toString()
        }
    }

    private fun observeViewModel() {
        with(viewModel){
            gameOver.observe(viewLifecycleOwner){
                launchGameFinishedFragment(viewModel.gameOver(level))
            }
            newQuestion.observe(viewLifecycleOwner){
                viewModel.createQuestion(level)
                setupBinding()
            }
            toastMessage.observe(viewLifecycleOwner){ message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
            gameTimeLeft.observe(viewLifecycleOwner) { time ->
                binding.tvTimer.text = if(time.toInt() > 9) "00:$time" else "00:0$time"
            }
            progressBar.observe(viewLifecycleOwner) { count ->
                binding.progressBar.setProgress(count, true)
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
        requireArguments().getParcelable<Level>(KEY_LEVEL)?.let{
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