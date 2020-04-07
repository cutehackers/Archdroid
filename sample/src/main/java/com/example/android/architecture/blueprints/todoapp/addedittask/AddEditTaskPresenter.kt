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

import app.junhyounglee.archroid.annotations.MvpPresenter
import com.example.android.architecture.blueprints.todoapp.Injection
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource

/**
 * Listens to user actions from the UI ([AddEditTaskFragment]), retrieves the data and updates
 * the UI as required.
 * @param taskId ID of the task to edit or null for a new task
 *
 * @param tasksRepository a repository of data for tasks
 *
 * @param addTaskView the add/edit view
 *
 * @param isDataMissing whether data needs to be loaded or not (for config changes)
 */
@MvpPresenter(AddEditTaskContract.View::class, AddEditTaskContract.Presenter::class)
class AddEditTaskPresenter(
    addTaskView: AddEditTaskContract.View,
    private val taskId: String?,
    override var isDataMissing: Boolean
) : MvpAddEditTaskPresenter(addTaskView), TasksDataSource.GetTaskCallback {

    private lateinit var tasksRepository: TasksDataSource

    override fun onCreate() {
        view.getContext()?.apply {
            tasksRepository = Injection.provideTasksRepository(applicationContext)
        }
    }

    override fun onTaskLoaded(task: Task) {
        // The view may not be able to handle UI updates anymore
        if (view.isActive) {
            view.setTitle(task.title)
            view.setDescription(task.description)
        }
        isDataMissing = false
    }

    override fun onDataNotAvailable() {
        // The view may not be able to handle UI updates anymore
        if (view.isActive) {
            view.showEmptyTaskError()
        }
    }

    fun start() {
        if (taskId != null && isDataMissing) {
            populateTask()
        }
    }

    override fun saveTask(title: String, description: String) {
        if (taskId == null) {
            createTask(title, description)
        } else {
            updateTask(title, description)
        }
    }

    override fun populateTask() {
        taskId?.apply {
            tasksRepository.getTask(this, this@AddEditTaskPresenter)
        } ?: throw RuntimeException("populateTask() was called but task is new.")
    }

    private fun createTask(title: String, description: String) {
        val newTask = Task(title, description)
        if (newTask.isEmpty) {
            view.showEmptyTaskError()
        } else {
            tasksRepository.saveTask(newTask)
            view.showTasksList()
        }
    }

    private fun updateTask(title: String, description: String) {
        taskId?.apply {
            tasksRepository.saveTask(Task(title, description, this))
            view.showTasksList() // After an edit, go back to the list.
        } ?: throw RuntimeException("updateTask() was called but task is new.")
    }
}
