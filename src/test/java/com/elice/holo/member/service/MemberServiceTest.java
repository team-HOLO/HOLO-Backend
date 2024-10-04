package com.elice.holo.member.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.elice.holo.member.domain.Member;
import com.elice.holo.member.dto.MemberLoginRequestDto;
import com.elice.holo.member.dto.MemberResponseDto;
import com.elice.holo.member.dto.MemberSignupRequestDto;
import com.elice.holo.member.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

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
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        MemberResponseDto result = memberService.signup(requestDto);

        // Then
        assertEquals(result.getEmail(), requestDto.getEmail());
        assertEquals(result.getName(), requestDto.getName());
        assertEquals(result.getTel(), requestDto.getTel());
        assertEquals(result.getGender(), requestDto.getGender());
        assertEquals(result.getAge(), requestDto.getAge());
        assertEquals(result.getIsAdmin(), false);
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

        // When
        when(memberRepository.findByEmailAndIsDeletedFalse(loginRequest.getEmail()))
            .thenReturn(Optional.of(member));

        MemberResponseDto result = memberService.login(loginRequest);

        // Then
        assertEquals(result.getEmail(), loginRequest.getEmail());
        assertEquals(result.getName(), "유재석");
        assertEquals(result.getTel(), "010-1234-5678");
        assertEquals(result.getGender(), true);
        assertEquals(result.getAge(), 45);
        assertEquals(result.getIsAdmin(), false);
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

        // Act
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
}
