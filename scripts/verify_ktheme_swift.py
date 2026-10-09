#!/usr/bin/env python3
import os
import re
import subprocess
import sys

PROJECT_ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))


def test_cli_generate_swift():
    cmd = [
        sys.executable,
        os.path.join(PROJECT_ROOT, "tools", "token_gen", "cli.py"),
        "--target",
        "swift",
    ]
    result = subprocess.run(cmd, cwd=PROJECT_ROOT, capture_output=True, text=True)
    assert (
        result.returncode == 0
    ), f"cli.py --target swift failed:\n{result.stderr}\n{result.stdout}"

    swift_file = os.path.join(
        PROJECT_ROOT, "platforms", "iOS", "Sources", "Ktheme", "GeneratedTokens.swift"
    )
    assert os.path.exists(swift_file), f"Generated file not found: {swift_file}"

    with open(swift_file, "r", encoding="utf-8") as f:
        content = f.read()

    assert "import SwiftUI" in content
    assert "public enum GeneratedTokens" in content
    assert "public enum Color" in content
    assert "public static let online =" in content
    print("[PASS] Requirement 1: cli.py --target swift generates valid Swift tokens.")


def test_package_swift_includes_ktheme():
    package_file = os.path.join(PROJECT_ROOT, "platforms", "iOS", "Package.swift")
    with open(package_file, "r", encoding="utf-8") as f:
        content = f.read()

    assert (
        '.library(name: "Ktheme", targets: ["Ktheme"])' in content
    ), "Ktheme library missing in Package.swift"
    assert 'name: "Ktheme"' in content, "Ktheme target missing in Package.swift"
    assert '"Ktheme"' in content, "Ktheme dependency missing in LinkpointiOS target"
    print(
        "[PASS] Requirement 2: Package.swift includes Ktheme target and library module."
    )


def test_views_import_ktheme_no_hardcoded_colors():
    view_files = [
        os.path.join(PROJECT_ROOT, "platforms", "iOS", "Views", "LoginView.swift"),
        os.path.join(PROJECT_ROOT, "platforms", "iOS", "Views", "MainTabView.swift"),
        os.path.join(
            PROJECT_ROOT,
            "platforms",
            "iOS",
            "Sources",
            "LinkpointiOS",
            "Views",
            "LoginView.swift",
        ),
        os.path.join(
            PROJECT_ROOT,
            "platforms",
            "iOS",
            "Sources",
            "LinkpointiOS",
            "Views",
            "MainTabView.swift",
        ),
    ]

    forbidden_patterns = [
        r"\.foregroundColor\(\.blue\)",
        r"\.foregroundColor\(\.white\)",
        r"\.foregroundColor\(\.red\)",
        r"\.foregroundColor\(\.gray\)",
        r"\.foregroundColor\(\.purple\)",
        r"\.foregroundColor\(\.secondary\)",
        r"\.foregroundColor\(\.primary\)",
        r"Color\.blue",
        r"Color\.purple",
        r"Color\.gray",
        r"Color\.black",
        r"Color\.red",
        r"Color\(\.systemBackground\)",
    ]

    for vf in view_files:
        assert os.path.exists(vf), f"View file not found: {vf}"
        with open(vf, "r", encoding="utf-8") as f:
            content = f.read()

        assert "import Ktheme" in content, f"import Ktheme missing in {vf}"

        for pattern in forbidden_patterns:
            matches = re.findall(pattern, content)
            assert (
                len(matches) == 0
            ), f"Forbidden hardcoded color pattern '{pattern}' found in {vf}: {matches}"

    print(
        "[PASS] Requirement 3: LoginView and MainTabView import Ktheme with zero hardcoded colors."
    )


def test_main_tab_view_adaptive_navigation():
    main_tab_file = os.path.join(
        PROJECT_ROOT,
        "platforms",
        "iOS",
        "Sources",
        "LinkpointiOS",
        "Views",
        "MainTabView.swift",
    )
    with open(main_tab_file, "r", encoding="utf-8") as f:
        content = f.read()

    assert (
        "@Environment(\\.horizontalSizeClass)" in content
    ), "horizontalSizeClass environment property missing"
    assert (
        "NavigationSplitView" in content
    ), "NavigationSplitView layout missing for regular width displays"
    assert "TabView" in content, "TabView layout missing for compact width displays"
    assert (
        "horizontalSizeClass == .regular" in content
    ), "horizontalSizeClass condition missing"

    print(
        "[PASS] Requirements 4 & 5: MainTabView adapts between NavigationSplitView and TabView."
    )


def main():
    test_cli_generate_swift()
    test_package_swift_includes_ktheme()
    test_views_import_ktheme_no_hardcoded_colors()
    test_main_tab_view_adaptive_navigation()
    print("\nAll verification checks passed successfully!")


if __name__ == "__main__":
    main()
