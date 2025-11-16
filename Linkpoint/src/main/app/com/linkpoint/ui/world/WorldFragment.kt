package com.linkpoint.ui.world

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.linkpoint.R
import com.linkpoint.databinding.FragmentLinkpointWorldBinding
import com.linkpoint.graphics.LinkpointRenderer
import kotlinx.coroutines.launch

class WorldFragment : Fragment(), LinkpointRenderer.RendererListener {

    private var binding: FragmentLinkpointWorldBinding? = null
    private var renderer: LinkpointRenderer? = null

    private val viewModel: WorldViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentLinkpointWorldBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentBinding = binding ?: return
        renderer = LinkpointRenderer(requireContext(), fragmentBinding.renderSurface, this)
        fragmentBinding.resetCameraButton.setOnClickListener {
            renderer?.resetCamera()
        }
        observeFrameMetrics()
    }

    private fun observeFrameMetrics() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.frameMetrics.collect { metrics ->
                    binding?.fpsLabel?.text = getString(
                        R.string.linkpoint_world_fps,
                        metrics.fpsFormatted
                    )
                    binding?.frameTimeLabel?.text = getString(
                        R.string.linkpoint_world_frame_time,
                        metrics.frameTimeFormatted
                    )
                }
            }
        }
    }

    override fun onRendererReady() {
        binding?.worldStatus?.text = getString(R.string.linkpoint_world_ready)
    }

    override fun onFrame(frameTimeMillis: Float, fps: Float) {
        viewModel.updateMetrics(fps, frameTimeMillis)
    }

    override fun onRendererError(throwable: Throwable) {
        binding?.worldStatus?.text = throwable.localizedMessage
    }

    override fun onResume() {
        super.onResume()
        renderer?.start()
    }

    override fun onPause() {
        renderer?.stop()
        super.onPause()
    }

    override fun onDestroyView() {
        renderer?.destroy()
        renderer = null
        binding = null
        super.onDestroyView()
    }
}
