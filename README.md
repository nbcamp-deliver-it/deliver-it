## 딜리버잇(Deliver-it)

- 사용자가 음식을 주문하고 음식점 사장님이 주문을 관리할 수 있는 배달 주문 관리 플랫폼입니다 

</br>

## 개발 환경 

- Language : Java 17 
- Framework : Spring Boot 3.5.6
- DB : MySQL 8.0
- CI/CD : AWS, Jenkins

</br>

## Flow chart 

- 사용자 화면 중심 

![user_story_flow_chart](./img/user_story_flow_chart.png)

- 주문 상태 전이 

![state_diagram](./img/state_diagram.png)

</br>

## ERD 

![erd](./img/erd.png)

</br>

## Architecture

![architecture](./img/architecture.png)

- AWS 환경 구성 (VPC, Security Group)
- 리버스 프록시 (nginx) 구성 
  - 무중단 배포를 위해 서버 앞단에 배치
- 서버 인스턴스 2대 (EC2 / t3.small)
  - 2 CPU / 2 GB

</br>

### CI/CD 구성 

![ci_cd_architecture](./img/cicd-architecture.png)

- github webhook push event 구독 
- nginx 를 통해 git 접근 및 jenkins 접근 
- 서버 인스턴스 각각 롤링 배포 방식으로 배포 

</br>

## 팀원 

<table width="950px">
    <thead>
    </thead>
    <tr>
        <th>Picture</th>
        <td align="center"><a href="https://github.com/WisdomKim95"><img src="https://avatars.githubusercontent.com/u/89783215?v=4" width="60" height="60" alt="이름1"></a></td>
        <td align="center"><a href="https://github.com/leeMK09"><img src="https://avatars.githubusercontent.com/u/68103540?v=4" width="60" height="60" alt="이름2"></a></td>
        <td align="center"><a href="https://github.com/huhsiyoung"><img src="https://avatars.githubusercontent.com/u/102939647?v=4" width="60" height="60" alt="이름3"></a></td>
        <td align="center"><a href="https://github.com/newbee9507"><img src="https://avatars.githubusercontent.com/u/130113243?v=4" width="60" height="60" alt="이름4"></a></td>
        <td align="center"><a href="https://github.com/yoo20370"><img src="https://avatars.githubusercontent.com/u/109394244?v=4" width="60" height="60" alt="이름5"></a></td>
        <td align="center"><a href="https://github.com/YB-Taekwon"><img src="https://avatars.githubusercontent.com/u/188652877?v=4" width="60" height="60" alt="이름6"></a></td>
    </tr>
    <tr>
        <th>Name</th>
        <td align="center">김지혜</td>
        <td align="center">이명규</td>
        <td align="center">허시영</td>
        <td align="center">조재희</td>
        <td align="center">유영우</td>
        <td align="center">김이안</td>
    </tr>
    <tr>
        <th>Position</th>
        <td align="center"><b>Leader</b><br>Backend<br></td>
        <td align="center"><br>Backend<br></td>
        <td align="center"><br>Backend<br></td>
        <td align="center"><br>Backend<br></td>
        <td align="center"><br>Backend<br></td>
        <td align="center"><br>Backend<br></td>
    </tr>
    <tr>
        <th>GitHub</th>
        <td align="center"><a href="https://github.com/WisdomKim95"><img src="https://img.shields.io/badge/WisdomKim95-black?style=social&logo=github" alt="WisdomKim95 GitHub"/></a></td>
        <td align="center"><a href="https://github.com/leeMK09"><img src="https://img.shields.io/badge/leeMK09-black?style=social&logo=github" alt="leeMK09 GitHub"/></a></td>
        <td align="center"><a href="https://github.com/huhsiyoung"><img src="https://img.shields.io/badge/huhsiyoung-black?style=social&logo=github" alt="huhsiyoung GitHub"/></a></td>
        <td align="center"><a href="https://github.com/newbee9507"><img src="https://img.shields.io/badge/newbee9507-black?style=social&logo=github" alt="newbee9507 GitHub"/></a></td>
        <td align="center"><a href="https://github.com/yoo20370"><img src="https://img.shields.io/badge/yoo20370-black?style=social&logo=github" alt="yoo20370 GitHub"/></a></td>
        <td align="center"><a href="https://github.com/YB-Taekwon"><img src="https://img.shields.io/badge/YB--Taekwon-black?style=social&logo=github" alt="YB-Taekwon GitHub"/></a></td>
    </tr>
</table>

</div>
