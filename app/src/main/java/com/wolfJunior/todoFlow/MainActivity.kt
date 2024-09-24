package com.wolfJunior.todoFlow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
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
    var selectedHours by remember { mutableIntStateOf(0) }
    var selectedMinutes by remember { mutableIntStateOf(0) }
    var selectedSeconds by remember { mutableIntStateOf(0) }

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
                        IconButton(onClick = {
                            val totalSeconds =
                                selectedHours * 3600 + selectedMinutes * 60 + selectedSeconds
                            onConfirm(codeState.text, totalSeconds)
                        }) {
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

                // 滚轮选择时分秒
                DurationPicker(
                    hours = selectedHours,
                    minutes = selectedMinutes,
                    seconds = selectedSeconds,
                    onDurationChange = { h, m, s ->
                        selectedHours = h
                        selectedMinutes = m
                        selectedSeconds = s
                    }
                )
            }
        }
    }
}

@Composable
fun DurationPicker(
    hours: Int,
    minutes: Int,
    seconds: Int,
    onDurationChange: (Int, Int, Int) -> Unit
) {
    var selectedHours by remember { mutableIntStateOf(hours) }
    var selectedMinutes by remember { mutableIntStateOf(minutes) }
    var selectedSeconds by remember { mutableIntStateOf(seconds) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 小时滚轮选择
        NumberPicker(
            value = selectedHours,
            range = 0..23,
            onValueChange = { selectedHours = it }
        )
        Text(text = "小时", fontSize = 14.sp, modifier = Modifier.align(Alignment.CenterVertically))

        // 分钟滚轮选择
        NumberPicker(
            value = selectedMinutes,
            range = 0..59,
            onValueChange = { selectedMinutes = it }
        )
        Text(text = "分钟", fontSize = 14.sp, modifier = Modifier.align(Alignment.CenterVertically))

        // 秒钟滚轮选择 - 确保宽度足够
        NumberPicker(
            value = selectedSeconds,
            range = 0..59,
            onValueChange = { selectedSeconds = it }
        )
        Text(text = "秒", fontSize = 14.sp, modifier = Modifier.align(Alignment.CenterVertically))
    }

    // 更新外部状态
    onDurationChange(selectedHours, selectedMinutes, selectedSeconds)
}

@Composable
fun NumberPicker(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = value)

    // 滚动到当前选中的数字
    LaunchedEffect(value) {
        if (listState.firstVisibleItemIndex != value) {
            listState.animateScrollToItem(value)
        }
    }

    Box(
        modifier = Modifier
            .height(100.dp)  // 控制整个滚轮选择器的高度
            .width(40.dp),   // 增加宽度，适应更多数字
        contentAlignment = Alignment.Center // 确保选中的值始终居中
    ) {
        // LazyColumn 中心对齐
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            items(range.toList()) { item ->
                Text(
                    text = item.toString(),
                    fontSize = 15.sp,  // 减小字体大小
                    modifier = Modifier
                        .height(35.dp) // 确保每个数字的高度一致
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            onValueChange(item)
                        },
                    textAlign = TextAlign.Center // 数字居中对齐
                )
            }
        }

        // 中心选择框，作为视觉上的参考线
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(40.dp) // 参考框的高度与单个数字项一致
                .background(
                    color = Color.Transparent, // 可视需求设定透明度或边框线
                    shape = RoundedCornerShape(8.dp)
                )
        )
    }

    // 滚动结束时更新选择的值
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                val adjustedValue = range.elementAt(index)
                if (adjustedValue != value) {
                    onValueChange(adjustedValue)
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