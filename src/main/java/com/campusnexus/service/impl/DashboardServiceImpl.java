package com.campusnexus.service.impl;

import com.campusnexus.dto.DashboardResponse;
import com.campusnexus.enums.Role;
import com.campusnexus.repository.*;
import com.campusnexus.service.DashboardService;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final CollegeRepository collegeRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventRegistrationRepository eventRegistrationRepository;
    private final ClubRepository clubRepository;

    public DashboardServiceImpl(CollegeRepository collegeRepository,
                                 UserRepository userRepository,
                                 EventRepository eventRepository,
                                 EventRegistrationRepository eventRegistrationRepository,
                                 ClubRepository clubRepository) {
        this.collegeRepository = collegeRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.eventRegistrationRepository = eventRegistrationRepository;
        this.clubRepository = clubRepository;
    }

    @Override
    public DashboardResponse getDashboardStats() {
        return DashboardResponse.builder()
                .totalColleges(collegeRepository.count())
                .totalUsers(userRepository.count())
                .totalEvents(eventRepository.count())
                .totalRegistrations(eventRegistrationRepository.count())
                .totalClubs(clubRepository.count())
                .activeStudents(userRepository.countByRoleAndIsActiveTrue(Role.STUDENT))
                .build();
    }
}
