package com.example.presentation.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.presentation.R
import com.example.presentation.navigation.NavGraph
import com.example.presentation.tools.RusDateTimeFormatter
import com.example.presentation.ui.theme.AppTheme
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale


@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainScreenViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val eventHandler = viewModel::event
    val action by viewModel.action.collectAsStateWithLifecycle(null)

    LaunchedEffect(action) {
        when (action) {
            null -> Unit
            is MainScreenSideEffect.NavigateToTaskScreen ->
                navController.navigate(
                    NavGraph.TaskScreen.passTaskId(id = (action as MainScreenSideEffect.NavigateToTaskScreen).id)
                )

            MainScreenSideEffect.NavigateToCreateTaskScreen ->
                navController.navigate(NavGraph.TaskScreen.newTask)
        }
    }

    LaunchedEffect(Unit) {
        eventHandler.invoke(MainScreenEvent.OnLaunch)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.background(AppTheme.colors.secondaryBackground)) {
            CustomCalendar(state, eventHandler)
            DayTable(state, eventHandler)
        }
        AddTaskButton(modifier = Modifier.align(Alignment.BottomEnd), eventHandler)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskButton(modifier: Modifier = Modifier, eventHandler: (MainScreenEvent) -> Unit) {
    Card(
        onClick = { eventHandler.invoke(MainScreenEvent.OnCreateNewTaskClick) },
        modifier = modifier
            .padding(end = 20.dp, bottom = 32.dp)
            .size(60.dp)
            .clickable { },
        elevation = CardDefaults.elevatedCardElevation(8.dp),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.primaryBackground)
    )
    {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .fillMaxHeight()
                    .width(4.dp)
                    .background(AppTheme.colors.accent, RoundedCornerShape(4.dp))
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(AppTheme.colors.accent, RoundedCornerShape(4.dp))
            )
        }
    }
}


@Composable
fun CustomCalendar(state: MainScreenState, eventHandler: (MainScreenEvent) -> Unit) {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) }
    val endMonth = remember { currentMonth.plusMonths(100) }
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }

    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek
    )

    var isFirstInvoke by remember { mutableStateOf(true) }
    LaunchedEffect(calendarState.firstVisibleMonth) {
        if (isFirstInvoke) isFirstInvoke = false
        else eventHandler.invoke(
            MainScreenEvent.OnDateSelected(calendarState.firstVisibleMonth.yearMonth.atDay(1))
        )
    }

    LaunchedEffect(state.currentDay) {
        if (state.currentDay.month != calendarState.firstVisibleMonth.yearMonth.month) {
            calendarState.animateScrollToMonth(YearMonth.of(state.currentDay.year, state.currentDay.month))
        }
    }

    Column(
        modifier = Modifier.background(
            AppTheme.colors.primaryBackground,
            RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
        )
    ) {

        MonthTitle(state, eventHandler)
        Spacer(modifier = Modifier.padding(8.dp))
        AnimatedVisibility(visible = state.calendarVisible) {
            HorizontalCalendar(
                state = calendarState,
                dayContent = { Day(it, it.date == LocalDate.now(), it.date == state.currentDay, eventHandler) },
                monthHeader = { month ->
                    val daysOfWeek = month.weekDays.first().map { it.date.dayOfWeek }
                    DaysOfWeekTitle(daysOfWeek = daysOfWeek)
                    Spacer(modifier = Modifier.padding(8.dp))
                },
                monthFooter = {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            )
        }
    }
}


@Composable
fun MonthTitle(state: MainScreenState, eventHandler: (MainScreenEvent) -> Unit) {
    var currentDayIconHeight by remember { mutableStateOf(0.dp) }

    val animatedMonthRowPadding by animateDpAsState(
        if (state.calendarVisible) currentDayIconHeight else 0.dp,
        label = ""
    )
    val animatedTextBias by animateFloatAsState(if (state.calendarVisible) 0f else -1f, label = "")
    val animatedRightArrowBias by animateFloatAsState(if (state.calendarVisible) 1f else -1f, label = "")

    val localDensity = LocalDensity.current

    Box(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
    ) {
        IconButton(modifier = Modifier
            .align(Alignment.TopEnd)
            .onGloballyPositioned {
                currentDayIconHeight = with(localDensity) { it.size.height.toDp() }
            },
            onClick = { eventHandler.invoke(MainScreenEvent.OnCurrentDayClick) }) {
            Icon(
                painterResource(id = R.drawable.ic_calendar),
                "current day",
                modifier = Modifier.size(20.dp)
            )
        }

        Box(
            modifier = Modifier
                .padding(top = animatedMonthRowPadding)
                .fillMaxWidth()
                .height(currentDayIconHeight),
            contentAlignment = Alignment.CenterStart
        ) {
            AnimatedVisibility(state.calendarVisible) {
                IconButton(onClick = {
                    eventHandler.invoke(MainScreenEvent.OnDateSelected(state.currentDay.minusMonths(1)))
                }) {
                    Icon(
                        painterResource(id = R.drawable.baseline_arrow_back_ios_24),
                        "prev month",
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Text(
                text = "${
                    state.currentDay.month.getDisplayName(
                        java.time.format.TextStyle.FULL_STANDALONE,
                        Locale.getDefault()
                    ).replaceFirstChar { it.uppercaseChar() }
                } ${state.currentDay.year}",
                modifier = Modifier
                    .align(BiasAlignment(animatedTextBias, 0f))
                    .clickable {
                        eventHandler.invoke(MainScreenEvent.OnCalendarVisibilityChange(!state.calendarVisible))
                    },
                style = AppTheme.typography.bold20,
            )

            AnimatedVisibility(
                state.calendarVisible,
                modifier = Modifier.align(BiasAlignment(animatedRightArrowBias, 0f))
            ) {
                IconButton(onClick = {
                    eventHandler.invoke(MainScreenEvent.OnDateSelected(state.currentDay.plusMonths(1)))
                }) {
                    Icon(
                        painterResource(id = R.drawable.baseline_arrow_forward_ios_24),
                        "next month",
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault()),
                style = AppTheme.typography.bold16,
                color = AppTheme.colors.primary.copy(0.4f)
            )
        }
    }
}

@Composable
fun Day(day: CalendarDay, isToday: Boolean, isSelected: Boolean, eventHandler: (MainScreenEvent) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { eventHandler.invoke(MainScreenEvent.OnDateSelected(day.date)) },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(32.dp)
                .aspectRatio(1f)
                .background(
                    color = if (isToday) AppTheme.colors.accent
                    else if (isSelected) AppTheme.colors.accentBackground
                    else Color.Transparent,
                    RoundedCornerShape(32.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day.date.dayOfMonth.toString(),
                color = if (isToday) AppTheme.colors.primaryBackground
                else if (day.position == DayPosition.MonthDate) AppTheme.colors.primary
                else AppTheme.colors.secondary,
            )
        }
    }
}

@Composable
fun DayTable(state: MainScreenState, eventHandler: (MainScreenEvent) -> Unit) {
    LazyColumn {
        item {
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${
                        state.currentDay.month.getDisplayName(
                            java.time.format.TextStyle.FULL_STANDALONE,
                            Locale.getDefault()
                        ).replaceFirstChar { it.uppercaseChar() }
                    } ${state.currentDay.dayOfMonth}, ${state.currentDay.year}",
                    style = AppTheme.typography.medium20
                )

                val tasksCount = state.dayTasks.sumOf { it.size }
                Text(
                    text = "$tasksCount задач${
                        if (tasksCount % 10 == 1) "a"
                        else if (tasksCount % 10 in 2..4) "и"
                        else ""
                    }",
                    modifier = Modifier
                        .background(AppTheme.colors.primaryBackground, RoundedCornerShape(20.dp))
                        .padding(8.dp)
                )
            }
        }

        items(MainScreenViewModel.HOURS.size) { hour ->
            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = MainScreenViewModel.HOURS[hour], style = AppTheme.typography.bold12)
                }

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(2.dp)
                        .background(
                            AppTheme.colors.secondary,
                            when (hour) {
                                0 -> RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)
                                23 -> RoundedCornerShape(bottomStart = 2.dp, bottomEnd = 2.dp)
                                else -> RectangleShape
                            }
                        )
                )

                Column(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 8.dp)
                ) {

                    Spacer(
                        Modifier
                            .padding(top = 16.dp)
                            .height(1.dp)
                            .fillMaxWidth()
                            .background(AppTheme.colors.secondary, RoundedCornerShape(1.dp))
                    )

                    state.dayTasks[hour].forEach {
                        Spacer(modifier = Modifier.height(16.dp))

                        Column(modifier = Modifier
                            .background(Color.White, RoundedCornerShape(4.dp))
                            .clickable {
                                eventHandler.invoke(MainScreenEvent.OnTaskClick(it.id))
                            }
                            .padding(8.dp)
                            .fillMaxWidth()) {

                            Text(
                                text = it.name,
                                style = AppTheme.typography.medium16
                            )
                            Text(
                                text = "${
                                    RusDateTimeFormatter.format(it.startDate)
                                } - ${
                                    RusDateTimeFormatter.format(it.finishDate)
                                }",
                                style = AppTheme.typography.regular12
                            )

                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}