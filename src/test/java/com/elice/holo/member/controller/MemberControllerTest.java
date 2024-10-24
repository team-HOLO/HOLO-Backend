package com.elice.holo.member.controller;

import com.elice.holo.member.domain.Member;
import com.elice.holo.member.dto.MemberLoginRequestDto;
import com.elice.holo.member.dto.MemberResponseDto;
import com.elice.holo.member.dto.MemberSignupRequestDto;
import com.elice.holo.member.dto.MemberMapper;
import com.elice.holo.member.dto.MemberUpdateRequestDto;
import com.elice.holo.member.service.MemberService;
import com.elice.holo.order.service.DiscordWebhookService;
import com.elice.holo.token.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@DisplayName("MemberController Test")
public class MemberControllerTest {

    @Mock
    private MemberService memberService;  // MemberService를 Mock으로 설정

    @Mock
    private JwtTokenProvider jwtTokenProvider;  // JwtTokenProvider를 Mock으로 설정

    @Mock
    private HttpServletResponse response;  // HttpServletResponse를 Mock으로 설정

    @Mock
    private MemberMapper memberMapper;  // MemberMapper를 Mock으로 설정

    @InjectMocks
    private MemberController memberController;

    @MockBean
    private DiscordWebhookService discordWebhookService; // DiscordWebhookService Mock

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Mock 객체 초기화
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    public void testSignupSuccess() {
        // Given
        MemberSignupRequestDto signupRequest = new MemberSignupRequestDto(
            "test@example.com", "password", "Test User", false, "010-1234-5678", true);


        Member mockMember = Member.builder()
            .email("test@example.com")
            .password("password")
            .name("Test User")
            .isAdmin(false)
            .tel("010-1234-5678")
            .gender(true)

            .isDeleted(false)
            .build();


        when(memberMapper.toEntity(signupRequest)).thenReturn(mockMember);  // toEntity 메서드 Mock 설정
        when(memberService.signupAndReturnEntity(signupRequest)).thenReturn(mockMember);
        when(jwtTokenProvider.generateToken(mockMember, java.time.Duration.ofHours(2)))
            .thenReturn("mockJwtToken");

        // When
        ResponseEntity<String> responseEntity = memberController.signup(signupRequest);

        // Then
        assertEquals(201, responseEntity.getStatusCodeValue());
        assertEquals("sign up! JWT Token: mockJwtToken", responseEntity.getBody());
        verify(memberService, times(1)).signupAndReturnEntity(signupRequest);
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    public void testLoginSuccess() {
        // Given
        MemberLoginRequestDto loginRequest = new MemberLoginRequestDto("test@example.com", "password");

        Member mockMember = Member.builder()
            .email("test@example.com")
            .password("password")
            .name("Test User")
            .isAdmin(false)
            .tel("010-1234-5678")
            .gender(true)

            .isDeleted(false)
            .build();


        when(memberService.loginAndReturnEntity(loginRequest)).thenReturn(mockMember);
        when(jwtTokenProvider.generateToken(mockMember, java.time.Duration.ofHours(2)))
            .thenReturn("mockJwtToken");

        // When
        ResponseEntity<String> responseEntity = memberController.login(loginRequest, response);

        // Then
        assertEquals(200, responseEntity.getStatusCodeValue());
        verify(response, times(1)).addHeader(eq("Set-Cookie"), anyString());
    }

    @Test
    @DisplayName("로그인 실패 테스트")
    public void testLoginFail() {
        // Given
        MemberLoginRequestDto loginRequest = new MemberLoginRequestDto("test@example.com", "wrongPassword");


        when(memberService.loginAndReturnEntity(loginRequest))
            .thenThrow(new IllegalArgumentException("비밀번호가 일치하지 않습니다."));

        // When
        ResponseEntity<String> responseEntity = memberController.login(loginRequest, response);


        assertEquals(400, responseEntity.getStatusCodeValue());
        assertEquals("비밀번호가 일치하지 않습니다.", responseEntity.getBody());
    }

    @Test
    @DisplayName("로그아웃 테스트")
    public void testLogout() {
        // When
        ResponseEntity<String> responseEntity = memberController.logout(response);

        // Then
        verify(response, times(1)).addHeader(eq("Set-Cookie"), anyString());
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals("logout finished", responseEntity.getBody());
    }

    @Test
    @DisplayName("모든 회원 조회 테스트")
    public void testGetAllMembers() {
        List<MemberResponseDto> mockMemberList = List.of(
            new MemberResponseDto(1L, "test1@example.com", "Test User 1", "010-1111-2222", true, 25, false),
            new MemberResponseDto(2L, "test2@example.com", "Test User 2", "010-3333-4444", true, 30, false)
        );


        when(memberService.getAllMembers()).thenReturn(mockMemberList);

        // When
        ResponseEntity<List<MemberResponseDto>> responseEntity = memberController.getAllMembers();

        // Then
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(mockMemberList.size(), responseEntity.getBody().size());
        verify(memberService, times(1)).getAllMembers();
    }

    @Test
    @DisplayName("특정 회원 조회 테스트")
    public void testGetMemberById() {
        // Given
        Long memberId = 1L;
        MemberResponseDto mockMember = new MemberResponseDto(memberId, "test@example.com", "Test User", "010-1111-2222", true, 25, false);


        when(memberService.getMemberById(memberId)).thenReturn(mockMember);

        // When
        ResponseEntity<MemberResponseDto> responseEntity = memberController.getMemberById(memberId);

        // Then
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(mockMember.getEmail(), responseEntity.getBody().getEmail());
        verify(memberService, times(1)).getMemberById(memberId);
    }

    @Test
    @DisplayName("회원 정보 수정 테스트")
    public void testUpdateMember() {
        // Given
        Long memberId = 1L;
        MemberUpdateRequestDto updateRequest = new MemberUpdateRequestDto(
            "test@example.com", // 이메일 추가
            "newPassword",      // 패스워드 추가
            "Updated User",
            "010-9999-8888",
            true

        );
        MemberResponseDto updatedMember = new MemberResponseDto(memberId, "test@example.com", "Updated User", "010-9999-8888", true, 26, false);


        when(memberService.updateMember(memberId, updateRequest)).thenReturn(updatedMember);

        // When
        ResponseEntity<MemberResponseDto> responseEntity = memberController.updateMember(memberId, updateRequest);

        // Then
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(updatedMember.getName(), responseEntity.getBody().getName());
        verify(memberService, times(1)).updateMember(memberId, updateRequest);
    }

    @Test
    @DisplayName("회원 삭제 테스트")
    public void testDeleteMember() {
        // Given
        Long memberId = 1L;
        HttpServletResponse response = mock(HttpServletResponse.class);  // HttpServletResponse 목 객체 생성

        // When
        ResponseEntity<Void> responseEntity = memberController.deleteMember(memberId, response);

        // Then
        assertEquals(204, responseEntity.getStatusCodeValue());
        verify(memberService, times(1)).deleteMember(memberId);


        verify(response, times(1)).addHeader(eq("Set-Cookie"), argThat(cookie ->
                cookie.contains("Max-Age=0") && cookie.contains("jwtToken=;")
        ));
    }

    @Test
    @DisplayName("관리자 권한 확인 테스트")
    public void testCheckAdmin() {
        // Given
        Authentication mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.isAuthenticated()).thenReturn(true);


        Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(mockAuthentication.getAuthorities()).thenAnswer(invocation -> authorities);


        SecurityContextHolder.getContext().setAuthentication(mockAuthentication);


        ResponseEntity<Boolean> responseEntity = memberController.checkAdmin();

        // Then
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(true, responseEntity.getBody());
    }
}
