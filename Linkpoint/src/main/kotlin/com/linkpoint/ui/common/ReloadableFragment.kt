package com.linkpoint.ui.common

import android.content.Intent
import android.os.Bundle

interface ReloadableFragment {
     fun setFragmentArgs(intent: Intent, bundle: Bundle)
}
