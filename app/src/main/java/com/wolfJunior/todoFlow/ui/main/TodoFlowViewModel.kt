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
    // 保存代办项列表
    var todoList by mutableStateOf(listOf<String>())
        private set

    // 保存弹窗显示状态
    var showDialog by mutableStateOf(false)
        private set

    // 保存输入框的文本状态
    var textState by mutableStateOf(TextFieldValue(""))

    // 显示弹窗
    fun openDialog() {
        showDialog = true
    }

    // 关闭弹窗并重置输入框状态
    fun closeDialog() {
        showDialog = false
        resetTextState() // 重置输入框
    }

    // 添加代办项
    fun addTodoItem() {
        if (textState.text.isNotEmpty()) {
            todoList = todoList + textState.text
            closeDialog() // 添加后关闭弹窗
        }
    }

    // 更新输入框状态
    fun updateTextState(newText: TextFieldValue) {
        textState = newText
    }

    // 重置输入框状态
    private fun resetTextState() {
        textState = TextFieldValue("")
    }
}