# SNS_Project00
2.SNS앱(게시글) - 비공개 설정 해둠 (FireBase사용함) 

--------------------------------------------------------------------------

<img src="https://www.gstatic.com/devrel-devsite/prod/v15f72515e1c53f03e6d573e85fc193d888eb8fb1758082e4a5ecf80f00fa48ef/android/images/lockup.svg" width="300" height="100"> Android Studio

<img src="https://www.gstatic.com/devrel-devsite/prod/v15f72515e1c53f03e6d573e85fc193d888eb8fb1758082e4a5ecf80f00fa48ef/firebase/images/lockup.png" width="300" height="100"> Firebase

<img src="https://github.com/bumptech/glide/blob/master/static/glide_logo.png?raw=true" width="300" height="100"> Glide

ExoPlayer (동영상 사용할려고)

--------------------------------------------------------------------------

- 구성<br>
 1.회원가입       :ok: <br>
   이메일, 비밀번호, 비밀번호 확인, 이름, 성별, 나이, 사진<br>
 2.로그인        :ok:<br>
   이메일, 비밀번호<br>
 3.비밀번호 찾기<br>
   이메일<br>
 4.게시글 리스트  :ok:<br>
   게시글 아이템(리스트 안의 각각각 화면)<br>
 5.게시글        :ok:<br>
   제목, 내용, 댓글, 좋아요, 싫어요<br>
 6.게시글 작성   :ok:<br>
   제목, 내용, 태그<br>
 7.게시글 수정   :ok:<br>
   제목, 내용, 태그<br>
 8.내 정보<br>
   이메일, 이름, 성별, 나이, 사진<br>
 9.친구<br>
   이메일, 이름, 성별, 나이, 사진<br>
 10.설정<br>
   .<br>
   .<br>
   .<br>
  



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




<br>

## 👀 Code 구조 :
|||
|---|---|
![img3](https://user-images.githubusercontent.com/39355400/161196698-2fd7f521-47c2-40e6-bde4-77d3ca91db28.PNG)

