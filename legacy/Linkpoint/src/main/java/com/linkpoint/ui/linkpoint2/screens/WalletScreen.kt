package com.linkpoint.ui.linkpoint2.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linkpoint.economy.CurrencyFormatter
import com.linkpoint.ui.components.linkpoint2.primitives.L2GlassSurface
import com.linkpoint.ui.components.linkpoint2.primitives.L2Row
import com.linkpoint.ui.components.linkpoint2.primitives.L2SectionHeader
import com.linkpoint.ui.components.linkpoint2.primitives.L2TonalButton
import com.linkpoint.ui.components.linkpoint2.primitives.L2TopBar
import com.linkpoint.ui.components.linkpoint2.tokens.Linkpoint2
import java.text.NumberFormat
import java.util.Locale

data class WalletTransaction(
    val id: String,
    val title: String,
    val subtitle: String,
    val amountLinden: Long,
    val timestamp: String,
    val isIncome: Boolean,
)

/**
 * Cluster H — Wallet. See design/screens-extra.jsx → WalletScreen.
 */
@Composable
fun WalletScreen(
    balanceLinden: Long,
    usdEquivalent: Double,
    weeklyIn: Long,
    weeklyOut: Long,
    transactions: List<WalletTransaction>,
    currencySymbol: String = "L$",
    isZeroCurrency: Boolean = false,
    onBack: () -> Unit,
    onSend: () -> Unit,
    onRequest: () -> Unit,
    onBuy: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = Linkpoint2.tokens
    val nf = NumberFormat.getNumberInstance(Locale.US)
    val activeSymbol = currencySymbol.ifEmpty { "L$" }

    Scaffold(
        topBar = {
            L2TopBar(
                title = "Wallet",
                subtitle = if (isZeroCurrency) "No Currency System" else "$activeSymbol balance",
                leading = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                trailing = {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (isZeroCurrency) {
                item {
                    L2GlassSurface(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp),
                    ) {
                        Column {
                            Text(
                                text = "No Currency System",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "This grid operates with no currency system. Payments and transactions are disabled.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = tokens.onSurfaceDim,
                            )
                        }
                    }
                }
            }

            item {
                BalanceCard(balanceLinden, usdEquivalent, activeSymbol, isZeroCurrency, nf)
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    L2TonalButton(
                        onClick = if (isZeroCurrency) { {} } else onSend,
                        modifier = Modifier.weight(1f),
                        enabled = !isZeroCurrency
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Send")
                    }
                    L2TonalButton(
                        onClick = if (isZeroCurrency) { {} } else onRequest,
                        modifier = Modifier.weight(1f),
                        enabled = !isZeroCurrency
                    ) {
                        Icon(Icons.Default.ArrowDownward, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Request")
                    }
                    L2TonalButton(
                        onClick = if (isZeroCurrency) { {} } else onBuy,
                        modifier = Modifier.weight(1f),
                        enabled = !isZeroCurrency
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Buy $activeSymbol")
                    }
                }
            }
            item {
                L2GlassSurface(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                ) {
                    Column {
                        Text("Weekly summary",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface)
                        Spacer(Modifier.height(8.dp))
                        Row {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Income", color = tokens.onSurfaceDim, style = MaterialTheme.typography.labelSmall)
                                Text(
                                    CurrencyFormatter.formatTransactionAmount(weeklyIn, activeSymbol, isZeroCurrency),
                                    color = tokens.success,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = FontFamily.Monospace,
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Outflow", color = tokens.onSurfaceDim, style = MaterialTheme.typography.labelSmall)
                                Text(
                                    CurrencyFormatter.formatTransactionAmount(-weeklyOut, activeSymbol, isZeroCurrency),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = FontFamily.Monospace,
                                )
                            }
                        }
                    }
                }
            }
            item {
                L2SectionHeader("Transactions")
            }
            items(transactions, key = { it.id }) { tx ->
                L2Row(
                    headline = tx.title,
                    supporting = "${tx.subtitle} · ${tx.timestamp}",
                    leading = {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(tokens.radii.md))
                                .background(
                                    if (tx.isIncome) tokens.success.copy(alpha = 0.18f)
                                    else MaterialTheme.colorScheme.surfaceVariant,
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                if (tx.isIncome) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                contentDescription = null,
                                tint = if (tx.isIncome) tokens.success else tokens.onSurfaceDim,
                            )
                        }
                    },
                    trailing = {
                        val amountStr = if (tx.isIncome) tx.amountLinden else -tx.amountLinden
                        Text(
                            text = CurrencyFormatter.formatTransactionAmount(amountStr, activeSymbol, isZeroCurrency),
                            color = if (tx.isIncome) tokens.success else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace,
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun BalanceCard(
    balanceLinden: Long,
    usdEquivalent: Double,
    symbol: String,
    isZeroCurrency: Boolean,
    nf: NumberFormat,
) {
    val cs = MaterialTheme.colorScheme
    val tokens = Linkpoint2.tokens
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(tokens.radii.xl))
            .background(
                Brush.linearGradient(
                    colors = listOf(cs.primary, cs.tertiary),
                ),
            )
            .padding(20.dp),
    ) {
        Column {
            Text(
                if (isZeroCurrency) "Grid economy" else "$symbol balance",
                style = MaterialTheme.typography.labelMedium,
                color = cs.onPrimary.copy(alpha = 0.8f),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                CurrencyFormatter.formatCurrency(balanceLinden, symbol, isZeroCurrency),
                fontSize = if (isZeroCurrency) 24.sp else 38.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = cs.onPrimary,
            )
            if (!isZeroCurrency) {
                Text(
                    "≈ \$${"%,.2f".format(usdEquivalent)} USD",
                    style = MaterialTheme.typography.bodyMedium,
                    color = cs.onPrimary.copy(alpha = 0.7f),
                )
            }
        }
    }
}
