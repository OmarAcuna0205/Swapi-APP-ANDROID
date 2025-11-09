@file:OptIn(ExperimentalFoundationApi::class)

package com.swapi.swapiV1.onboarding.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.swapi.swapiV1.onboarding.viewmodel.OnboardingViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingView(
    viewModel: OnboardingViewModel,
    onFinish: () -> Unit = {}
) {
    val pages by viewModel.pages.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()

    val pagerState = rememberPagerState(
        initialPage = currentPage,
        pageCount = { pages.size }
    )
    val scope = rememberCoroutineScope()
    LaunchedEffect(pagerState.currentPage) {
        viewModel.setPage(pagerState.currentPage)
    }
    LaunchedEffect(currentPage) {
        if (pagerState.currentPage != currentPage) {
            pagerState.scrollToPage(currentPage)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            OnboardingPageView(
                pageModel = pages[page],
                selected = page == pagerState.currentPage
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            DotsIndicatorView(
                totalDots = pages.size,
                selectedIndex = currentPage
            )

            BottomBarView(
                isLastPage = viewModel.isLastPage(),
                page = currentPage,
                total = pages.size,
                onPrev = {
                    if (currentPage > 0) {
                        scope.launch { pagerState.animateScrollToPage(currentPage - 1) }
                    }
                },
                onNext = {
                    if (!viewModel.isLastPage()) {
                        scope.launch { pagerState.animateScrollToPage(currentPage + 1) }
                    } else {
                        onFinish()
                    }
                }
            )
        }
    }
}