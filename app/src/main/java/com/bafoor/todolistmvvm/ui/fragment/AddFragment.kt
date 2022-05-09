package com.bafoor.todolistmvvm.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bafoor.todolistmvvm.R
import com.bafoor.todolistmvvm.ui.tasks.AddEditTaskViewModel
import com.bafoor.todolistmvvm.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add.view.*
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class AddFragment : Fragment() {

    private val viewModel : AddEditTaskViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        view.edit_task_name.setText(viewModel.taskName)
        view.check_box_important.isChecked = viewModel.taskImportance
        view.check_box_important.jumpDrawablesToCurrentState()
        view.date_created_tv.isVisible = viewModel.task != null
        view.date_created_tv.text = "Created ${viewModel.task?.createdDateFormatted}"

        view.edit_task_name.addTextChangedListener {
            viewModel.taskName = it.toString()
        }

        view.check_box_important.setOnCheckedChangeListener { _, isChecked ->
            viewModel.taskImportance = isChecked
        }

        view.fab_save_task.setOnClickListener {
            viewModel.onSaveClick()
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditTaskEvent.collect { event ->
             when(event){
                 is AddEditTaskViewModel.AddEditTaskEvent.NavigateBackWithResult -> {
                     view.edit_task_name.clearFocus()
                     setFragmentResult(
                         "add_edit_request",
                         bundleOf("add_edit_request" to event.result)
                     )
                 }
                 is AddEditTaskViewModel.AddEditTaskEvent.ShowInvalidInputMessage -> {
                     Snackbar.make(requireView(), event.txt, Snackbar.LENGTH_SHORT).show()
                 }
             }.exhaustive
            }
        }

        return view
    }
}



