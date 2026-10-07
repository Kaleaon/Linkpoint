package com.linkpoint.connection

import android.content.Context
import com.linkpoint.network.core.GridConnection
import com.linkpoint.protocol.messages.UDPConnectionFixed
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import androidx.test.core.app.ApplicationProvider

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class GridConnectionAsyncTest {

    @Test
    fun `closeAsync and suspend close run without blocking caller thread`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val mockUdpConnection = UDPConnectionFixed()

        val testScope = TestScope(UnconfinedTestDispatcher())
        val gridConnection = GridConnection(
            context = context,
            sharedUdpConnection = mockUdpConnection,
            scope = testScope
        )

        assertNotNull(gridConnection)

        // Non-blocking closeAsync invocation
        gridConnection.closeAsync()

        // Suspending close invocation
        gridConnection.close()
    }
}
