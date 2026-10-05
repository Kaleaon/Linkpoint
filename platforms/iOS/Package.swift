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
            name: "LLSDXMLRPC",
            dependencies: ["LLSD"]
        ),
        .target(
            name: "LLSDNetwork",
            dependencies: ["LLSD"]
        ),
        .target(
            name: "LinkpointiOS",
            dependencies: ["LLSD", "LLSDXMLRPC", "LLSDNetwork"]
        ),
        .testTarget(
            name: "LLSDTests",
            dependencies: ["LLSD"]
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
