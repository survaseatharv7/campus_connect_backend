package com.campusnexus.service;

import com.campusnexus.dto.*;

import java.util.List;
import java.util.UUID;

public interface ClubService {
    ClubResponse createClub(ClubCreateRequest request, UUID studentId);
    List<ClubResponse> getClubsByCollege(UUID collegeId);
    ClubResponse approveByHOD(UUID clubId, UUID hodId);
    ClubResponse rejectByHOD(UUID clubId, UUID hodId);
    ClubResponse approveByPrincipal(UUID clubId, UUID principalId);
    ClubResponse rejectByPrincipal(UUID clubId, UUID principalId);
    List<ClubResponse> getClubRequestsForHOD(UUID departmentId, String status);
    List<ClubResponse> getClubRequestsForPrincipal(UUID collegeId, String status);
    ClubResponse joinClub(UUID clubId, UUID studentId);
}
