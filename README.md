<h1 align="center">
  정보공유SNS
</h1>


## 👋 소개
학교 안에서 학과 사람들만의 소소한 일상, 정보를 주고 받기 위한 목적으로 개발한 애플리케이션입니다.


<br>


## 📅 기간
- 2019.07 ~ 2019.08

## 😁 인원
- 1명

## 🔨 Language
- Java

## 🛠 Tool
- IDE [Android Studio], Server [Firebase]

## 📚 Library
- Glide [Image Library], ExoPlayer [미디어 재생 Library]

## ⚙️ Server 개발 
-	사용자 인증 기능 (회원가입/로그인/Email기반 비밀번호재설정) 개발 / <b>Firebase Authentication</b>
-	게시글 관리 기능 (등록/수정) 개발 / <b>Firebase Cloud Firestore (DB)</b>
-	게시글 첨부 파일 관리 (이미지) 기능 / <b>Firebase Storage (File)</b>
-	Client UI (게시글/사진,영상/회원정보/회원리스트)개발 / <b>Glide, ExoPlayer</b>


<br>

## 👀 ‘학과 SNS’ Service 구조 :
![img10](https://user-images.githubusercontent.com/39355400/161201685-0b479319-143a-465d-832b-2308ab1dd816.PNG)

<details>
    <summary>자세히</summary>

<!-- summary 아래 한칸 공백 두고 내용 삽입 -->
< Authentication를 사용한 비밀번호 재설정 > 
|1) 재설정할 이메일 입력|2) 해당 이메일로 변경메일 발송 후, 변경|
|---|---|
|![img12](https://user-images.githubusercontent.com/39355400/161269580-db13df6f-70ba-4893-9c97-2308611f49c5.PNG)|![img13](https://user-images.githubusercontent.com/39355400/161269582-96e3f967-3bfe-4f1f-9771-53aad457421d.PNG)|

< Cloud Firestore를 사용한 게시글, 회원정보 데이터 관리 >
||
|---|
|![img14](https://user-images.githubusercontent.com/39355400/161269586-baf78b27-6f04-4110-9de0-54dd7129785e.PNG)|

< Storage를 이용한 이미지, 동영상 데이터 관리 >
||
|---|
|![img15](https://user-images.githubusercontent.com/39355400/161269589-16b38270-4ae6-4e44-b02d-4f5610244b12.PNG)|  
</details>


<br>

## 👀 ‘학과 SNS’ Service UI : 
![img11](https://user-images.githubusercontent.com/39355400/161201689-72449f14-1eff-47ff-b0f5-6f3f9fdd6e15.PNG)

