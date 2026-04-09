package com.campusnexus.service;

import com.campusnexus.dto.*;

import java.util.List;
import java.util.UUID;

public interface BroadcastService {
    BroadcastResponse createBroadcast(BroadcastRequest request, UUID sentById);
    List<BroadcastResponse> getBroadcastsBySender(UUID sentById);
    List<BroadcastResponse> getRelevantBroadcasts(UUID collegeId, UUID departmentId);
}
