## 실행 및 테스트케이스 실행 방법

#### 첫 실행시 아래 항목으로 테이블을 생성해야합니다.

```shell
spring.jpa.hibernate.ddl-auto = create
spring.sql.init.mode=always
spring.jpa.defer-datasource-initialization:true
```

#### 환경설정 값에 암호화를 위한 비번을 넣어야 합니다.

```shell
jasypt.encryptor.password=mypass를 넣고 실행합니다.
```

#### 또는 jar 실행시

```shell
-Djasypt.encryptor.password=mypass
```

를 넣고 실행합니다.

## database

1. 사전에 회원 가입 가능한 사용자라는 제약사항이 있어서 resources/data.sql 에 사전에 필요한 회원 데이터들을 넣어 놓았습니다.
2. h2 사용하였습니다.
3.

## Swagger 주소

http://localhost:8080/swagger-ui/

## 요구 사항 구현 여부

1. login endpoint를 swagger에 보여주는 설정은 못해서 따로 postman으로 로그인 해서 access token을 가져와야 합니다.
2. 결정 세액 계산 로직은 구현을 했는데 값이 맞는지 확인이 잘 안되네요 ㅠ (Tax.java)
3. /szs/scrap api는 일단 기본은 비동기로 호출되도록 구현한 상태이고 service와 junit에는 동기, 비동기 호출을 구현한 상태입니다.

## 구현 방법

1. 인증은 JWT토큰을 사용하도록 구현되어 있습니다.
2. scrap api는 retrofit2로 구현한 상태입니다.
3. 결정 세액 계산 로직은 DB에서 데이터를 읽고 get할때 계산하도록 되어 있습니다.
4. 로그인은 spring security를 사용하였습니다.
5. DB는 h2를 사용하였습니다.

## 재차 주의점

```shell
org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'jasyptConfig': Injection of
autowired dependencies failed; nested exception is java.lang.IllegalArgumentException: Could not resolve placeholder '
jasypt.encryptor.password' in value "${jasypt.encryptor.password}"
```

위와 같은 에러가 발생하면 실행 환경 설정에 jasypt.encryptor.password=mypass 값을 넣어줘야 합니다.