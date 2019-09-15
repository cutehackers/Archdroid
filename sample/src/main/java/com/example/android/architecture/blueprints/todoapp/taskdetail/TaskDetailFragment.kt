/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.architecture.blueprints.todoapp.taskdetail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.TextView
import app.junhyounglee.archroid.annotations.BindMvpPresenter
import app.junhyounglee.archroid.annotations.MvpFragmentView
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.addedittask.AddEditTaskActivity
import com.example.android.architecture.blueprints.todoapp.addedittask.AddEditTaskFragment
import com.example.android.architecture.blueprints.todoapp.util.showSnackBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

/**
 * Main UI for the task detail screen.
 */
@MvpFragmentView(TaskDetailView::class, R.layout.taskdetail_frag)
@BindMvpPresenter(TaskDetailPresenter::class)
class TaskDetailFragment : MvpTaskDetailFragment() {

    private lateinit var detailTitle: TextView
    private lateinit var detailDescription: TextView
    private lateinit var detailCompleteStatus: CheckBox

    override val taskId: String by lazy {
        arguments!!.getString(ARGUMENT_TASK_ID)
    }

    override var isActive: Boolean = false
        get() = isAdded

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = super.onCreateView(inflater, container, savedInstanceState) as View

        setHasOptionsMenu(true)
        root.apply {
            detailTitle = findViewById(R.id.task_detail_title)
            detailDescription = findViewById(R.id.task_detail_description)
            detailCompleteStatus = findViewById(R.id.task_detail_complete)
        }

        // Set up floating action button
        activity?.findViewById<FloatingActionButton>(R.id.fab_edit_task)?.setOnClickListener {
            presenter.editTask()
        }

        return root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val deletePressed = item.itemId == R.id.menu_delete
        if (deletePressed) presenter.deleteTask()
        return deletePressed
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.taskdetail_fragment_menu, menu)
    }

    override fun setLoadingIndicator(active: Boolean) {
        if (active) {
            detailTitle.text = ""
            detailDescription.text = getString(R.string.loading)
        }
    }

    override fun hideDescription() {
        detailDescription.visibility = View.GONE
    }

    override fun hideTitle() {
        detailTitle.visibility = View.GONE
    }

    override fun showDescription(description: String) {
        with(detailDescription) {
            visibility = View.VISIBLE
            text = description
        }
    }

    override fun showCompletionStatus(complete: Boolean) {
        with(detailCompleteStatus) {
            isChecked = complete
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    presenter.completeTask()
                } else {
                    presenter.activateTask()
                }
            }
        }
    }

    override fun showEditTask(taskId: String) {
        val intent = Intent(context, AddEditTaskActivity::class.java)
        intent.putExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID, taskId)
        startActivityForResult(intent, REQUEST_EDIT_TASK)
    }

    override fun showTaskDeleted() {
        activity?.finish()
    }

    override fun showTaskMarkedComplete() {
        rootView.showSnackBar(getString(R.string.task_marked_complete), Snackbar.LENGTH_LONG)
    }

    override fun showTaskMarkedActive() {
        rootView.showSnackBar(getString(R.string.task_marked_active), Snackbar.LENGTH_LONG)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_EDIT_TASK) {
            // If the task was edited successfully, go back to the list.
            if (resultCode == Activity.RESULT_OK) {
                activity?.finish()
            }
        }
    }

    override fun showTitle(title: String) {
        with(detailTitle) {
            visibility = View.VISIBLE
            text = title
        }
    }

    override fun showMissingTask() {
        detailTitle.text = ""
        detailDescription.text = getString(R.string.no_data)
    }

    companion object {

        private const val ARGUMENT_TASK_ID = "TASK_ID"

        private const val REQUEST_EDIT_TASK = 1

        fun newInstance(taskId: String) =
            TaskDetailFragment().apply {
                arguments = prepareArgs(taskId)
            }

        private fun prepareArgs(taskId: String) = Bundle().apply {
            putString(ARGUMENT_TASK_ID, taskId)
        }
    }

}