import SwiftUI
import MetalKit

/// Lightweight Metal view that mirrors the data shared in the reference
/// conversation. The renderer is intentionally simplified so it compiles in
/// the sandbox environment while still exposing the same API surface.
struct MetalWorldView: UIViewRepresentable {
    func makeCoordinator() -> Renderer {
        Renderer()
    }

    func makeUIView(context: Context) -> MTKView {
        let view = MTKView()
        let device = MTLCreateSystemDefaultDevice()
        view.device = device
        view.clearColor = MTLClearColor(red: 0.32, green: 0.5, blue: 0.82, alpha: 1)
        view.delegate = context.coordinator
        if let device = device {
            context.coordinator.setup(device: device)
        }
        context.coordinator.start()
        return view
    }

    func updateUIView(_ uiView: MTKView, context: Context) {}

    final class Renderer: NSObject, MTKViewDelegate {
        private var startTime = CACurrentMediaTime()
        private var commandQueue: MTLCommandQueue?

        func setup(device: MTLDevice) {
            commandQueue = device.makeCommandQueue()
        }

        func start() {
            startTime = CACurrentMediaTime()
        }

        func mtkView(_ view: MTKView, drawableSizeWillChange size: CGSize) { }

        func draw(in view: MTKView) {
            guard let drawable = view.currentDrawable,
                  let descriptor = view.currentRenderPassDescriptor,
                  let commandQueue = commandQueue,
                  let commandBuffer = commandQueue.makeCommandBuffer(),
                  let encoder = commandBuffer.makeRenderCommandEncoder(descriptor: descriptor) else {
                return
            }

            // Simple animated background gradient
            let elapsed = Float(CACurrentMediaTime() - startTime)
            let intensity = (sin(elapsed) + 1) / 2
            view.clearColor = MTLClearColor(red: 0.25 + 0.3 * Double(intensity),
                                            green: 0.45,
                                            blue: 0.75,
                                            alpha: 1.0)

            encoder.endEncoding()
            commandBuffer.present(drawable)
            commandBuffer.commit()
        }
    }
}
