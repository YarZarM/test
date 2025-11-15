package com.example.myapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.flowRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapp.ui.components.ActionPill
import com.example.myapp.ui.components.CircularGauge
import com.example.myapp.ui.components.RiskFactorChip
import com.example.myapp.ui.viewmodel.HomeViewModel
import com.example.myapp.ui.viewmodel.HomeViewModelFactory
import com.example.myapp.ui.viewmodel.RiskLevel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(LocalContext.current)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF080808))
    ) {
        when {
            uiState.isLoading && !uiState.isRefreshing -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(uiState.ringColor)
                    )
                }
            }
            uiState.error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                ) {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    androidx.compose.material3.Button(
                        onClick = { viewModel.loadData(isRefresh = false) }
                    ) {
                        Text("Retry")
                    }
                }
            }
            else -> {
                val listState = rememberLazyListState()
                val pullRefreshState = androidx.compose.material3.pullrefresh.rememberPullRefreshState(
                    refreshing = uiState.isRefreshing,
                    onRefresh = { viewModel.loadData(isRefresh = true) }
                )
                
                Box(modifier = Modifier.fillMaxSize()) {
                    androidx.compose.material3.pullrefresh.pullRefresh(
                        state = pullRefreshState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                bottom = 60.dp,
                                top = 40.dp
                            )
                        ) {
                    item {
                        // Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFF22D3EE),
                                                Color(0xFF8B5CF6)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(20.dp))
                            Column {
                                Text(
                                    text = "Today Migraine Risk",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Updated Migraine Info",
                                    fontSize = 12.sp,
                                    color = Color(0xFF9CA3AF)
                                )
                            }
                        }
                    }
                    
                    item {
                        // Gauge
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularGauge(
                                percentage = uiState.percentage,
                                riskLevel = uiState.riskLevel.name.lowercase()
                                    .replaceFirstChar { it.uppercase() },
                                ringColor = uiState.ringColor,
                                modifier = Modifier.padding(bottom = 18.dp)
                            )
                        }
                    }
                    
                    item {
                        // Top Drivers Section
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 18.dp)
                        ) {
                            Text(
                                text = "TOP DRIVERS",
                                fontSize = 12.sp,
                                color = Color(0xFF9CA3AF),
                                modifier = Modifier.padding(bottom = 8.dp),
                                letterSpacing = 1.sp
                            )
                            
                            androidx.compose.foundation.layout.FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                uiState.migraineData?.topFactors?.forEach { factor ->
                                    RiskFactorChip(
                                        feature = factor.feature,
                                        featureName = viewModel.getFeatureName(factor.feature),
                                        emoji = viewModel.getFeatureEmoji(factor.feature),
                                        score = factor.score,
                                        scoreColor = viewModel.getFactorColor(factor.score),
                                        arrow = viewModel.getFactorArrow(factor.score),
                                        isSelected = uiState.selectedFactor == factor.feature,
                                        onClick = { viewModel.selectFactor(factor.feature) }
                                    )
                                }
                            }
                        }
                    }
                    
                    item {
                        // Recommended Actions Section
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 18.dp)
                        ) {
                            Text(
                                text = "RECOMMENDED ACTIONS",
                                fontSize = 12.sp,
                                color = Color(0xFF9CA3AF),
                                modifier = Modifier.padding(bottom = 8.dp),
                                letterSpacing = 1.sp
                            )
                            
                            if (uiState.selectedFactor == null) {
                                Text(
                                    text = "Select a driver",
                                    color = Color(0xFF888888),
                                    fontSize = 13.sp
                                )
                            } else {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    uiState.filteredActions.forEach { action ->
                                        ActionPill(text = action)
                                    }
                                }
                            }
                        }
                    }
                    }
                    
                    androidx.compose.material3.pullrefresh.PullRefreshIndicator(
                        refreshing = uiState.isRefreshing,
                        state = pullRefreshState,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
            }
        }
    }
}
