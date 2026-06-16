# UC-6: View Drop-out Risk Score

## General Info

| Field | Value |
|-------|-------|
| ID | UC-6 |
| Actor | Registered User, AI/ML Engine |
| Precondition | User logged in, has 7+ days session history |
| Postcondition | Risk Score displayed to user |

## Main Flow

| Step | Actor | Action |
|------|-------|--------|
| 1 | User | Open Dashboard or Profile |
| 2 | System | Fetch last 7 days session history |
| 3 | AI Engine | Analyze frequency, duration, success rate, streak |
| 4 | AI Engine | Calculate Risk Score 0 to 100 |
| 5 | System | Display Risk Score: Low / Medium / High |
| 6 | System | Show 7-day trend chart |
| 7 | System | Compare with previous week |
| 8 | User | View detail metrics |

## Alternative Flows

Alt 1 - Not enough data (step 2):
- Show: Need 7 days of data to calculate
- Suggest user to create session today

Alt 2 - Score >= 70 (step 4):
- Auto-trigger UC-7
- Show red alert with button to UC-8

Alt 3 - Network error (step 2):
- Show: Failed to load. Please retry.
- Show cached score if available

Alt 4 - AI Engine error (step 3):
- Show cached score from last calculation
- Show banner: Score may not be up to date

## Business Rules
- Score recalculated every 24 hours
- Score >= 70: High Risk, auto-trigger UC-7
- Score 40-69: Medium Risk
- Score < 40: Low Risk, no warning