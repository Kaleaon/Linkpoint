// swift-tools-version: 5.7
import PackageDescription

let package = Package(
    name: "LLSDKit",
    platforms: [
        .iOS(.v16),
        .macOS(.v13)
    ],
    products: [
        .library(name: "LLSD", targets: ["LLSD"]),
        .library(name: "LLSDAssets", targets: ["LLSDAssets"]),
        .library(name: "LLSDXMLRPC", targets: ["LLSDXMLRPC"]),
        .library(name: "LLSDNetwork", targets: ["LLSDNetwork"]),
        .library(name: "Ktheme", targets: ["Ktheme"]),
        .library(name: "LinkpointiOS", targets: ["LinkpointiOS"])
    ],
    dependencies: [
        .package(url: "https://github.com/google/flatbuffers.git", from: "23.5.26")
    ],
    targets: [
        .target(
            name: "LLSD",
            dependencies: [
                .product(name: "FlatBuffers", package: "flatbuffers")
            ]
        ),
        .target(
            name: "LLSDAssets",
            dependencies: ["LLSD"]
        ),
        .target(
            name: "LLSDXMLRPC",
            dependencies: ["LLSD"]
        ),
        .target(
            name: "LLSDNetwork",
            dependencies: ["LLSD"]
        ),
        .target(
            name: "Ktheme",
            dependencies: []
        ),
        .target(
            name: "LinkpointiOS",
            dependencies: ["LLSD", "LLSDAssets", "LLSDXMLRPC", "LLSDNetwork", "Ktheme"]
        ),
        .testTarget(
            name: "LLSDTests",
            dependencies: ["LLSD"]
        ),
        .testTarget(
            name: "LLSDAssetsTests",
            dependencies: ["LLSDAssets"]
        ),
        .testTarget(
            name: "XMLRPCTests",
            dependencies: ["LLSDXMLRPC"]
        ),
        .testTarget(
            name: "NetworkTests",
            dependencies: ["LLSDNetwork"]
        ),
        .testTarget(
            name: "KthemeTests",
            dependencies: ["Ktheme"]
        ),
        .testTarget(
            name: "LinkpointiOSTests",
            dependencies: ["LinkpointiOS", "Ktheme"]
        )
    ]
)
