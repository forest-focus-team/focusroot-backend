package com.focusroot.prediction;

import com.focusroot.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Prediction", description = "Focus success prediction and activity logs")
@RestController
@RequestMapping("/predictions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class PredictionController {

    private final PredictionService predictionService;

    @Operation(summary = "Get activity logs for current user")
    @GetMapping("/activity")
    public ResponseEntity<ApiResponse<List<UserActivityLog>>> getActivityLogs(
            @AuthenticationPrincipal UserDetails principal) {
        List<UserActivityLog> logs = predictionService.getActivityLogs(principal.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(logs));
    }

    @Operation(summary = "Predict success probability for a planned session")
    @GetMapping("/success")
    public ResponseEntity<ApiResponse<PredictionService.PredictionResult>> predict(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(defaultValue = "25") int plannedMinutes) {
        PredictionService.PredictionResult result =
                predictionService.predictSuccess(principal.getUsername(), plannedMinutes);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}
