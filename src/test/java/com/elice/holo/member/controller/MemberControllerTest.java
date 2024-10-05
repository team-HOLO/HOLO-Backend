package com.elice.holo.member.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.elice.holo.member.domain.Member;
import com.elice.holo.member.dto.MemberSignupRequestDto;
import com.elice.holo.member.dto.MemberUpdateRequestDto;
import com.elice.holo.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @DisplayName("회원가입 API 테스트")
    @Test
    void signupTest() throws Exception {
        // Given
        final String url = "/api/members/signup";
        MemberSignupRequestDto signupRequest = new MemberSignupRequestDto();
        signupRequest.setEmail("test@test.com");
        signupRequest.setPassword("password123");
        signupRequest.setName("유재석");
        signupRequest.setTel("010-1234-5678");
        signupRequest.setGender(true);
        signupRequest.setAge(45);

        String requestBody = objectMapper.writeValueAsString(signupRequest);

        // When
        ResultActions result = mockMvc.perform(
            post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        // Then
        result.andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value("test@test.com"))
            .andExpect(jsonPath("$.name").value("유재석"))
            .andExpect(jsonPath("$.tel").value("010-1234-5678"))
            .andExpect(jsonPath("$.gender").value(true))
            .andExpect(jsonPath("$.age").value(45))
            .andExpect(jsonPath("$.isAdmin").value(false));
    }

    @DisplayName("모든 회원 조회 API 테스트")
    @Test
    void getAllMembersTest() throws Exception {
        // Given
        final String url = "/api/members";

        Member member1 = Member.builder()
            .email("test1@test.com")
            .password("password123")
            .name("Tester1")
            .tel("010-1234-5678")
            .gender(true)
            .age(30)
            .isDeleted(false)
            .isAdmin(false)
            .build();

        Member member2 = Member.builder()
            .email("test2@test.com")
            .password("password123")
            .name("Tester2")
            .tel("010-8765-4321")
            .gender(false)
            .age(25)
            .isDeleted(false)
            .isAdmin(false)
            .build();

        memberRepository.save(member1);
        memberRepository.save(member2);

        // When
        ResultActions result = mockMvc.perform(get(url));

        // Then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$[0].email").value("test1@test.com"))
            .andExpect(jsonPath("$[0].name").value("Tester1"))
            .andExpect(jsonPath("$[0].tel").value("010-1234-5678"))
            .andExpect(jsonPath("$[0].gender").value(true))
            .andExpect(jsonPath("$[0].age").value(30))
            .andExpect(jsonPath("$[0].isAdmin").value(false))
            .andExpect(jsonPath("$[1].email").value("test2@test.com"))
            .andExpect(jsonPath("$[1].name").value("Tester2"))
            .andExpect(jsonPath("$[1].tel").value("010-8765-4321"))
            .andExpect(jsonPath("$[1].gender").value(false))
            .andExpect(jsonPath("$[1].age").value(25))
            .andExpect(jsonPath("$[1].isAdmin").value(false));
    }

    @DisplayName("특정 회원 조회 API 테스트")
    @Test
    void getMemberByIdTest() throws Exception {
        // Given
        final String url = "/api/members/{memberId}";

        Member member = Member.builder()
            .email("test1@test.com")
            .password("password123")
            .name("Tester1")
            .tel("010-1234-5678")
            .gender(true)
            .age(30)
            .isDeleted(false)
            .isAdmin(false)
            .build();

        Member savedMember = memberRepository.save(member);

        // When
        ResultActions result = mockMvc.perform(get(url, savedMember.getMemberId()));

        // Then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("test1@test.com"))
            .andExpect(jsonPath("$.name").value("Tester1"))
            .andExpect(jsonPath("$.tel").value("010-1234-5678"))
            .andExpect(jsonPath("$.gender").value(true))
            .andExpect(jsonPath("$.age").value(30))
            .andExpect(jsonPath("$.isAdmin").value(false));
    }

    @DisplayName("회원 정보 수정 API 테스트")
    @Test
    void updateMemberTest() throws Exception {
        // Given
        final String url = "/api/members/{memberId}";

        Member member = Member.builder()
            .email("test@test.com")
            .password("password123")
            .name("유재석")
            .tel("010-1234-5678")
            .gender(true)
            .age(45)
            .isDeleted(false)
            .isAdmin(false)
            .build();

        Member savedMember = memberRepository.save(member);

        MemberUpdateRequestDto updateRequest = new MemberUpdateRequestDto();
        updateRequest.setEmail("updated@test.com");
        updateRequest.setName("UpdatedName");
        updateRequest.setTel("010-1235-5678");
        updateRequest.setGender(true);
        updateRequest.setAge(30);

        String requestBody = objectMapper.writeValueAsString(updateRequest);

        // When
        ResultActions result = mockMvc.perform(
            put(url, savedMember.getMemberId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        // Then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("updated@test.com"))
            .andExpect(jsonPath("$.name").value("UpdatedName"))
            .andExpect(jsonPath("$.tel").value("010-1235-5678"))
            .andExpect(jsonPath("$.gender").value(true))
            .andExpect(jsonPath("$.age").value(30))
            .andExpect(jsonPath("$.isAdmin").value(false));
    }

    @DisplayName("회원 삭제 API 테스트")
    @Test
    void deleteMemberTest() throws Exception {
        // Given
        final String url = "/api/members/{memberId}";

        Member member = Member.builder()
            .email("test@test.com")
            .password("password123")
            .name("유재석")
            .tel("010-1234-5678")
            .gender(true)
            .age(45)
            .isDeleted(false)
            .isAdmin(false)
            .build();

        Member savedMember = memberRepository.save(member);

        // When
        ResultActions result = mockMvc.perform(delete(url, savedMember.getMemberId()));

        // Then
        result.andExpect(status().isNoContent());

        Optional<Member> deletedMember = memberRepository.findByMemberIdAndIsDeletedFalse(
            savedMember.getMemberId());
        assert (deletedMember.isEmpty());
    }
}
