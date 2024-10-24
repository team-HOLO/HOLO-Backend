# 서비스 소개
## HOLO: Home Organization & Lifestyle Optimization
[홈화면이나 있어보이는 이미지+ 로고](~~~~~)
HOLO는 1인 가구를 대상으로 한 인테리어 쇼핑몰입니다.
HOLO는 인테리어 제품을 쉽고 빠르게 구매할 수 있습니다!
"혼자만의 공간을 나만의 감성으로" HOLO는 1인 가구를 위한 맞춤형 인테리어 경험을 제공합니다.(후보)

# 팀원 소개
- 팀원 모두 프론트엔드와 백엔드에 참여하여, 도메인 별로 담당을 나눠 프로젝트를 진행했습니다.
  | 이름 (Name)  | 역할 (Role)  | 담당 도메인 (Domain)      | 주요 기여 (Key Contributions) |
  |-------------|------------|-------------------------|-------------------------------|
  | 심우민       | 팀장 (Leader) | 상품 (Product)          | 상품 등록/수정/삭제 API, 상품 정렬 및 검색 기능, S3를 이용한 이미지 업로드 기능 구현 |
  | 백승주       | 팀원 (Member) | 회원 (Member)           |  회원 가입/로그인/수정/ 삭제 API, Spring Security- jwt 토큰 쿠키 사용 인증 방식, oauth 로그인 기능 구현 |
  | 손병훈       | 팀원 (Member) | 주문 (Order)            |  주문 등록/수정/삭제 API, 주문 조회 및 상태 변경기능 |
  | 윤지현       | 팀원 (Member) | 카테고리 (Category)      | 카테고리 등록/수정/삭제 API, 카테고리 조회 기능 구현, Netlify와 Github Actions를 사용한 배포 자동화, EC2 서버 세팅 |
  | 임서현       | 팀원 (Member) | 장바구니 (Cart)          | localstorge를 통해 장바구니 등록 /수량 수정/ 선택 삭제/ 전체 삭제/조회/ 총 가격 계산 |

# 핵심 기능
- 일반 유저
    - 장바구니
    - 상품 구매
    - Oauth 이용 로그인
- 관리자
    - 상품 관리
    - 카테고리 관리
    - 주문 관리

# 기술 스택
- **Frontend** : React(18.3.1), Material UI(6.1.3)
- **Backend** : Spring Boot(3.3.4), MySQL Community(8.0.39)
- **Authentication** : JWT
- **Security** : Spring Security6
- **Deployment**: Netlify(Frontend), Github Actions(CI/CD), AWS EC2(Backend, Ubuntu 22.04.5 LTS)

# 아키텍처
![HOLO_Architecture_Diagram](/uploads/26d47ea3156341c8afd23ffc5b3bfbbf/HOLO_sample_.drawio__3_.png)
- Netlify를 이용한 React 프론트 배포
- Nginx는 SSL을 처리하고, 리버스 프록시로서 클라이언트 요청을 Spring 애플리케이션으로 전달
- AWS S3, RDS를 사용해 데이터 저장 및 이미지 업로드 기능 제공
- Github Actions를 이용한 배포 자동화
- Google Oauth를 사용한 사용자 인증 및 인가 기능 제공
- 주문 생성, 수정, 삭제와 같은 주요 이벤트는 Discord Webhook을 통해 관리자에게 실시간으로 알림 전달


# ERD
![image](/uploads/caee15e25414f21ce5dc126c4b4df4d4/image.png)

# API 문서
- API 문서 -> 노션에 있는거 Gitlab 위키에 옮겨서 링크
- Swagger-UI: https://elice-holo.shop/swagger-ui/index.html

# 트러블슈팅
- 각자 경험한 트러블 슈팅 나열

# Links
- https://elice-holo.netlify.app
- API 서버: https://elice-holo.shop
