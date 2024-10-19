package com.elice.holo.member.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.elice.holo.member.domain.Member;
import com.elice.holo.member.domain.MemberDetails;
import com.elice.holo.member.dto.MemberLoginRequestDto;
import com.elice.holo.member.dto.MemberMapper;
import com.elice.holo.member.dto.MemberResponseDto;
import com.elice.holo.member.dto.MemberSignupRequestDto;
import com.elice.holo.member.dto.MemberUpdateRequestDto;
import com.elice.holo.member.repository.MemberRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberMapper memberMapper;

    @InjectMocks
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("회원가입 서비스 테스트")
    @Test
    void signupTest() {
        // Given
        MemberSignupRequestDto requestDto = new MemberSignupRequestDto();
        requestDto.setEmail("test@test.com");
        requestDto.setPassword("password123");
        requestDto.setName("유재석");
        requestDto.setTel("010-1234-5678");
        requestDto.setGender(true);
        requestDto.setAge(45);

        // 회원가입 후 반환할 Member 객체
        Member member = Member.builder()
            .email(requestDto.getEmail())
            .password(requestDto.getPassword())
            .name(requestDto.getName())
            .tel(requestDto.getTel())
            .gender(requestDto.getGender())
            .age(requestDto.getAge())
            .isDeleted(false)
            .build();

        // When

        when(memberMapper.toEntity(any(MemberSignupRequestDto.class))).thenReturn(member);


        when(memberRepository.save(any(Member.class))).thenReturn(member);


        Member result = memberService.signupAndReturnEntity(requestDto);

        // Then
        assertEquals(result.getEmail(), requestDto.getEmail());
        assertEquals(result.getName(), requestDto.getName());
        assertEquals(result.getTel(), requestDto.getTel());
        assertEquals(result.getGender(), requestDto.getGender());
        assertEquals(result.getAge(), requestDto.getAge());
    }

    @DisplayName("로그인 서비스 테스트")
    @Test
    void loginTest() {
        // Given
        MemberLoginRequestDto loginRequest = new MemberLoginRequestDto();
        loginRequest.setEmail("test@test.com");
        loginRequest.setPassword("password123");

        Member member = Member.builder()
            .email(loginRequest.getEmail())
            .password(loginRequest.getPassword())
            .name("유재석")
            .tel("010-1234-5678")
            .gender(true)
            .age(45)
            .isDeleted(false)
            .isAdmin(false)
            .build();

        MemberResponseDto responseDto = new MemberResponseDto(member); // MemberResponseDto 생성

        // When
        when(memberRepository.findByEmailAndIsDeletedFalse(loginRequest.getEmail()))
            .thenReturn(Optional.of(member));
        when(memberMapper.toDto(any(Member.class))).thenReturn(responseDto);

        MemberResponseDto result = memberService.login(loginRequest);

        // Then
        assertEquals(result.getEmail(), loginRequest.getEmail());
        assertEquals(result.getName(), "유재석");
    }

    @DisplayName("회원 로그인 실패 - 존재하지 않는 회원")
    @Test
    void loginFailTest() {
        // Given
        MemberLoginRequestDto loginRequest = new MemberLoginRequestDto();
        loginRequest.setEmail("nonexistent@test.com");
        loginRequest.setPassword("password123");

        // When
        when(memberRepository.findByEmailAndIsDeletedFalse(loginRequest.getEmail()))
            .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> memberService.login(loginRequest));
    }

    @DisplayName("회원 로그인 실패 - 삭제된 회원")
    @Test
    void loginDeletedMemberTest() {
        // Given
        MemberLoginRequestDto loginRequest = new MemberLoginRequestDto();
        loginRequest.setEmail("deleted@test.com");
        loginRequest.setPassword("password123");

        // When
        when(memberRepository.findByEmailAndIsDeletedFalse(loginRequest.getEmail()))
            .thenReturn(Optional.empty());

        // Then
        assertThrows(IllegalArgumentException.class, () -> memberService.login(loginRequest));
    }

    @DisplayName("회원 삭제(소프트 딜리트) 테스트")
    @Test
    void deleteMemberTest() {
        // Given
        Long memberId = 1L;
        Member member = Member.builder()
            .email("test@test.com")
            .isDeleted(false)
            .build();

        // When
        when(memberRepository.findByMemberIdAndIsDeletedFalse(memberId)).thenReturn(
            Optional.of(member));

        memberService.deleteMember(memberId);

        // Then
        assertEquals(true, member.getIsDeleted());
    }

    @DisplayName("회원 삭제 실패 - 이미 삭제된 회원")
    @Test
    void deleteAlreadyDeletedMemberTest() {
        // Given
        Long memberId = 1L;

        // When
        when(memberRepository.findByMemberIdAndIsDeletedFalse(memberId)).thenReturn(
            Optional.empty());

        //  Then
        assertThrows(IllegalArgumentException.class, () -> memberService.deleteMember(memberId));
        verify(memberRepository, never()).save(any(Member.class));
    }

    @DisplayName("회원 조회 실패 - 삭제된 회원")
    @Test
    void getMemberFailDeletedTest() {
        // Given
        Long memberId = 1L;

        // When
        when(memberRepository.findByMemberIdAndIsDeletedFalse(memberId)).thenReturn(
            Optional.empty());

        // Then
        assertThrows(IllegalArgumentException.class, () -> memberService.getMemberById(memberId));
    }

    @DisplayName("회원 정보 수정 테스트")
    @Test
    void updateMemberTest() {
        // Given
        Long memberId = 1L;

        MemberUpdateRequestDto updateRequest = new MemberUpdateRequestDto();
        updateRequest.setName("강호동");
        updateRequest.setTel("010-8765-4321");
        updateRequest.setGender(false);
        updateRequest.setAge(50);

        Member existingMember = Member.builder()
            .email("test@test.com")
            .password("password123")
            .name("유재석")
            .tel("010-1234-5678")
            .gender(true)
            .age(45)
            .isDeleted(false)
            .isAdmin(false)
            .build();

        Member updatedMember = Member.builder()
            .email("test@test.com")
            .password("password123")
            .name(updateRequest.getName()) // 수정된 이름
            .tel(updateRequest.getTel()) // 수정된 전화번호
            .gender(updateRequest.getGender()) // 수정된 성별
            .age(updateRequest.getAge()) // 수정된 나이
            .isDeleted(false)
            .isAdmin(false)
            .build();

        MemberResponseDto responseDto = new MemberResponseDto(updatedMember);

        // When
        when(memberRepository.findByMemberIdAndIsDeletedFalse(memberId))
            .thenReturn(Optional.of(existingMember)); // 기존 회원 정보 조회
        when(memberMapper.toDto(any(Member.class))).thenReturn(responseDto);

        MemberResponseDto result = memberService.updateMember(memberId, updateRequest);

        // Then
        assertEquals(result.getName(), updateRequest.getName());
        assertEquals(result.getTel(), updateRequest.getTel());
        assertEquals(result.getGender(), updateRequest.getGender());
        assertEquals(result.getAge(), updateRequest.getAge());

        verify(memberRepository).save(existingMember);
    }



    @DisplayName("모든 회원 조회 테스트")
    @Test
    void getAllMembersTest() {
        // Given
        Member member1 = Member.builder()
            .email("admin@test.com")
            .name("AdminUser")
            .tel("010-1234-5678")
            .gender(true)
            .age(30)
            .isDeleted(false)
            .isAdmin(true)
            .build();

        Member member2 = Member.builder()
            .email("user@test.com")
            .name("NormalUser")
            .tel("010-8765-4321")
            .gender(false)
            .age(25)
            .isDeleted(false)
            .isAdmin(false)
            .build();

        List<Member> members = List.of(member1, member2);

        List<MemberResponseDto> responseDtos = List.of(
            new MemberResponseDto(member1),
            new MemberResponseDto(member2)
        );


        Authentication mockAuthentication = mock(Authentication.class);
        MemberDetails mockMemberDetails = new MemberDetails(member1);
        when(mockAuthentication.getPrincipal()).thenReturn(mockMemberDetails);
        when(mockAuthentication.isAuthenticated()).thenReturn(true);


        SecurityContextHolder.getContext().setAuthentication(mockAuthentication);


        when(memberRepository.findAllByIsDeletedFalse()).thenReturn(members);
        when(memberMapper.toDtoList(any())).thenReturn(responseDtos);

        List<MemberResponseDto> result = memberService.getAllMembers();

        // Then
        assertEquals(2, result.size());
        assertEquals(result.get(0).getEmail(), "admin@test.com");
        assertEquals(result.get(1).getEmail(), "user@test.com");
    }


    @DisplayName("특정 회원 조회 테스트")
    @Test
    void getMemberByIdTest() {
        // Given
        Long memberId = 1L;

        Member member = Member.builder()
            .memberId(memberId) // memberId를 설정
            .email("test@test.com")
            .password("password123")
            .name("유재석")
            .tel("010-1234-5678")
            .gender(true)
            .age(45)
            .isDeleted(false)
            .isAdmin(false)
            .build();

        MemberResponseDto responseDto = new MemberResponseDto(member);


        Authentication mockAuthentication = mock(Authentication.class);
        MemberDetails mockMemberDetails = new MemberDetails(member); // 본인 정보 조회
        when(mockAuthentication.getPrincipal()).thenReturn(mockMemberDetails);
        when(mockAuthentication.isAuthenticated()).thenReturn(true);


        SecurityContextHolder.getContext().setAuthentication(mockAuthentication);

        // When
        when(memberRepository.findByMemberIdAndIsDeletedFalse(memberId)).thenReturn(Optional.of(member));
        when(memberMapper.toDto(member)).thenReturn(responseDto);

        MemberResponseDto result = memberService.getMemberById(memberId);

        // Then
        assertEquals(result.getEmail(), "test@test.com");
        assertEquals(result.getName(), "유재석");
        assertEquals(result.getTel(), "010-1234-5678");
        assertEquals(result.getGender(), true);
        assertEquals(result.getAge(), 45);
        verify(memberRepository, times(1)).findByMemberIdAndIsDeletedFalse(memberId);
        verify(memberMapper, times(1)).toDto(member);
    }

    @DisplayName("특정 회원 조회 실패 - 존재하지 않는 회원")
    @Test
    void getMemberByIdFailTest() {
        // Given
        Long memberId = 1L;

        // When
        when(memberRepository.findByMemberIdAndIsDeletedFalse(memberId)).thenReturn(
            Optional.empty());

        // Then
        assertThrows(IllegalArgumentException.class, () -> memberService.getMemberById(memberId));
    }

    @DisplayName("이미 존재하는 이메일로 회원가입 시도 테스트")
    @Test
    void signupExistingEmailTest() {
        // Given
        MemberSignupRequestDto requestDto = new MemberSignupRequestDto();
        requestDto.setEmail("test@test.com");
        requestDto.setPassword("password123");
        requestDto.setName("유재석");
        requestDto.setTel("010-1234-5678");
        requestDto.setGender(true);
        requestDto.setAge(45);


        when(memberRepository.findByEmailAndIsDeletedFalse("test@test.com"))
            .thenReturn(Optional.of(Member.builder().build()));


        assertThrows(IllegalArgumentException.class, () -> memberService.signupAndReturnEntity(requestDto));


        verify(memberRepository, never()).save(any(Member.class));
    }
}
