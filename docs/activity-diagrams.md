# Activity Diagrams - FocusRoot Phase 2

## 1. Focus Session Lifecycle

### States

| State | Description |
|-------|-------------|
| IDLE | User at home screen |
| SETUP | Selecting duration and apps to block |
| FOCUSING | Timer running, apps blocked |
| SUCCESS | Timer done, points awarded |
| FAILED | User exited early, 0 points |
| RESULT | Show final score and tree |

### Flow

1. User selects duration and apps to block
2. System validates input
3. If invalid: show error, return to step 1
4. If valid: start timer, block apps
5. While FOCUSING:
   - User tries to exit: show confirm dialog
   - Confirm Yes: FAILED, tree dies, 0 points
   - Confirm No: continue focusing
   - Timer = 0: SUCCESS
6. SUCCESS: calculate points, plant tree, update stats
7. FAILED: record failed session, show dead tree
8. Show Result screen
9. User returns home

### Tree Planting Logic

| Duration | Tree | Points |
|----------|------|--------|
| 15 mins | Seedling | 15 pts |
| 30 mins | Sprout | 30 pts |
| 45 mins | Tree | 45 pts |
| 60 mins | Big Tree | 60 pts |
| Failed | Dead Tree | 0 pts |

### State Transitions

| From | Condition | To |
|------|-----------|-----|
| IDLE | User click Start | SETUP |
| SETUP | Input valid | FOCUSING |
| SETUP | Input invalid | SETUP |
| FOCUSING | Timer = 0 | SUCCESS |
| FOCUSING | Exit + Yes | FAILED |
| FOCUSING | Exit + No | FOCUSING |
| SUCCESS | auto | RESULT |
| FAILED | auto | RESULT |
| RESULT | Go home | IDLE |

---

## 2. Group Focus Session Lifecycle

### States

| State | Description |
|-------|-------------|
| WAITING | Leader created, waiting for members |
| READY | Enough members, Leader can start |
| FOCUSING | All focusing, timer running |
| COMPLETED | Timer done, calculating points |
| RESULT | Showing group results |

### Flow

1. Leader creates group (name, duration, invite friends)
2. System generates invite code, valid 30 mins
3. Push notification sent to members
4. Members join via invite code
5. Waiting Room shown to Leader
6. Leader clicks Start:
   - All joined: start immediately
   - Some missing: ask Start anyway? Yes/No
7. Synchronized countdown starts
8. During session:
   - Member exits: notify group, member loses points
   - Leader exits: transfer role, session continues
9. Timer = 0: session ends
10. Calculate points for each member
11. Show Group Result with mini leaderboard

### Group Business Rules
- Max 10 members per group
- All members use same duration
- Member exits: 0 points, dead tree
- Leader exits: role transferred automatically
- Invite code expires after 30 minutes