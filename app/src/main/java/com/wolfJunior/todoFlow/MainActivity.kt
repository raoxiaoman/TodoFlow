package com.wolfJunior.todoFlow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.wolfJunior.todoFlow.ui.main.ChildItemData
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoFlow(viewModel: TodoFlowViewModel = viewModel()) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TodoFlow") },
                actions = {
                    IconButton(onClick = { viewModel.toggleAddParentDialog() }) {
                        Icon(
                            painterResource(id = R.drawable.ic_add),
                            contentDescription = "添加代办集"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(viewModel.parentItems.size) { index ->
                    ParentItem(
                        name = viewModel.parentItems[index].name,
                        children = viewModel.parentItems[index].children,
                        onAddChild = { code, duration ->
                            viewModel.addChildItem(index, code, duration)
                        },
                        onExpandChildren = { viewModel.toggleAddChildDialog(index) }
                    )
                }
            }
        }

        // 添加父条目弹窗
        if (viewModel.showAddParentDialog) {
            AddParentDialog(
                onDismiss = { viewModel.toggleAddParentDialog() },
                onConfirm = { name ->
                    viewModel.addParentItem(name)
                    viewModel.toggleAddParentDialog()
                }
            )
        }

        // 添加子条目弹窗
        if (viewModel.showAddChildDialog) {
            AddChildDialog(
                onDismiss = { viewModel.toggleAddChildDialog(-1) },
                onConfirm = { code, duration ->
                    viewModel.addChildItem(viewModel.currentParentIndex, code, duration)
                    viewModel.toggleAddChildDialog(-1)
                }
            )
        }
    }
}

@Composable
fun ParentItem(
    name: String,
    children: List<ChildItemData>,
    onAddChild: (String, Int) -> Unit,
    onExpandChildren: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .background(color = Color.Gray, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = name, fontSize = 16.sp, color = Color.White)

            Row {
                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_up_wide_line),
                        contentDescription = "展开/收起",
                        tint = Color.White
                    )
                }
                IconButton(onClick = { onExpandChildren() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = "添加子条目",
                        tint = Color.White
                    )
                }
            }
        }

        if (isExpanded) {
            Column(modifier = Modifier.padding(start = 10.dp, top = 10.dp)) {
                children.forEach { child ->
                    ChildItem(child = child)
                }
            }
        }
    }
}

@Composable
fun ChildItem(child: ChildItemData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(vertical = 5.dp)
            .background(color = Color.LightGray, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Text(text = child.code, fontSize = 14.sp, modifier = Modifier.weight(1f))

        Text(
            text = "${child.duration}s",
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.Bottom)
        )
    }
}


@Composable
fun AddParentDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var nameState by remember { mutableStateOf(TextFieldValue("")) }

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "添加代办集", fontSize = 18.sp)

                    Row {
                        IconButton(onClick = { onConfirm(nameState.text) }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_check),
                                contentDescription = "确认"
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_close),
                                contentDescription = "取消"
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = nameState,
                    onValueChange = { nameState = it },
                    label = { Text("请输入代表集名称") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun AddChildDialog(onDismiss: () -> Unit, onConfirm: (String, Int) -> Unit) {
    var codeState by remember { mutableStateOf(TextFieldValue("")) }
    var durationState by remember { mutableStateOf(0) }

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "添加代表项", fontSize = 18.sp)

                    Row {
                        IconButton(onClick = { onConfirm(codeState.text, durationState) }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_check),
                                contentDescription = "确认"
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_close),
                                contentDescription = "取消"
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = codeState,
                    onValueChange = { codeState = it },
                    label = { Text("请输入代码的内容") },
                    modifier = Modifier.fillMaxWidth()
                )

                DurationPicker(
                    selectedDuration = durationState,
                    onDurationChange = { durationState = it }
                )
            }
        }
    }
}

@Composable
fun DurationPicker(selectedDuration: Int, onDurationChange: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "选择时长（秒）", fontSize = 14.sp)
        Slider(
            value = selectedDuration.toFloat(),
            onValueChange = { onDurationChange(it.toInt()) },
            valueRange = 0f..3600f
        )
        Text(text = "${selectedDuration}s")
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMyScreen() {
    TodoFlowTheme {
        TodoFlow()
    }
}