README파일을 이용하여 Swagger 주소 및 요구 사항 구현 여부, 구현 방법, 그리고
검증 결과에 대해 작성합니다.

실행 방법
환경설정 값에
jasypt.encryptor.password=mypass를 넣고 실행합니다.
또는 jar 실행시
-Djasypt.encryptor.password=mypass
를 넣고 실행합니다.

주의점
org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'jasyptConfig': Injection of
autowired dependencies failed; nested exception is java.lang.IllegalArgumentException: Could not resolve placeholder '
jasypt.encryptor.password' in value "${jasypt.encryptor.password}"
위와 같은 에러가 발생하면 실행 환경 설정에 jasypt.encryptor.password=mypass 값을 넣어줘야 합니다.