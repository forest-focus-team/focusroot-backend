@Test
    @DisplayName("Cộng xu chính xác và chuyển trạng thái sang COMPLETED khi hoàn thành đủ thời gian mục tiêu")
    void endSession_ShouldEarnCoins_WhenSessionCompletedSuccessfully() {
        // Arrange
        Long userId = 1L;
        EndSessionRequest endRequest = new EndSessionRequest();
        endRequest.setQuitEarly(false); // Không thoát sớm

        Session activeSession = new Session();
        activeSession.setId(100L);
        activeSession.setUserId(userId);
        activeSession.setTargetDuration(25); // Mục tiêu 25 phút
        // Giả lập thời gian bắt đầu là 26 phút trước để khi kết thúc đạt chỉ tiêu
        activeSession.setStartTime(LocalDateTime.now().minusMinutes(26)); 
        activeSession.setStatus(SessionStatus.ACTIVE);

        when(sessionRepository.findByUserIdAndStatus(userId, SessionStatus.ACTIVE))
                .thenReturn(java.util.Optional.of(activeSession));
        when(sessionRepository.save(any(Session.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        SessionResponse response = sessionService.endSession(userId, endRequest);

        // Assert
        org.junit.jupiter.api.Assertions.assertEquals("COMPLETED", response.getStatus());
        // Thời gian thực tế thực thi = 26 phút -> 26 * 2 = 52 xu
        org.junit.jupiter.api.Assertions.assertTrue(response.getCoinEarned() >= 50); 
        verify(sessionRepository, times(1)).save(activeSession);
    }
