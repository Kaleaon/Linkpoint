// swift-tools-version: 5.7
import PackageDescription

let package = Package(
    name: "LLSDKit",
    platforms: [
        .iOS(.v15),
        .macOS(.v12)
    ],
    products: [
        .library(name: "LLSD", targets: ["LLSD"]),
        .library(name: "LLSDAssets", targets: ["LLSDAssets"]),
        .library(name: "LLSDXMLRPC", targets: ["LLSDXMLRPC"]),
        .library(name: "LLSDNetwork", targets: ["LLSDNetwork"]),
        .library(name: "LinkpointiOS", targets: ["LinkpointiOS"])
    ],
    dependencies: [],
    targets: [
        .target(
            name: "LLSD",
            dependencies: []
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
            name: "LinkpointiOS",
            dependencies: ["LLSD", "LLSDAssets", "LLSDXMLRPC", "LLSDNetwork"]
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
            name: "LinkpointiOSTests",
            dependencies: ["LinkpointiOS"]
        )
    ]
)
