#### 문서 분류 서비스:
* 모듈
  * gateway
    * MSA 포워딩, 로드밸런싱을 담당하는 게이트웨이 서비스
  * modeler
    * 샘플 문서를 업로드하여 분류 모델을 생성하는 서비스
  * modeler-ui
    * modeler user interface 구현
  * tester
    * 생성된 모델을 사용하여 문서를 분류하는 서비스
  * tester-ui
    * tester user interface 구현
  * docker-compose.yml
    * Cassandra
      * 생성된 모델, 업로드한 샘플 문서를 저장하는 데이터베이스
      * 3중화 구성
    * Zookeeper
      * MSA 디스커버리 서비스