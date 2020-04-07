package com.example.android.architecture.blueprints.todoapp.taskdetail

import app.junhyounglee.archroid.runtime.core.view.MvpView

/**
 * This specifies the contract between the view and the presenter.
 */
interface TaskDetailContract {
    interface View : MvpView {

        val taskId: String

        val isActive: Boolean

        fun setLoadingIndicator(active: Boolean)

        fun showMissingTask()

        fun hideTitle()

        fun showTitle(title: String)

        fun hideDescription()

        fun showDescription(description: String)

        fun showCompletionStatus(complete: Boolean)

        fun showEditTask(taskId: String)

        fun showTaskDeleted()

        fun showTaskMarkedComplete()

        fun showTaskMarkedActive()
    }

    interface Presenter {

        fun editTask()

        fun deleteTask()

        fun completeTask()

        fun activateTask()
    }
}