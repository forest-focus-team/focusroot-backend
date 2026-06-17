# UC-7: Receive Drop-out Warning

## General Info

| Field | Value |
|-------|-------|
| ID | UC-7 |
| Actor | Registered User, AI/ML Engine |
| Precondition | Risk Score >= 70 from UC-6 |
| Postcondition | User receives warning and knows next action |

## Main Flow

| Step | Actor | Action |
|------|-------|--------|
| 1 | AI Engine | Detect Risk Score exceeds 70 |
| 2 | System | Create warning notification |
| 3 | System | Send push notification to device |
| 4 | User | Click on notification |
| 5 | System | Show Warning screen with reason and action plan |
| 6 | User | Choose: Start Session / View Suggestions / Dismiss |
| 7 | System | Log user response |

## Alternative Flows

Alt 1 - User dismiss (step 6):
- Log dismissed
- Resend after 24h if score still >= 70

Alt 2 - Push notification disabled:
- Show fixed red banner inside app
- Cannot dismiss until score < 70

Alt 3 - Score drops after warning:
- Auto-dismiss warning
- Show: Great job! Your focus is improving

Alt 4 - Network error:
- Cache warning locally
- Show when reconnected

## Business Rules
- Max 1 warning per 24h
- Include specific reason: You have not completed a session in 3 days
- Short action: Try a 15-min session today