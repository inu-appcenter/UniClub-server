package com.uniclub.domain.main.service;

import com.uniclub.domain.club.repository.ClubRepository;
import com.uniclub.domain.main.dto.MainPageClubResponseDto;
import com.uniclub.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MainService {

    private final ClubRepository clubRepository;


    @Transactional(readOnly = true)
    public List<MainPageClubResponseDto> getMainPageClubs(UserDetailsImpl userDetails){
        return clubRepository.getMainPageClubs(PageRequest.of(0,6));
    }
}
