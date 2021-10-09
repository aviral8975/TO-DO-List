package com.example.to_do_list.ui.addedittask

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.to_do_list.R
import com.example.to_do_list.databinding.FragmentAddEditBinding
import com.example.to_do_list.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddEditTaskFrag : Fragment(R.layout.fragment_add_edit){

    private val viewModel: AddEditTaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddEditBinding.bind(view)

        binding.apply {
            et1.setText(viewModel.taskname).toString()
            cb2.isChecked = viewModel.taskImportance
            cb2.jumpDrawablesToCurrentState()
            tv2.isVisible = viewModel.task != null
            tv2.text = "Created : ${viewModel.task?.createdDateFormatted}"

            et1.addTextChangedListener {
                viewModel.taskname = it.toString()
            }
            
            cb2.setOnCheckedChangeListener { _, isChecked ->
                viewModel.taskImportance = isChecked
            }

            fabsavetask.setOnClickListener {
                viewModel.onSaveClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditTaskEvent.collect { event ->

                when(event){
                    is AddEditTaskViewModel.AddEditTaskEvent.NavigateBackResult -> {
                        binding.et1.clearFocus()
                        setFragmentResult(
                                "add_edit_request",
                                bundleOf("add_edit_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                    is AddEditTaskViewModel.AddEditTaskEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                }.exhaustive
            }
        }
    }

}