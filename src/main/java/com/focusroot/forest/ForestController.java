package com.focusroot.forest;

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

@Tag(name = "Forest", description = "User forest and tree species endpoints")
@RestController
@RequestMapping("/forest")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ForestController {

    private final ForestService forestService;

    @Operation(summary = "Get current user's forest (all planted trees)")
    @GetMapping
    public ResponseEntity<ApiResponse<List<MyForest>>> getForest(
            @AuthenticationPrincipal UserDetails principal) {
        List<MyForest> forest = forestService.getForest(principal.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(forest));
    }

    @Operation(summary = "Get all available tree species")
    @GetMapping("/species")
    public ResponseEntity<ApiResponse<List<TreeSpecies>>> getSpecies() {
        List<TreeSpecies> species = forestService.getAllSpecies();
        return ResponseEntity.ok(ApiResponse.ok(species));
    }
}
