# Java 코딩테스트 Playground Framework

개인 알고리즘 문제 풀이를 **검증 · 실험 · 벤치마크**하기 위한 Java 기반 프레임워크입니다.

이 프로젝트는 다음을 목표로 합니다:

- 각종 코딩테스트 사이트의 문제를 **자동 채점 및 벤치마크**
- 예제 테스트, 랜덤 테스트, 오라클/레퍼런스 테스트, 메타모픽 테스트 등 **다양한 테스트 방법** 제공
- “왜 통과/실패/스킵되었는지”지 로그로 명확히 표시

사용성과 유지보수성을 위해 아래의 요소를 고려했습니다:
- 문제 풀이 코드와 **검증 로직의 명확한 분리**
- OS/IDE에 의존하지 않는 **안정적인 실행**

---

## 프로젝트 구조
```
src/  
├─ framework/ # 테스트/채점/벤치마크 프레임워크  
├─ platforms/ # 문제별 코드 (LeetCode, Programmers 등)  
└─ app/ # 실행 엔트리포인트
```

### framework/
공통 실행/채점/벤치마크 로직을 담은 순수 라이브러리입니다.

- `core` : Solution 실행 모델
- `test` : TestCase / TestSuite / BatchTestSuite
- `judge` : 단일/배치 채점기
- `oracle` : 오라클 기반 검증
- `bench` : 성능 측정 (avg / best / p50 / p95)
- `factory` : 랜덤 / 엣지 / 메타모픽 테스트 생성
- `runner.auto` : 모듈 실행 및 옵션 필터링
- `test.text` : 배열 리터럴 기반 텍스트 파서

> framework는 문제 코드에 의존하지 않습니다.

---

### platforms/
플랫폼별 문제 풀이 코드가 위치합니다.
```
platforms/  
├─ leetcode/        < 각 문제풀이 플랫폼별 폴더 (필수)   
│ └─ Q242/          < 문제별 폴더 (필수)  
│ ㅤ├─ model/       < 문제별 입출력클래스 폴더 (선택)  
│ ㅤ├─ solutions/   < 문제별 솔루션 폴더 (선택)  
│ ㅤ└─ tests/       < 테스트케이스/오라클/레퍼런스 풀이를 모아둔 폴더 (필수)  
│ ㅤㅤ├─ examples/  < 테스트케이스(inputs.txt, outputs.txt) 파일이 위치하는 폴더 (필수)  
│ ㅤㅤ├─ oracle/    < 오라클 클래스 폴더 (선택)  
│ ㅤㅤ└─ reference/ < 레퍼런스 클래스 폴더 (선택)  
└─ programmers/  
ㅤ└─ Q76501/  
```
---

## 실행

```java
// RunAll.java
AutoRunner.runAll(
    List.of(
        new SignedSumModule(),
        new AnagramModule(),
        new AnagramMetamorphicModule()
    ),
    cfg,
    opt
);
```

- Module(=문제) 단위로 실행
- 옵션(RunOptions)에 따라 모듈/스위트/케이스 필터링 가능
- 스킵된 항목은 이유를 로그로 출력