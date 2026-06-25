package com.campusnexus.service.impl;

import com.campusnexus.dto.*;
import com.campusnexus.entity.*;
import com.campusnexus.exception.ResourceNotFoundException;
import com.campusnexus.repository.*;
import com.campusnexus.service.BroadcastService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BroadcastServiceImpl implements BroadcastService {

    private final BroadcastRepository broadcastRepository;
    private final UserRepository userRepository;
    private final FCMService fcmService;

    public BroadcastServiceImpl(BroadcastRepository broadcastRepository,
                                 UserRepository userRepository,
                                 FCMService fcmService) {
        this.broadcastRepository = broadcastRepository;
        this.userRepository = userRepository;
        this.fcmService = fcmService;
    }

    @Override
    @Transactional
    public BroadcastResponse createBroadcast(BroadcastRequest request, UUID sentById) {
        User sender = userRepository.findById(sentById)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Broadcast broadcast = Broadcast.builder()
                .title(request.getTitle())
                .message(request.getMessage())
                .attachmentUrl(request.getAttachmentUrl())
                .level(request.getLevel())
                .sentBy(sender)
                .build();

        String topic;

        switch (request.getLevel()) {
            case CAMPUS:
                topic = "campus-all";
                break;
            case COLLEGE:
                if (sender.getCollege() != null) {
                    broadcast.setCollege(sender.getCollege());
                    topic = "college-" + sender.getCollege().getId();
                } else {
                    topic = "campus-all";
                }
                break;
            case DEPARTMENT:
                if (sender.getDepartment() != null) {
                    broadcast.setDepartment(sender.getDepartment());
                    broadcast.setCollege(sender.getCollege());
                    topic = "dept-" + sender.getDepartment().getId();
                } else {
                    topic = "campus-all";
                }
                break;
            default:
                topic = "campus-all";
        }

        broadcast = broadcastRepository.save(broadcast);

        // Send FCM notification
        fcmService.sendToTopic(topic, request.getTitle(), request.getMessage(), null);

        return mapToResponse(broadcast);
    }

    @Override
    public List<BroadcastResponse> getBroadcastsBySender(UUID sentById) {
        return broadcastRepository.findBySentById(sentById).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BroadcastResponse> getRelevantBroadcasts(UUID collegeId, UUID departmentId) {
        return broadcastRepository.findRelevantBroadcasts(collegeId, departmentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private BroadcastResponse mapToResponse(Broadcast broadcast) {
        User sender = broadcast.getSentBy();
        String senderName = sender.getName();
        String senderRole = sender.getRole().name();

        if (sender.getRole() == com.campusnexus.enums.Role.CAMPUS_ADMIN) {
            senderName = "Campus";
            senderRole = "Admin";
        }

        return BroadcastResponse.builder()
                .id(broadcast.getId())
                .title(broadcast.getTitle())
                .message(broadcast.getMessage())
                .attachmentUrl(broadcast.getAttachmentUrl())
                .level(broadcast.getLevel())
                .levelLabel(broadcast.getLevel().name())
                .senderName(senderName)
                .senderRole(senderRole)
                .sentAt(broadcast.getSentAt())
                .build();
    }
}
