name: Feature Request
about: 기능 추가 요청 🛠️
title: "[FEAT] 제목을 입력하세요"
labels: feature

body:
  - type: markdown
    attributes:
      value: |
        ## 📝 기능 요청 안내
        백엔드 기능 추가 요청을 위해 아래 항목을 작성해주세요. 필요 시 관련 팀원에게 추가 설명을 요청할 수 있습니다.

  - type: input
    id: feature-summary
    attributes:
      label: ✍️ 기능 요약
      description: "추가하려는 기능을 간단히 요약해주세요."
      placeholder: "예: JWT 인증 토큰 갱신 기능 추가"

  - type: textarea
    id: problem-statement
    attributes:
      label: 🎯 해결하려는 문제
      description: "이 기능이 해결하고자 하는 문제를 설명해주세요. 기능이 필요한 이유나 관련된 이슈를 언급해 주세요."
      placeholder: "예: 기존 토큰 만료 시 재로그인해야 하는 불편함이 있습니다."
    validations:
      required: true

  - type: textarea
    id: proposed-solution
    attributes:
      label: 💡 제안된 해결 방법
      description: "백엔드에서 이 기능을 어떻게 구현하면 좋을지 구체적으로 설명해주세요."
      placeholder: "예: 리프레시 토큰을 사용해 만료된 JWT를 갱신하는 API 추가"
    validations:
      required: true

  - type: textarea
    id: impact-analysis
    attributes:
      label: 🔍 영향을 줄 수 있는 부분
      description: "이 변경 사항이 영향을 줄 수 있는 다른 서비스, 모듈, 또는 데이터베이스 테이블을 나열해주세요."
      placeholder: "예: AuthService, User 엔티티, Redis 캐시"

  - type: input
    id: additional-notes
    attributes:
      label: 📌 참고 사항
      description: "추가적인 설명이나 논의가 필요한 부분이 있으면 작성해주세요."
      placeholder: "예: 관련된 프론트엔드 변경 사항도 필요합니다."
