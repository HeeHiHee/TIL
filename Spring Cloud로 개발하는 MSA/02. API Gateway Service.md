# API Gateway?

> 사용자가 설정한 라우팅 설정에 따라서 각각 엔드포인트로 클라이언트 대신해서 요청하고 응답을 받으면 다시 클라이언트한테 전달해주는 프락시 역할

### API Gateway가 없다면

*엔드포인트 : 마이크로 서비스의 주소 

1. 클라이언트 사이드에서 엔드포인트를 직접 호출했을 경우
2. 마이크로 서비스가 생성/수정되었을때
3. 클라이언트 사이드에 있는 어플리케이션도 같이 수정/배포가 되어야함 

---

- 그래서 단일 진입점을 가지고 있는 형태로 개발이 필요하게됨
- 서버/백앤드단에다가 중간에 게이트웨이 역할을 해줄 수 있는 API Gateway를 둠
- 각각의 마이크로 서비스로 요청되는 모든 정보를 일괄적으로 처리할 수 있게됨

### 장점

**비즈니스 서비스 로직에 사전 처리, 사후 처리를 넣을 수 있음** 

1. 인증 및 권한 부여(사전 처리)
    - 인증 서비스와 권한 부여에 대한 단일 작업을 진행할 수 있음
2. 서비스 검색 통합
    - 마이크로 서비스 검색을 쉽게 할 수 있음
3. 응답 캐싱
    - 응답할 수 있는 캐싱 정보를 저장할 수 있음
4. 정책, 회로 차단기 및 QoS 다시 시도
    - 일괄적인 정책, 클라이언트로부터 요청이 들어왔었던 서비스에 문제가 생겼을시 더이상 요청을 넘겨주지 않는 회로차단기 기능도 단일진입점에서 처리할 수 있음
5. 속도 제한
6. 부하 분산
7. 로깅, 추적, 상관관계(사후 처리)
    - 마이크로 서비스들 간에도 호출되는 경우가 많아, 누가 요청했고 어디로 보내졌고 등을 추적할 수 있음
8. 헤더, 쿼리 문자열 및 청구 변환
9. IP 허용 목록에 추가

# Spring Cloud Gateway?

## Step1) Dependencies 추가

- DevTools, Eureka Discovery Client, Gateway

## Step2) application.properties(or application.yml)

### routes 설정

- zuul 라우터구현과 동일한 방식으로 routes지정
- id : 라우터의 고유한 이름
- uri : 해당하는 정보를 어디로 포워딩 시켜줄 것인지 위치정보를 설정
- predicates : 클라이언트가 조건에다가 first-service라는걸 요청하게되면 localhost 8081번으로 가겠다는 조건을 설정

### 스프링 클라우드 게이트웨이를 사용하는 큰 목적

- 비동기 처리가 가능해짐
- zuul은 동기방식 서비스를 제공
- Servlet 같은 작업에서 동기방식을 사용했기 때문에, 최신 트렌드에 맞는 펑셔널 프로그램이나 비동기 방식을 지원하지 않는 단점이 있음

## Step3) Test

- first-service 컨트롤러와 second-service 컨트롤러를 호출
- 8000번의 first-service 호출했을때, 8000번의 second-service를 호출했을때의 화면

## 서버실행

<aside>
📌 Netty 서버가 아닌 Tomcat 서버로 실행되는 문제발생
- pom.xml 파일에서 
`spring-cloud-starter-gateway-mvc` 으로 의존성이 추가되어있음
`spring-cloud-starter-gateway` 으로 수정
maven 재빌드

</aside>

# Spring Cloud Gateway - Filter 적용1

## 클라우드 자체 필터 구조

클라우드 자체 필터 구조

- Handler Mapping(요청정보)
    - 게이트웨이가 클라이언트로부터 어떤 요청이 들어왔는지 요청 정보를 받음
- Predicate(판단)
    - 그 요청에 대한 사전 조건 즉, 어떤 이름으로 요청되었는지 조건을 predicate 영역에서 분기해줌
- Pre Filter / Post Filter(사전/사후 필터)
    - 사전 필터와 사후 필터를 pre-filter와 post-filter로 작성을 해서 요청정보를 구성할 수 있음
    - Java Code
        - 예제 하나는 Java코드에서 직접 하나 만들어 볼거임
        - 그동안은 yml 파일에 작업을 했었는데, 이를 자바코드에서 처리
    - Property
        - 지금까지 해온대로 yml 파일에 직접 정의
     
# Spring Cloud Gateway - Filter 적용2

## Config.java 파일에서 filter 적용하기

라우팅 정보를 yml 파일이 아니라 java코드로 작성

### 패키지 생성

- config 패키지 생성
- FilterConfig 클래스 생성

### ApigatewayServiceApplication에 서비스 등록

ApiGateway

1. Configuration 어노테이션 등록 후 @Bean도 등록하기
2. .route에서 path와 uri 등록
    1. yml 파일에서 등록했던 내용과 동일
3. .filter로 필터를 어떻게 사용할 지 정의
    1. requestHeader와 responseHeader에 정보를 추가 

### FirstService, SecondService

1. message 메서드를 생성해 `RequestHeader` 값을 확인
2. 로그로 requestheader값을 확인하기 위해 롬복의 @Slf4j 어노테이션 사용

# Global Filter

**커스텀 필터**

- 라우팅 정보마다 필요한 필터가 있을 경우에 다 지정해줬음
- firstService에도 CustomFilter를 지정해주고, SecondService에도 CustomFilter를 지정해줬음

**글로벌 필터**

- 공통적인 필터가 있다면 하나하나 따로 지정해주지않고, 한 번에 처리 가능

- 글로벌 필터는 default-filters라는 걸 등록하면 됨
- .yml파일에서 파라미터 preLogger와 postLogger를 설정
    - 필터 클래스 안에서 .isPreLogger와 같은 용도로 사용할 수 있음

- 글로벌 필터가 모든 필터의 가장 첫번째 시작하고, 가장 마지막에 종료가 됨
- 필터들이 어떤 과정에 의해서 출력되는지 확인 가능

# Spring Cloud Gateway - Load Balancer1

## 서비스 요청 흐름

1. 유레카 서버 기동
    - 서비스 디스커버리와 레지스트레이션으로 서비스들의 등록을 담당
2. 2개의 서비스가 존재함
    - First Service
        - 포트번호 8081
        - /first-service/welcome 이라는 엔드포인트를 가짐
    - Second Sevice
        - 포트번호 8082
        - /second-service/welcom 이라는 엔드포인트를 가짐
3. 클라이언트에서 API Gateway를 통과해서 요청정보를 보내게 됨 
4. 유레카에 요청 정보가 전달되고, 마이크로 서비스의 위치정보를 전달받음
5. API Gateway가 전달받고, 해당하는 정보로 직접 포워딩 시켜줌

# Spring Cloud Gateway - Load Balancer2

## 서버 2개 기동

1. 복사해서 다른 포트로 실행
2. 터미널에서 서버 생성
    - 서버를 실행하고 터미널을 열어서 디렉토리 목록 확인 `ls -al`
    - 디렉토리 목록에 src 폴더가 있으면 계속 진행
    - `mvn clean compile package`
        - clean : 기존의 메이븐 빌드 다 삭제
        - comile : 컴파일
        - package : jar파일 war파일 패키징
    - 필요한 jar파일이 빌드되고, target이라는 폴더에 jar파일이 생성됨
    - target 파일 내에 .jar파일이 있는지 확인 후 jar 파일 실행
    - `java -jar -Dserver.port=9092 .target/second-service-0.0.1-SNAPSHOT.jar`
3. 직접 패키징 파일을 만들어 놓고 실행

### 서비스 흐름

API Gateway 파일

1. /first-service/** 요청이 API Gateway로 들어옴
2. 유레카 서버에서 MY-FIRST-SERVICE라는 네이밍을 가진 서비스의 위치를 확인
3. 위치를 확인했더니 포트번호가 8081, 9091 2개가 있음
4. 2개의 서버 중 하나의 서버를 선택해서 gateway에게 전달

## 여러 개의 서버 중에서 어느 서버로 갔는지 확인

1. request에 있는 getServerPort를 사용해서 포트번호 얻어오기 
2. 환경변수에 등록되어진 정보중에서 LocalServerPort로 포트번호 얻어오기 

- 랜덤 포트번호를 지정해서 서버를 기동하고, 다른 서버는 커맨드를 통해서 기동
    - 포스트맨을 통해 호출해보면 2가지의 포트로 나눠서 접속되는 것을 알 수 있음
