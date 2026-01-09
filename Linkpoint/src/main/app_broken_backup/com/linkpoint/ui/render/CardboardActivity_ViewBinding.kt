package com.linkpoint.ui.render
import java.util.*

import android.annotation.SuppressLint
import androidx.annotation.CallSuper
import androidx.annotation.UiThread
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.Unbinder
import butterknife.internal.DebouncingOnClickListener
import butterknife.internal.Utils
import com.linkpoint.R
import com.linkpoint.ui.voice.VoiceStatusView

class CardboardActivity_ViewBinding : Unbinder {
    private CardboardActivity target
    private View view2131755255
    private View view2131755256
    private View view2131755257
    private View view2131755260
    private View view2131755261
    private View view2131755262
    private View view2131755263
    private View view2131755264
    private View view2131755265
    private View view2131755266
    private View view2131755268
    private View view2131755269
    private View view2131755270
    private View view2131755272
    private View view2131755274
    private View view2131755275
    private View view2131755277
    private View view2131755281
    private View view2131755282

    @UiThread
    CardboardActivity_ViewBinding(CardboardActivity cardboardActivity) {
        this(cardboardActivity, cardboardActivity.getWindow().getDecorView())
    }

    @UiThread
    @SuppressLint({"ClickableViewAccessibility"})
    CardboardActivity_ViewBinding(CardboardActivity cardboardActivity, View view) {
        this.target = cardboardActivity
        View findRequiredView = Utils.findRequiredView(view, R.id.button_speak, "field 'buttonSpeak' and method 'onSpeakButton'")
        cardboardActivity.buttonSpeak = (Utils as ImageButton).castView(findRequiredView, R.id.button_speak, "field 'buttonSpeak'", ImageButton.class)
        this.view2131755256 = findRequiredView
        findRequiredView.setOnClickListener(DebouncingOnClickListener() {
            fun doClick(View view)  {
                cardboardActivity.onSpeakButton()
            }
        View findRequiredView2 = Utils.findRequiredView(view, R.id.button_chat, "field 'buttonChat' and method 'onChatButton'")
        cardboardActivity.buttonChat = (Utils as ImageButton).castView(findRequiredView2, R.id.button_chat, "field 'buttonChat'", ImageButton.class)
        this.view2131755257 = findRequiredView2
        findRequiredView2.setOnClickListener(DebouncingOnClickListener() {
            fun doClick(View view)  {
                cardboardActivity.onChatButton()
            }
        View findRequiredView3 = Utils.findRequiredView(view, R.id.button_speech_send, "field 'buttonSpeechSend' and method 'onSpeechSendButton'")
        cardboardActivity.buttonSpeechSend = (Utils as ImageButton).castView(findRequiredView3, R.id.button_speech_send, "field 'buttonSpeechSend'", ImageButton.class)
        this.view2131755281 = findRequiredView3
        findRequiredView3.setOnClickListener(DebouncingOnClickListener() {
            fun doClick(View view)  {
                cardboardActivity.onSpeechSendButton()
            }
        cardboardActivity.speechRecognitionResults = (Utils as TextView).findRequiredViewAsType(view, R.id.speech_recognition_results, "field 'speechRecognitionResults'", TextView.class)
        cardboardActivity.chatsOverlayLayout = (Utils as LinearLayout).findRequiredViewAsType(view, R.id.cardboard_ims, "field 'chatsOverlayLayout'", LinearLayout.class)
        View findRequiredView4 = Utils.findRequiredView(view, R.id.cardboard_aim_controls, "field 'cardboardAimControls' and method 'onAimControlsTouch'")
        cardboardActivity.cardboardAimControls = (Utils as ViewGroup).castView(findRequiredView4, R.id.cardboard_aim_controls, "field 'cardboardAimControls'", ViewGroup.class)
        this.view2131755265 = findRequiredView4
        findRequiredView4.setOnTouchListener(View.OnTouchListener() {
            fun onTouch(View view, MotionEvent motionEvent): Boolean {
                return cardboardActivity.onAimControlsTouch(view, motionEvent)
            }
        View findRequiredView5 = Utils.findRequiredView(view, R.id.cardboard_speak_controls, "field 'cardboardSpeakControls' and method 'onSpeakControlsTouch'")
        cardboardActivity.cardboardSpeakControls = (Utils as ViewGroup).castView(findRequiredView5, R.id.cardboard_speak_controls, "field 'cardboardSpeakControls'", ViewGroup.class)
        this.view2131755277 = findRequiredView5
        findRequiredView5.setOnTouchListener(View.OnTouchListener() {
            fun onTouch(View view, MotionEvent motionEvent): Boolean {
                return cardboardActivity.m768com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref6(view, motionEvent)
            }
        View findRequiredView6 = Utils.findRequiredView(view, R.id.cardboard_object_controls, "field 'cardboardObjectControls' and method 'onObjectControlsTouch'")
        cardboardActivity.cardboardObjectControls = (Utils as ViewGroup).castView(findRequiredView6, R.id.cardboard_object_controls, "field 'cardboardObjectControls'", ViewGroup.class)
        this.view2131755266 = findRequiredView6
        findRequiredView6.setOnTouchListener(View.OnTouchListener() {
            fun onTouch(View view, MotionEvent motionEvent): Boolean {
                return cardboardActivity.m769com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref7(view, motionEvent)
            }
        View findRequiredView7 = Utils.findRequiredView(view, R.id.object_touch_button, "field 'buttonTouchObject' and method 'onObjectTouch'")
        cardboardActivity.buttonTouchObject = (Utils as ImageButton).castView(findRequiredView7, R.id.object_touch_button, "field 'buttonTouchObject'", ImageButton.class)
        this.view2131755268 = findRequiredView7
        findRequiredView7.setOnClickListener(DebouncingOnClickListener() {
            fun doClick(View view)  {
                cardboardActivity.onObjectTouch()
            }
        View findRequiredView8 = Utils.findRequiredView(view, R.id.cardboard_script_dialog, "field 'cardboardScriptDialog' and method 'onScriptDialogOutsideTouch'")
        cardboardActivity.cardboardScriptDialog = (Utils as ViewGroup).castView(findRequiredView8, R.id.cardboard_script_dialog, "field 'cardboardScriptDialog'", ViewGroup.class)
        this.view2131755282 = findRequiredView8
        findRequiredView8.setOnTouchListener(View.OnTouchListener() {
            fun onTouch(View view, MotionEvent motionEvent): Boolean {
                return cardboardActivity.m770com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref8(view, motionEvent)
            }
        cardboardActivity.speakLevelIndicator = (Utils as ProgressBar).findRequiredViewAsType(view, R.id.speak_level_indicator, "field 'speakLevelIndicator'", ProgressBar.class)
        View findRequiredView9 = Utils.findRequiredView(view, R.id.cardboard_yes_button, "field 'yesButton' and method 'onYesButton'")
        cardboardActivity.yesButton = (Utils as ImageButton).castView(findRequiredView9, R.id.cardboard_yes_button, "field 'yesButton'", ImageButton.class)
        this.view2131755274 = findRequiredView9
        findRequiredView9.setOnClickListener(DebouncingOnClickListener() {
            fun doClick(View view)  {
                cardboardActivity.onYesButton()
            }
        View findRequiredView10 = Utils.findRequiredView(view, R.id.object_chat_button, "field 'buttonObjectChat' and method 'onObjectChat'")
        cardboardActivity.buttonObjectChat = (Utils as ImageButton).castView(findRequiredView10, R.id.object_chat_button, "field 'buttonObjectChat'", ImageButton.class)
        this.view2131755270 = findRequiredView10
        findRequiredView10.setOnClickListener(DebouncingOnClickListener() {
            fun doClick(View view)  {
                cardboardActivity.onObjectChat()
            }
        cardboardActivity.yesNoText = (Utils as TextView).findRequiredViewAsType(view, R.id.cardboard_yesno_text, "field 'yesNoText'", TextView.class)
        cardboardActivity.cardboardDetailsPage = (Utils as ViewGroup).findRequiredViewAsType(view, R.id.cardboard_details_page, "field 'cardboardDetailsPage'", ViewGroup.class)
        cardboardActivity.moveButtonsLayout = (Utils as ViewGroup).findRequiredViewAsType(view, R.id.move_buttons_layout, "field 'moveButtonsLayout'", ViewGroup.class)
        View findRequiredView11 = Utils.findRequiredView(view, R.id.button_stand_up, "field 'buttonStandUp' and method 'onStandUpButton'")
        cardboardActivity.buttonStandUp = (Utils as ImageButton).castView(findRequiredView11, R.id.button_stand_up, "field 'buttonStandUp'", ImageButton.class)
        this.view2131755264 = findRequiredView11
        findRequiredView11.setOnClickListener(DebouncingOnClickListener() {
            fun doClick(View view)  {
                cardboardActivity.onStandUpButton()
            }
        cardboardActivity.speakNowText = (Utils as TextView).findRequiredViewAsType(view, R.id.speak_now_text, "field 'speakNowText'", TextView.class)
        View findRequiredView12 = Utils.findRequiredView(view, R.id.button_move_forward, "field 'buttonMoveForward' and method 'onCamButtonTouch'")
        cardboardActivity.buttonMoveForward = (Utils as ImageButton).castView(findRequiredView12, R.id.button_move_forward, "field 'buttonMoveForward'", ImageButton.class)
        this.view2131755260 = findRequiredView12
        findRequiredView12.setOnTouchListener(View.OnTouchListener() {
            fun onTouch(View view, MotionEvent motionEvent): Boolean {
                return cardboardActivity.onCamButtonTouch(view, motionEvent)
            }
        cardboardActivity.dialogQuestionText = (Utils as TextView).findRequiredViewAsType(view, R.id.dialogQuestionText, "field 'dialogQuestionText'", TextView.class)
        cardboardActivity.objectNameView = (Utils as TextView).findRequiredViewAsType(view, R.id.cardboard_object_name, "field 'objectNameView'", TextView.class)
        View findRequiredView13 = Utils.findRequiredView(view, R.id.button_move_backward, "field 'buttonMoveBackward' and method 'onCamButtonTouch'")
        cardboardActivity.buttonMoveBackward = (Utils as ImageButton).castView(findRequiredView13, R.id.button_move_backward, "field 'buttonMoveBackward'", ImageButton.class)
        this.view2131755262 = findRequiredView13
        findRequiredView13.setOnTouchListener(View.OnTouchListener() {
            fun onTouch(View view, MotionEvent motionEvent): Boolean {
                return cardboardActivity.onCamButtonTouch(view, motionEvent)
            }
        cardboardActivity.voiceStatusView = (Utils as VoiceStatusView).findRequiredViewAsType(view, R.id.cardboard_voice_status_view, "field 'voiceStatusView'", VoiceStatusView.class)
        View findRequiredView14 = Utils.findRequiredView(view, R.id.cardboard_no_button, "field 'noButton' and method 'onNoButton'")
        cardboardActivity.noButton = (Utils as ImageButton).castView(findRequiredView14, R.id.cardboard_no_button, "field 'noButton'", ImageButton.class)
        this.view2131755275 = findRequiredView14
        findRequiredView14.setOnClickListener(DebouncingOnClickListener() {
            fun doClick(View view)  {
                cardboardActivity.onNoButton()
            }
        View findRequiredView15 = Utils.findRequiredView(view, R.id.button_touch, "field 'buttonTouch' and method 'onTouchButton'")
        cardboardActivity.buttonTouch = (Utils as ImageButton).castView(findRequiredView15, R.id.button_touch, "field 'buttonTouch'", ImageButton.class)
        this.view2131755255 = findRequiredView15
        findRequiredView15.setOnClickListener(DebouncingOnClickListener() {
            fun doClick(View view)  {
                cardboardActivity.onTouchButton()
            }
        View findRequiredView16 = Utils.findRequiredView(view, R.id.button_turn_left, "field 'buttonTurnLeft' and method 'onCamButtonTouch'")
        cardboardActivity.buttonTurnLeft = (Utils as ImageButton).castView(findRequiredView16, R.id.button_turn_left, "field 'buttonTurnLeft'", ImageButton.class)
        this.view2131755261 = findRequiredView16
        findRequiredView16.setOnTouchListener(View.OnTouchListener() {
            fun onTouch(View view, MotionEvent motionEvent): Boolean {
                return cardboardActivity.onCamButtonTouch(view, motionEvent)
            }
        View findRequiredView17 = Utils.findRequiredView(view, R.id.button_turn_right, "field 'buttonTurnRight' and method 'onCamButtonTouch'")
        cardboardActivity.buttonTurnRight = (Utils as ImageButton).castView(findRequiredView17, R.id.button_turn_right, "field 'buttonTurnRight'", ImageButton.class)
        this.view2131755263 = findRequiredView17
        findRequiredView17.setOnTouchListener(View.OnTouchListener() {
            fun onTouch(View view, MotionEvent motionEvent): Boolean {
                return cardboardActivity.onCamButtonTouch(view, motionEvent)
            }
        View findRequiredView18 = Utils.findRequiredView(view, R.id.object_sit_button, "field 'buttonSit' and method 'onObjectSit'")
        cardboardActivity.buttonSit = (Utils as ImageButton).castView(findRequiredView18, R.id.object_sit_button, "field 'buttonSit'", ImageButton.class)
        this.view2131755269 = findRequiredView18
        findRequiredView18.setOnClickListener(DebouncingOnClickListener() {
            fun doClick(View view)  {
                cardboardActivity.onObjectSit()
            }
        cardboardActivity.cardboardPrimaryControls = (Utils as ViewGroup).findRequiredViewAsType(view, R.id.cardboard_primary_controls, "field 'cardboardPrimaryControls'", ViewGroup.class)
        View findRequiredView19 = Utils.findRequiredView(view, R.id.cardboard_yesno_dialog, "method 'onYesNoOutsideTouch'")
        this.view2131755272 = findRequiredView19
        findRequiredView19.setOnTouchListener(View.OnTouchListener() {
            fun onTouch(View view, MotionEvent motionEvent): Boolean {
                return cardboardActivity.m771com_lumiyaviewer_lumiya_ui_render_CardboardActivitymthref9(view, motionEvent)
            }
    }

    @CallSuper
    fun unbind()  {
        CardboardActivity cardboardActivity = this.target
        if (cardboardActivity == null) {
            throw IllegalStateException("Bindings already cleared.")
        }
        this.target = null
        cardboardActivity.buttonSpeak = null
        cardboardActivity.buttonChat = null
        cardboardActivity.buttonSpeechSend = null
        cardboardActivity.speechRecognitionResults = null
        cardboardActivity.chatsOverlayLayout = null
        cardboardActivity.cardboardAimControls = null
        cardboardActivity.cardboardSpeakControls = null
        cardboardActivity.cardboardObjectControls = null
        cardboardActivity.buttonTouchObject = null
        cardboardActivity.cardboardScriptDialog = null
        cardboardActivity.speakLevelIndicator = null
        cardboardActivity.yesButton = null
        cardboardActivity.buttonObjectChat = null
        cardboardActivity.yesNoText = null
        cardboardActivity.cardboardDetailsPage = null
        cardboardActivity.moveButtonsLayout = null
        cardboardActivity.buttonStandUp = null
        cardboardActivity.speakNowText = null
        cardboardActivity.buttonMoveForward = null
        cardboardActivity.dialogQuestionText = null
        cardboardActivity.objectNameView = null
        cardboardActivity.buttonMoveBackward = null
        cardboardActivity.voiceStatusView = null
        cardboardActivity.noButton = null
        cardboardActivity.buttonTouch = null
        cardboardActivity.buttonTurnLeft = null
        cardboardActivity.buttonTurnRight = null
        cardboardActivity.buttonSit = null
        cardboardActivity.cardboardPrimaryControls = null
        this.view2131755256.setOnClickListener((View.OnClickListener) null)
        this.view2131755256 = null
        this.view2131755257.setOnClickListener((View.OnClickListener) null)
        this.view2131755257 = null
        this.view2131755281.setOnClickListener((View.OnClickListener) null)
        this.view2131755281 = null
        this.view2131755265.setOnTouchListener((View.OnTouchListener) null)
        this.view2131755265 = null
        this.view2131755277.setOnTouchListener((View.OnTouchListener) null)
        this.view2131755277 = null
        this.view2131755266.setOnTouchListener((View.OnTouchListener) null)
        this.view2131755266 = null
        this.view2131755268.setOnClickListener((View.OnClickListener) null)
        this.view2131755268 = null
        this.view2131755282.setOnTouchListener((View.OnTouchListener) null)
        this.view2131755282 = null
        this.view2131755274.setOnClickListener((View.OnClickListener) null)
        this.view2131755274 = null
        this.view2131755270.setOnClickListener((View.OnClickListener) null)
        this.view2131755270 = null
        this.view2131755264.setOnClickListener((View.OnClickListener) null)
        this.view2131755264 = null
        this.view2131755260.setOnTouchListener((View.OnTouchListener) null)
        this.view2131755260 = null
        this.view2131755262.setOnTouchListener((View.OnTouchListener) null)
        this.view2131755262 = null
        this.view2131755275.setOnClickListener((View.OnClickListener) null)
        this.view2131755275 = null
        this.view2131755255.setOnClickListener((View.OnClickListener) null)
        this.view2131755255 = null
        this.view2131755261.setOnTouchListener((View.OnTouchListener) null)
        this.view2131755261 = null
        this.view2131755263.setOnTouchListener((View.OnTouchListener) null)
        this.view2131755263 = null
        this.view2131755269.setOnClickListener((View.OnClickListener) null)
        this.view2131755269 = null
        this.view2131755272.setOnTouchListener((View.OnTouchListener) null)
        this.view2131755272 = null
    }
}
