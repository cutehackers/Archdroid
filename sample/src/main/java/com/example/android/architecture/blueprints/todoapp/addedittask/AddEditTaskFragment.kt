/*
 * Copyright 2017, The Android Open Source Project
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

package com.example.android.architecture.blueprints.todoapp.addedittask

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import app.junhyounglee.archroid.annotations.BindMvpPresenter
import app.junhyounglee.archroid.annotations.MvpFragmentView
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.util.showSnackBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

/**
 * Main UI for the add task screen. Users can enter a task title and description.
 */
@MvpFragmentView(AddEditTaskView::class, R.layout.addtask_frag)
@BindMvpPresenter(AddEditTaskPresenter::class)
class AddEditTaskFragment : MvpAddEditTaskFragment() {

    override val taskId: String? = null

    override var isDataMissing: Boolean = true

    override var isActive = false
        get() = isAdded

    private lateinit var title: TextView
    private lateinit var description: TextView


    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.findViewById<FloatingActionButton>(R.id.fab_edit_task_done)?.apply {
            setImageResource(R.drawable.ic_done)
            setOnClickListener {
                presenter.saveTask(title.text.toString(), description.text.toString())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = super.onCreateView(inflater, container, savedInstanceState) as View
        root.apply {
            title = findViewById(R.id.add_task_title)
            description = findViewById(R.id.add_task_description)
        }
        setHasOptionsMenu(true)

        // Prevent the presenter from loading data from the repository if this is a config change.
        // Data might not have loaded when the config change happen, so we saved the state.
        isDataMissing = savedInstanceState?.getBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY) ?: true

        return root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState.apply {
            putBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY, isDataMissing)
        })
    }

    override fun showEmptyTaskError() {
        title.showSnackBar(getString(R.string.empty_task_message), Snackbar.LENGTH_LONG)
    }

    override fun showTasksList() {
        activity?.apply {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun setTitle(title: String) {
        this.title.text = title
    }

    override fun setDescription(description: String) {
        this.description.text = description
    }

    companion object {
        const val SHOULD_LOAD_DATA_FROM_REPO_KEY = "SHOULD_LOAD_DATA_FROM_REPO_KEY"

        const val ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID"

        fun newInstance(taskId: String?) =
            AddEditTaskFragment().apply {
                arguments = Bundle().apply {
                    putString(ARGUMENT_EDIT_TASK_ID, taskId)
                }
            }
    }
}