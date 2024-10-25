# 서비스 소개
## HOLO: Home Organization & Lifestyle Optimization
![스크린샷_2024-10-24_오후_5.46.50](/uploads/c5608b25bd9a8a5d8079037c53e6e27f/스크린샷_2024-10-24_오후_5.46.50.png)
HOLO는 1인 가구를 대상으로 한 인테리어 쇼핑몰입니다. 1인 가구의 라이프 스타일과 딱 맞는 인테리어 제품을 쉽고 빠르게 구매할 수 있습니다!  
> **"혼자만의 공간을 나만의 감성으로"** HOLO는 1인 가구를 위한 맞춤형 인테리어 경험을 제공합니다.

# Links
[![Netlify Status](https://api.netlify.com/api/v1/badges/c94a2a65-b5d9-4f7a-b9ba-bfcd8080ee16/deploy-status)](https://app.netlify.com/sites/elice-holo/deploys)  | ![Github Actions](https://github.com/team-HOLO/HOLO-BE/actions/workflows/deploy.yml/badge.svg?branch=dev)
- https://elice-holo.netlify.app 
- API 서버: https://elice-holo.shop/api/categories/all

# 팀원 소개
- 팀원 모두 프론트엔드와 백엔드에 참여하여, 도메인 별로 담당을 나눠 프로젝트를 진행했습니다.
  | 이름 (Name)  | 역할 (Role)  | 담당 도메인 (Domain)      | 주요 기여 (Key Contributions) |
  |-------------|------------|-------------------------|-------------------------------|
  | 심우민       | 팀장 (Leader) | 상품 (Product)          | 상품 등록/수정/삭제 API, 상품 정렬 및 검색 기능, S3를 이용한 이미지 업로드 기능 구현 |
  | 백승주       | 팀원 (Member) | 회원 (Member)           |  회원 가입/로그인/수정/ 삭제 API, Spring Security- jwt 토큰 쿠키 사용 인증 방식, oauth 로그인 기능 구현 |
  | 손병훈       | 팀원 (Member) | 주문 (Order)            |  주문 등록/수정/삭제 API, 주문 조회 및 상태 변경기능 |
  | 윤지현       | 팀원 (Member) | 카테고리 (Category)      | 카테고리 등록/수정/삭제 API, 카테고리 조회 기능 구현, Netlify와 Github Actions를 사용한 CI/CD, EC2 서버 세팅, Discord 주문 알림 |
  | 임서현       | 팀원 (Member) | 장바구니 (Cart)          | localstorge를 통해 장바구니 등록 /수량 수정/ 선택 삭제/ 전체 삭제/조회/ 총 가격 계산 |

# 핵심 기능
## 일반 유저

### 회원 가입
![ScreenRecorderProject70](/uploads/14032889efc8929f927050995a075e23/ScreenRecorderProject70.gif)
### 장바구니
![홀로_장바구니](/uploads/a483dd9d1f1f3ce84790265d2c03c4f8/홀로_장바구니.gif)
### 상품 구매
![주문등록진짜진짜](/uploads/ac06748470fa9ed4e0eab8c60ca61af2/주문등록진짜진짜.gif)
### Oauth 이용 로그인
![oauth로그인gif](/uploads/6cedee2401db7e0244c8d2015e5a1ca3/oauth로그인gif.gif)
### 마이페이지
![주문마이페이지진짜진짜](/uploads/45e9ca438ab75724e002adfc1d5da4ce/주문마이페이지진짜진짜.gif)
## 관리자
### 상품 관리
### 카테고리 관리
![카테고리-시연-최종](/uploads/0b3d1ec0b2842c3df08694da9581f73a/카테고리-시연-최종.gif)
### 주문 관리
![시연](/uploads/0fd217f5c44f8bf5dc4baaaed4f0c0dd/시연.gif)
### 접근 제한
![접근제한](/uploads/15f473d8485b66ac6fb44ff6c834718b/접근제한.gif)
- 관리자 권한 없이 Admin 페이지에 접근한 경우: 권한 안내 페이지로 이동
- 유저 권한 없이 MyPage에 접근한 경우: 로그인 페이지로 이동
## 디스코드 봇
- 주문 생성, 수정, 취소 시 관리자에게 실시간 Discord 알림 전송
- CS 대응을 위한 실시간 모니터링 기능
![스크린샷_2024-10-25_오전_11.31.25](/uploads/336a7f176977384b07e415b79c026444/스크린샷_2024-10-25_오전_11.31.25.png)
![스크린샷_2024-10-25_오전_11.30.30](/uploads/2aaf62359a166f7b099c4c38c20390d8/스크린샷_2024-10-25_오전_11.30.30.png)


# 기술 스택
- **Frontend** : React(v18.3.1), Material UI(v6.1.3)
- **Backend** : Spring Boot(v3.3.4), Java(v21)
- **Storage** : AWS S3(이미지 파일 저장), AWS RDS (MySQL Community v8.0.39)
- **Test** : Junit(v5.10.3)
- **Security & Authentication** : Spring Security (v6), JWT, Google OAuth2, Let's Encrypt(TLSv1.2, v1.3)
- **Deployment**: Netlify(Frontend), AWS EC2(Backend, Ubuntu 22.04.5 LTS), Github Actions(CI/CD), Nginx(Reverse Proxy, 1.18.0)
- **DNS**: AWS Route 53

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
 - 이미지 파일 저장: AWS S3
 - 도메인 및 DNS
    - 가비아에서 구매한 도메인 `elice-holo.shop`을 AWS Route 53에서 관리
    - EC2 인스턴스의 퍼블릭 IP에 도메인 연결
    - SSL/TLS 인증서를 사용한 HTTPS 제공 (Let's Encrypt)
```

# API 문서
- Swagger-UI: https://elice-holo.shop/swagger-ui/index.html

# [트러블슈팅](https://www.notion.so/elice-track/7d0239a270ed4bac8b55f17b0c511fe7?v=35b8497c9c3042a9b1ed863eaaefdb47&pvs=4)
