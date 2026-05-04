# Lint Temporary Exceptions Register

This project runs Android Lint in strict mode (`abortOnError=true`, `checkReleaseBuilds=true`) and publishes lint reports in CI for pull requests and release branches.

## Policy
- Temporary exceptions are allowed only for unblockers with a named owner and fixed expiry date.
- Expired exceptions must be removed or renewed (with rationale) in the same pull request.
- Any baseline additions must reference a tracking issue and follow this register format.

## Active Exceptions

| ID | Scope | Reason | Owner | Expires On (UTC) | Tracking |
| --- | --- | --- | --- | --- | --- |
| _None_ | - | - | - | - | - |

## How to update
1. Add/adjust the lint suppression/baseline entry in code.
2. Add a row above with owner + explicit expiry date (YYYY-MM-DD).
3. Link the tracking issue and cleanup plan.
