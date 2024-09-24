package com.wolfJunior.todoFlow.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel

/**
 * @author raohui
 * Created on 2024/9/23
 * Email:raohui@inshot.com
 */
class TodoFlowViewModel : ViewModel() {
    var parentItems by mutableStateOf(listOf<ParentItemData>())
        private set

    var showAddParentDialog by mutableStateOf(false)
        private set

    var showAddChildDialog by mutableStateOf(false)
        private set

    var currentParentIndex by mutableStateOf(-1)
        private set

    var textState by mutableStateOf(TextFieldValue(""))

    fun toggleAddParentDialog() {
        showAddParentDialog = !showAddParentDialog
    }

    fun toggleAddChildDialog(index: Int) {
        currentParentIndex = index
        showAddChildDialog = !showAddChildDialog
    }

    fun addParentItem(name: String) {
        parentItems = parentItems + ParentItemData(name)
    }

    fun addChildItem(parentIndex: Int, code: String, duration: Int) {
        val parent = parentItems[parentIndex]
        val updatedParent = parent.copy(children = parent.children + ChildItemData(code, duration))
        parentItems = parentItems.toMutableList().apply { set(parentIndex, updatedParent) }
    }
}

data class ParentItemData(val name: String, val children: List<ChildItemData> = emptyList())
data class ChildItemData(val code: String, val duration: Int)