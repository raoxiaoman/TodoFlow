# CLAUDE.md

此文件为 Claude Code (claude.ai/code) 提供在此代码库中工作的指导。

## 项目概述

TodoFlow 是一个基于 Jetpack Compose 和 Kotlin 构建的 Android 待办事项管理应用。应用支持管理父待办事项（代办集）和子事项（代表项），并带有时长跟踪功能。

## 构建命令

```bash
# 构建调试版 APK
./gradlew assembleDebug

# 构建正式版 APK
./gradlew assembleRelease

# 运行单元测试
./gradlew test

# 运行 lint 检查
./gradlew lint

# 清理构建
./gradlew clean
```

## 项目架构

- **单 Activity**: `MainActivity.kt` 承载所有 Compose UI
- **ViewModel**: `TodoFlowViewModel.kt` 使用 Compose runtime `mutableStateOf` 管理状态
- **数据模型**: `ParentItemData` 和 `ChildItemData` 定义在 `TodoFlowViewModel.kt` 中
- **主题**: `ui/theme/` 下的 Material3 主题（Color.kt, Theme.kt, Type.kt）

## 主要 UI 组件

所有Composable组件都在 `MainActivity.kt` 中：
- `TodoFlow()` - 带有顶部栏和懒加载列表的主界面
- `ParentItem()` - 可展开的父待办事项组
- `ChildItem()` - 显示子事项的代码和时长
- `AddParentDialog()` / `AddChildDialog()` - 创建事项的对话框
- `DurationPicker()` - 自定义滚轮选择器（时/分/秒）
- `NumberPicker()` - 基于 LazyColumn 的可滚动数字选择器

## 技术栈

- Kotlin 1.9.0
- Jetpack Compose + Material3
- AndroidX Lifecycle ViewModel
- Gradle 8.7，使用版本目录 (`gradle/libs.versions.toml`)
- minSdk 24, targetSdk 34
