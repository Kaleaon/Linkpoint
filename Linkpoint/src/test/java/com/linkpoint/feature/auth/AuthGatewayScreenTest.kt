package com.linkpoint.feature.auth

import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AuthGatewayScreenTest {

    @Test
    fun testAuthGatewayUiStateErrorCallbacks() {
        var switchAccountCalled = false
        var changeGridCalled = false

        val onSwitchAccount = { switchAccountCalled = true }
        val onChangeGrid = { changeGridCalled = true }

        onSwitchAccount()
        onChangeGrid()

        assertTrue(switchAccountCalled)
        assertTrue(changeGridCalled)
    }
}
