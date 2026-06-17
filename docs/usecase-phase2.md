# Use Case Diagram - FocusRoot Phase 2

## Actors

### Registered User
- Logged in, has session history from Phase 1
- Can use all 6 UC in Phase 2

### AI/ML Engine (System Actor)
- Analyzes user behavior and session history
- Auto-triggers warning when Risk Score is high
- Generates personalized suggestions

## Use Cases Phase 2

| UC ID | Name | Group | Description |
|-------|------|-------|-------------|
| UC-6 | View Drop-out Risk Score | Drop-out Prediction | View AI-calculated drop-out risk |
| UC-7 | Receive Drop-out Warning | Drop-out Prediction | Receive warning when score >= 70 |
| UC-8 | View Personalized Suggestions | Drop-out Prediction | View AI suggestions |
| UC-9 | Create Group Session | Group Peer-pressure | Create group focus session |
| UC-10 | Join Group Session | Group Peer-pressure | Join via invite code |
| UC-11 | View Group Leaderboard | Group Peer-pressure | View group ranking |

## Relationships

- UC-6 extends UC-7 if score >= 70
- UC-6 extends UC-8
- UC-9 includes UC-10
- UC-10 includes UC-11

## Phase 2 Scope

IN SCOPE:
- AI-based drop-out prediction from session history
- Push notification warning
- Personalized suggestions from AI
- Group session (create, join, group leaderboard)

OUT OF SCOPE:
- Video/audio call in group session
- Advanced ML model retraining
- Third-party integrations
- Achievements and badges system