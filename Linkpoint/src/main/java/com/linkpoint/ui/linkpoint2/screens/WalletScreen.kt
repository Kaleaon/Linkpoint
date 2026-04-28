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
 * Cluster H — Wallet (L$). See design/screens-extra.jsx → WalletScreen.
 */
@Composable
fun WalletScreen(
    balanceLinden: Long,
    usdEquivalent: Double,
    weeklyIn: Long,
    weeklyOut: Long,
    transactions: List<WalletTransaction>,
    onBack: () -> Unit,
    onSend: () -> Unit,
    onRequest: () -> Unit,
    onBuy: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = Linkpoint2.tokens
    val nf = NumberFormat.getNumberInstance(Locale.US)
    Scaffold(
        topBar = {
            L2TopBar(
                title = "Wallet",
                subtitle = "L\$ balance",
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
            item {
                BalanceCard(balanceLinden, usdEquivalent, nf)
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    L2TonalButton(onClick = onSend, modifier = Modifier.weight(1f)) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Send")
                    }
                    L2TonalButton(onClick = onRequest, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.ArrowDownward, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Request")
                    }
                    L2TonalButton(onClick = onBuy, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Buy L\$")
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
                                    "+L\$ ${nf.format(weeklyIn)}",
                                    color = tokens.success,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = FontFamily.Monospace,
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Outflow", color = tokens.onSurfaceDim, style = MaterialTheme.typography.labelSmall)
                                Text(
                                    "−L\$ ${nf.format(weeklyOut)}",
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
                                .clip(RoundedCornerShape(tokens.radii.md))
                                .background(
                                    if (tx.isIncome) tokens.success.copy(alpha = 0.18f)
                                    else MaterialTheme.colorScheme.surfaceVariant,
                                )
                                .padding(8.dp),
                        ) {
                            Icon(
                                if (tx.isIncome) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                contentDescription = null,
                                tint = if (tx.isIncome) tokens.success else tokens.onSurfaceDim,
                            )
                        }
                    },
                    trailing = {
                        Text(
                            text = "${if (tx.isIncome) "+" else "−"}L\$ ${nf.format(tx.amountLinden)}",
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
                "Linden balance",
                style = MaterialTheme.typography.labelMedium,
                color = cs.onPrimary.copy(alpha = 0.8f),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "L\$ ${nf.format(balanceLinden)}",
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = cs.onPrimary,
            )
            Text(
                "≈ \$${"%,.2f".format(usdEquivalent)} USD",
                style = MaterialTheme.typography.bodyMedium,
                color = cs.onPrimary.copy(alpha = 0.7f),
            )
        }
    }
}

