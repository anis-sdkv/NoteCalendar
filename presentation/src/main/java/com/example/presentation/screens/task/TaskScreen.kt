package com.example.presentation.screens.task

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.presentation.R
import com.example.presentation.tools.RusDateTimeFormatter
import com.example.presentation.ui.components.CustomTextField
import com.example.presentation.ui.theme.AppTheme
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Composable
fun TaskScreen(
    navController: NavController,
    viewModel: TaskScreenViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val eventHandler = viewModel::event
    val action by viewModel.action.collectAsStateWithLifecycle(null)

    LaunchedEffect(action) {
        when (action) {
            TaskScreenSideEffect.NavigateBack -> {
                navController.navigateUp()
            }

            null -> Unit
        }
    }

    BackHandler {
        eventHandler.invoke(TaskScreenEvent.OnNavigateBack)
    }

    Column(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.ime)
    ) {
        Header(state, eventHandler)
        TaskFields(state, eventHandler)
    }

    DateTimeDialogs(state, eventHandler)
    MessageDialogs(state, eventHandler)
}

@Composable
private fun Header(state: TaskScreenState, eventHandler: (TaskScreenEvent) -> Unit) {
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(60.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { eventHandler.invoke(TaskScreenEvent.OnNavigateBack) }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_ios_24),
                    contentDescription = "back",
                    modifier = Modifier.scale(0.8f)
                )
            }
            Text(
                text = stringResource(id = R.string.task),
                modifier = Modifier.padding(start = 12.dp),
                style = AppTheme.typography.bold20
            )
        }

        IconButton(
            onClick = if (state.isEditMode) {
                { eventHandler.invoke(TaskScreenEvent.OnModeChange(false)) }
            } else {
                { eventHandler.invoke(TaskScreenEvent.OnModeChange(true)) }
            },
            modifier = Modifier
                .align(Alignment.CenterEnd)
        ) {
            Icon(
                painter = painterResource(id = if (state.isEditMode) R.drawable.baseline_done_24 else R.drawable.baseline_edit_24),
                contentDescription = "edit",
                modifier = Modifier.scale(if (state.isEditMode) 1f else 0.8f)
            )
        }
    }
}

@Composable
fun TaskFields(state: TaskScreenState, eventHandler: (TaskScreenEvent) -> Unit) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
            .scrollable(scrollState, Orientation.Vertical)
    ) {

        val titlePadding = 20.dp
        if (state.isEditMode)
            CustomTextField(
                value = state.taskName,
                onValueChange = { eventHandler.invoke(TaskScreenEvent.OnNameChange(it)) },
                modifier = Modifier
                    .padding(top = titlePadding)
                    .fillMaxWidth(),
                textStyle = AppTheme.typography.medium20,
                singleLine = true,
                hint = stringResource(id = R.string.title)
            )
        else
            Text(
                text = state.taskName,
                style = AppTheme.typography.medium20,
                modifier = Modifier
                    .padding(top = titlePadding)
                    .fillMaxWidth()
                    .clickable { eventHandler.invoke(TaskScreenEvent.OnModeChange(true)) },
                overflow = TextOverflow.Ellipsis
            )

        Text(
            text = stringResource(id = R.string.deadlines) + ":",
            modifier = Modifier.padding(top = 32.dp),
            style = AppTheme.typography.regular20
        )
        Box(modifier = Modifier
            .clickable { eventHandler.invoke(TaskScreenEvent.OnDateTimeClick) }
            .padding(vertical = 12.dp)
            .fillMaxWidth()
            .background(AppTheme.colors.accentBackground, RoundedCornerShape(8.dp))
            .padding(20.dp), contentAlignment = Alignment.Center) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (state.startDate == null || state.finishDate == null) stringResource(id = R.string.hint_deadlines)
                    else "${RusDateTimeFormatter.format(state.startDate)} - ${RusDateTimeFormatter.format(state.finishDate)}",
                    style = AppTheme.typography.regular16,
                    color = AppTheme.colors.accent,
                    textAlign = TextAlign.Center
                )
                if (state.startDate != null && state.finishDate != null)
                    Text(
                        text = stringResource(id = R.string.hint_edit),
                        modifier = Modifier.padding(top = 4.dp),
                        style = AppTheme.typography.regular12,
                        color = AppTheme.colors.accent,
                        textAlign = TextAlign.Center
                    )
            }
        }


        Text(
            text = stringResource(id = R.string.description) + ":",
            modifier = Modifier.padding(top = 20.dp),
            style = AppTheme.typography.regular20
        )
        if (state.isEditMode)
            CustomTextField(
                value = state.taskDescription,
                onValueChange = { eventHandler.invoke(TaskScreenEvent.OnDescriptionChange(it)) },
                textStyle = AppTheme.typography.regular16,
                modifier = Modifier.padding(top = 12.dp),
                hint = stringResource(id = R.string.hint_start_typing)
            )
        else
            Text(
                text = state.taskDescription,
                style = AppTheme.typography.regular16,
                modifier = Modifier
                    .clickable { eventHandler.invoke(TaskScreenEvent.OnModeChange(true)) }
                    .padding(vertical = 12.dp)
            )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimeDialogs(state: TaskScreenState, eventHandler: (TaskScreenEvent) -> Unit) {

    val startDatePickerState = rememberDatePickerState()
    val startTimePickerState = rememberTimePickerState()

    val finishDatePickerState = rememberDatePickerState()
    val finishTimePickerState = rememberTimePickerState()

    if (state.showStartDateSelector)
        CustomDatePicker(
            title = "Выберите время начала",
            startDatePickerState,
            onConfirm = { eventHandler.invoke(TaskScreenEvent.OnStartDateSelected) },
            onDismiss = { eventHandler.invoke(TaskScreenEvent.OnDismissDateTime) }
        )

    if (state.showStartTimeSelector)
        CustomTimePicker(
            pickerState = startTimePickerState,
            onConfirm = { eventHandler.invoke(TaskScreenEvent.OnStartTimeSelected) },
            onDismiss = { eventHandler.invoke(TaskScreenEvent.OnDismissDateTime) }
        )


    if (state.showFinishDateSelector)
        CustomDatePicker(
            title = "Выберите время окончания",
            finishDatePickerState,
            onConfirm = { eventHandler.invoke(TaskScreenEvent.OnFinishDateSelected) },
            onDismiss = { eventHandler.invoke(TaskScreenEvent.OnDismissDateTime) }
        )

    if (state.showFinishTimeSelector)
        CustomTimePicker(
            pickerState = finishTimePickerState,
            onConfirm = {
                val startDate = Instant.ofEpochMilli(startDatePickerState.selectedDateMillis!!)
                val startDateTime = LocalDateTime
                    .ofInstant(startDate, ZoneOffset.UTC)
                    .withHour(startTimePickerState.hour)
                    .withMinute(startTimePickerState.minute)

                val finishDate = Instant.ofEpochMilli(finishDatePickerState.selectedDateMillis!!)
                val finishDateTime = LocalDateTime
                    .ofInstant(finishDate, ZoneOffset.UTC)
                    .withHour(finishTimePickerState.hour)
                    .withMinute(finishTimePickerState.minute)

                eventHandler.invoke(TaskScreenEvent.OnFinishTimeSelected(startDateTime, finishDateTime))
            },
            onDismiss = { eventHandler.invoke(TaskScreenEvent.OnDismissDateTime) }
        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePicker(title: String, pickerState: DatePickerState, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(id = R.string.ok).uppercase(), color = AppTheme.colors.accent)
            }
        },
        colors = DatePickerDefaults.colors(containerColor = AppTheme.colors.primaryBackground)
    ) {
        DatePicker(
            state = pickerState,
            title = {
                Text(
                    text = title, modifier = Modifier.padding(24.dp),
                    style = AppTheme.typography.medium16
                )
            },
            colors = DatePickerDefaults.colors(
                selectedDayContainerColor = AppTheme.colors.accent,
                selectedDayContentColor = AppTheme.colors.primaryBackground,
                selectedYearContainerColor = AppTheme.colors.accent,
                containerColor = AppTheme.colors.primaryBackground,
                todayDateBorderColor = AppTheme.colors.accent,
                todayContentColor = AppTheme.colors.accent,
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTimePicker(pickerState: TimePickerState, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "OK", color = AppTheme.colors.accent)
            }
        },
        colors = DatePickerDefaults.colors(containerColor = AppTheme.colors.primaryBackground)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 40.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            TimePicker(
                state = pickerState,
                colors = TimePickerDefaults.colors(
                    selectorColor = AppTheme.colors.accent,
                    timeSelectorSelectedContainerColor = AppTheme.colors.accentBackground,
                    clockDialColor = AppTheme.colors.accentBackground,
                    timeSelectorUnselectedContainerColor = AppTheme.colors.secondaryBackground
                )
            )
        }
    }
}

@Composable
fun MessageDialogs(state: TaskScreenState, eventHandler: (TaskScreenEvent) -> Unit) {
    if (state.showErrorDialog)
        CustomDialog(
            title = "Ошибка",
            messages = state.errors,
            onDismiss = { eventHandler.invoke(TaskScreenEvent.OnDismissError) },
            onConfirm = { eventHandler.invoke(TaskScreenEvent.OnDismissError) })

    if (state.showSaveDialog)
        CustomDialog(
            title = "Изменения не сохранены",
            messages = listOf("Отменить изменения?"),
            onDismiss = { eventHandler.invoke(TaskScreenEvent.OnDismissSave) },
            onConfirm = { eventHandler.invoke(TaskScreenEvent.OnConfirmSave) })
}

@Composable
fun CustomDialog(title: String, messages: List<String>, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        containerColor = AppTheme.colors.primaryBackground,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = title,
                style = AppTheme.typography.medium20,
                color = AppTheme.colors.primary
            )
        },
        text = {
            LazyColumn {
                items(messages) {
                    Text(
                        text = it,
                        modifier = Modifier.padding(vertical = 4.dp),
                        style = AppTheme.typography.medium12,
                        color = AppTheme.colors.primary
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "OK",
                    style = AppTheme.typography.bold12,
                    color = AppTheme.colors.accent
                )
            }
        },
        onDismissRequest = onDismiss,
    )
}