# UC-9: Create Group Session

## General Info

| Field | Value |
|-------|-------|
| ID | UC-9 |
| Actor | Registered User (Group Leader) |
| Precondition | User logged in, has at least 1 friend |
| Postcondition | Group session created, invite sent to members |

## Main Flow

| Step | Actor | Action |
|------|-------|--------|
| 1 | User | Click Group Session > Create New Group |
| 2 | System | Show form: name, duration, select members |
| 3 | User | Fill info and select friends |
| 4 | User | Click Create Group |
| 5 | System | Validate at least 1 friend invited |
| 6 | System | Create Group Session on server |
| 7 | System | Generate invite code expires in 30 mins |
| 8 | System | Send push notification to invited friends |
| 9 | System | Show Waiting Room to Leader |
| 10 | User | Wait for members to join |
| 11 | User | Click Start when ready |
| 12 | System | Start synchronized countdown for all members |
| 13 | System | Session ends, calculate points, show Group Result |

## Alternative Flows

Alt 1 - Not enough members (step 11):
- Ask: Only X/Y joined. Start anyway?
- Yes: start now. No: keep waiting

Alt 2 - Member exits during session (step 12):
- Notify group: [Name] has left
- Member loses points, tree dies

Alt 3 - Leader exits (step 12):
- Transfer Leader role to next member
- Session continues normally

Alt 4 - Network error (step 6):
- Show: Failed to create group
- User can retry

Alt 5 - User cancel (step 4):
- Group not created, return to previous screen

## Business Rules
- Max 10 members per group
- All members use same duration
- Invite code expires after 30 minutes
- Leader can kick member before start