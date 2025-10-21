package com.linkpoint.ui.common

import android.content.Intent
import android.os.Bundle

interface ReloadableFragment {
    Unit setFragmentArgs(Intent intent, Bundle bundle)
}
