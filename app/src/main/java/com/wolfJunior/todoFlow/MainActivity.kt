package com.wolfJunior.todoFlow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wolfJunior.todoFlow.ui.main.TodoFlowViewModel
import com.wolfJunior.todoFlow.ui.theme.TodoFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoFlowTheme {
                TodoFlow()
            }
        }
    }
}

@Composable
fun TodoFlow(todoViewModel: TodoFlowViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 顶部标题和加号布局
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "TodoFlow", fontSize = 24.sp)
            IconButton(onClick = { todoViewModel.openDialog() }) {
                Icon(painter = painterResource(id = R.drawable.ic_add), contentDescription = "加号")
            }
        }

        // 列表内容
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(todoViewModel.todoList.size) { index ->
                ListItem(name = todoViewModel.todoList[index])
            }
        }
        // 显示弹窗
        if (todoViewModel.showDialog) {
            AddTodoDialog(
                textState = todoViewModel.textState,
                onTextChange = { newText -> todoViewModel.updateTextState(newText) },
                onDismiss = { todoViewModel.closeDialog() },
                onConfirm = { todoViewModel.addTodoItem() }
            )
        }
    }
}

@Composable
fun ListItem(name: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(color = Color.Gray, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 左侧显示名称
        Text(text = name, fontSize = 16.sp, color = Color.White)

        // 右侧两个图标
        Row {
            IconButton(onClick = { /* 箭头点击事件 */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_up_wide_line),
                    contentDescription = "箭头",
                    tint = Color.White
                )
            }

            IconButton(onClick = { /* 加号点击事件 */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = "加号",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun AddTodoDialog(
    textState: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(8.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 顶部标题和按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "添加代办集", fontSize = 18.sp)
                    Row {
                        IconButton(onClick = { onConfirm() }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_check),
                                contentDescription = "确认",
                                tint = Color.Black
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_close),
                                contentDescription = "取消",
                                tint = Color.Black
                            )
                        }
                    }
                }

                // 输入框
                OutlinedTextField(
                    value = textState,
                    onValueChange = { onTextChange(it) },
                    label = { Text("请输入代办集名称") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMyScreen() {
    TodoFlowTheme {
        TodoFlow()
    }
}