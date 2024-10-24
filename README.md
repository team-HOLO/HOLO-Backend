# 서비스 소개
## HOLO: Home Organization & Lifestyle Optimization
![스크린샷_2024-10-24_오후_5.46.50](/uploads/c5608b25bd9a8a5d8079037c53e6e27f/스크린샷_2024-10-24_오후_5.46.50.png)
HOLO는 1인 가구를 대상으로 한 인테리어 쇼핑몰입니다. 1인 가구의 라이프 스타일과 딱 맞는 인테리어 제품을 쉽고 빠르게 구매할 수 있습니다!  
> **"혼자만의 공간을 나만의 감성으로"** HOLO는 1인 가구를 위한 맞춤형 인테리어 경험을 제공합니다.

# 팀원 소개
- 팀원 모두 프론트엔드와 백엔드에 참여하여, 도메인 별로 담당을 나눠 프로젝트를 진행했습니다.
  | 이름 (Name)  | 역할 (Role)  | 담당 도메인 (Domain)      | 주요 기여 (Key Contributions) |
  |-------------|------------|-------------------------|-------------------------------|
  | 심우민       | 팀장 (Leader) | 상품 (Product)          | 상품 등록/수정/삭제 API, 상품 정렬 및 검색 기능, S3를 이용한 이미지 업로드 기능 구현 |
  | 백승주       | 팀원 (Member) | 회원 (Member)           |  회원 가입/로그인/수정/ 삭제 API, Spring Security- jwt 토큰 쿠키 사용 인증 방식, oauth 로그인 기능 구현 |
  | 손병훈       | 팀원 (Member) | 주문 (Order)            |  주문 등록/수정/삭제 API, 주문 조회 및 상태 변경기능 |
  | 윤지현       | 팀원 (Member) | 카테고리 (Category)      | 카테고리 등록/수정/삭제 API, 카테고리 조회 기능 구현, Netlify와 Github Actions를 사용한 배포 자동화, EC2 서버 세팅, Discord 주문 알림 |
  | 임서현       | 팀원 (Member) | 장바구니 (Cart)          | localstorge를 통해 장바구니 등록 /수량 수정/ 선택 삭제/ 전체 삭제/조회/ 총 가격 계산 |

# 핵심 기능
## 일반 유저
### 장바구니
### 상품 구매
### Oauth 이용 로그인
## 관리자
### 상품 관리
### 카테고리 관리
![카테고리-시연-최종](/uploads/0b3d1ec0b2842c3df08694da9581f73a/카테고리-시연-최종.gif)
### 주문 관리
## 디스코드 봇
### 새로운 주문 등록 알림
### 주문 정보 수정 알림
### 주문 취소 알림
  
장바구니 기능
![장바구니_기능](/uploads/4798f39ab173c3ab0f8bdf7578e650de/장바구니_기능.gif)


# 기술 스택
- **Frontend** : React(v18.3.1), Material UI(v6.1.3)
- **Backend** : Spring Boot(v3.3.4), Java(v21)
- **Storage** : AWS S3(이미지 파일 저장)
- **Database** : AWS RDS (MySQL Community v8.0.39)
- **Test** : Junit(v5.10.3)
- **Security & Authentication** : Spring Security (v6), JWT, Google OAuth2
- **Deployment**: Netlify(Frontend), AWS EC2(Backend, Ubuntu 22.04.5 LTS), Github Actions(CI/CD)
- **Domain & DNS**: AWS Route 53
- **SSL/TLS**: Let's Encrypt(TLSv1.2, v1.3)

# 아키텍처
![HOLO_Architecture_Diagram](/uploads/26d47ea3156341c8afd23ffc5b3bfbbf/HOLO_sample_.drawio__3_.png)
- Netlify를 이용한 React 프론트 배포
- Nginx는 SSL을 처리하고, 리버스 프록시로서 클라이언트 요청을 Spring 애플리케이션으로 전달
- AWS S3, RDS를 사용해 데이터 저장 및 이미지 업로드 기능 제공
- Github Actions를 이용한 CI/CD
- Google Oauth를 사용한 사용자 인증 및 인가 기능 제공
- 주문 생성, 수정, 삭제와 같은 주요 이벤트는 Discord Webhook을 통해 관리자에게 실시간으로 알림 전달


# ERD
![image](/uploads/caee15e25414f21ce5dc126c4b4df4d4/image.png)

# 배포 환경
```
 - 서버 : AWS EC2
   - 운영 체제: Ubuntu 22.04.5 LTS (GNU/Linux 6.8.0-1017-aws x86_64)
   - JRE: openjdk version "21.0.4"
   - 애플리케이션 서버: Spring Boot (내장 Tomcat 사용)
   - 리버스 프록시: Nginx(v1.18.0)
     - HTTPS 및 SSL/TLS 처리
     - TLSv1.2, TLSv1.3
 - 데이터 베이스: AWS RDS
   - MySQL Community(8.0.39)
 - 파일 저장: AWS S3
 - 도메인 및 DNS
    - 가비아에서 구매한 도메인 `elice-holo.shop`을 AWS Route 53에서 관리
    - EC2 인스턴스의 퍼블릭 IP에 도메인 연결
    - SSL/TLS 인증서를 사용한 HTTPS 제공 (Let's Encrypt)
```

# API 문서
- [API 문서](https://kdt-gitlab.elice.io/cloud_track/class_04/web_project2/team01/holo-backend/-/wikis/Documents/API-%EB%AA%85%EC%84%B8%EC%84%9C)
- Swagger-UI: https://elice-holo.shop/swagger-ui/index.html

# 트러블슈팅
- [더 많은 트러블 슈팅을 보시려면 → ](https://kdt-gitlab.elice.io/cloud_track/class_04/web_project2/team01/holo-backend/-/wikis/TroubleShooting/TroubleShooting)

# Links
- https://elice-holo.netlify.app
- API 서버: https://elice-holo.shop/api/categories/all
