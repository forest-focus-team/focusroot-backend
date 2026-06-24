# WebSocket Group Session Test - Week 5

## Muc tieu
Test thu cong kich ban multi-client WebSocket Group.

## Ket qua test

### TC-01: Tao group - PASS
### TC-02: 2 client join 1 group - PASS
### TC-03: WebSocket connect voi JWT - PASS
### TC-04: Broadcast session start/end - PASS
### TC-05: 1 client rot - PASS

## Bug da fix
LazyInitializationException trong GroupController.
Fix: Tao GroupResponse va GroupMemberResponse DTO.

## Ket luan
- 2 client chay on dinh realtime: PASS
- Khong crash khi 1 client rot: PASS
- JWT handshake hoat dong dung: PASS
