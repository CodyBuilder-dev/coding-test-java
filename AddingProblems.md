
# 🧩 새 문제 추가 가이드

이 프레임워크에서 **새 알고리즘 문제를 추가하는 최소 절차**를 설명합니다.
파일만 제대로 배치하면, 테스트 로딩과 실행은 프레임워크가 자동으로 처리합니다.

---

## 1️⃣ 문제 디렉토리 생성

플랫폼과 문제 번호 기준으로 폴더를 만듭니다.

예시: Programmers 76501번 문제

```
platforms/programmers/Q76501/
├─ model/
├─ solutions/
└─ tests/
ㅤ└─ examples/
```

---

## 2️⃣ 입력 타입 정의 (`model/`)

입력이 여러 개라면 `record`로 정의합니다.

```java
package platforms.programmers.Q76501.model;

public record SignedSumInput(int[] absolutes, boolean[] signs) {}
```
- **단일 입력**이거나 Java 기본 데이터타입이면, **record 없이 바로 사용**해도 됩니다.
- **복수 입력**일 경우, **반드시 Input클래스를 생성해줘야 실행이 가능**합니다.
- Batch 문제의 경우에도 입력 원소 타입(EI) 기준으로 정의합니다.


## 3️⃣ 풀이 코드 작성 (solutions/)

플랫폼 요구 시그니처를 그대로 유지합니다.  
클래스 이름은 자유이며, 채점할 메서드는 @SubmitMethod로 지정합니다.

```Java
package platforms.programmers.Q76501.solutions;

import framework.runner.auto.SubmitMethod;

@SubmitMethod("solution")
public class SubmissionSol1 {
  public int solution(int[] absolutes, boolean[] signs) {
    int sum = 0;
    for (int i = 0; i < absolutes.length; i++) {
      sum += signs[i] ? absolutes[i] : -absolutes[i];
    }
    return sum;
  }
}
```
- 채점 메서드 외 내부 메서드 작성은 자유롭게 가능합니다
- 여러 풀이가 있다면 클래스만 추가하면 됩니다.

## 4️⃣ 테스트케이스 입력 작성 (파일만 추가)
- 문자열은 ""로 감쌀 것
- 배열 리터럴 문법 사용

### 📄 tests/examples/inputs.txt


- 단일 입력 문제 (TC 1개에 한 줄) 
  - solution(String s)인 경우
    ```
    "true"
    "false"
    "error"
    ```
  - solution(String[][] s)인 경우
    ```
    [["RGBYDW","GBRDWY"],["YBGDRW","GBDYWR"]]
    [["RGBYDW","RGBYDW"],["RGBYDW","RGBYWD"]]
    ```

- 복수 입력 문제 (TC 1개가 여러 줄)
  - solution(String s, String t)인 경우
    ```
    "anagram"
    "nagaram"
    "rat"
    "car"
    ```
  - solution(int[] i, boolean[] b)인 경우
    ```
    [4,7,12]
    [true,false,true]
    [1,2,3]
    [false,false,true]
    ```
  
### 📄 tests/examples/outputs.txt
- 단일 입력 문제
  - int solution(String s) 
    ```
    1
    0
    -1
    ```
  - int[] solution(String[][] s)
    ```
    [1,1]
    [1,0]
    ```   
- 복수 입력 문제 
  - booelan(solution String s, String t)
    ```
    true
    false
    ```
  - int solution(int[] i, boolean[] b)  
    ```
    9
    0
    ```

⚠️ 파일이 없거나 파싱에 실패하면 **즉시 에러(fail-fast)** 가 발생합니다.

## 5️⃣ (선택) Oracle / Reference 추가
### Oracle (불변식 검증)
```Java
package platforms.programmers.Q76501.tests.oracle;

import framework.oracle.*;

public final class Oracles {
  public static Oracle<SignedSumInput, Integer> nonNull() {
    return (in, out) ->
        out == null ? CheckResult.fail("null output") : CheckResult.pass();
  }
}
```

### Reference (채점용 정답)
```Java
package platforms.programmers.Q76501.tests.reference;

import framework.oracle.Reference;

public final class ReferenceImpl implements Reference<SignedSumInput, Integer> {
  @Override
  public Integer computeExpected(SignedSumInput in) {
    int sum = 0;
    for (int i = 0; i < in.absolutes().length; i++) {
      sum += in.signs()[i] ? in.absolutes()[i] : -in.absolutes()[i];
    }
    return sum;
  }
}
```
- **Oracle**은 해당 정답이 갖는 제약조건(null, 정수 범위 등)의 구현체입니다.
- **Reference**는 느리지만 확실하게 정답인 풀이의 구현체입니다.
- Oracle/Reference는 **테스트케이스가 없는 테스트를 검증**할 때 사용됩니다.
- Oracle/Reference 추가 없이도 **테스트케이스가 정상이면 채점은 가능**합니다.

## 6️⃣ Module 작성 (가장 중요)
### 단일 문제 (Single)

```Java
package platforms.programmers.Q76501;

import framework.runner.auto.*;
import platforms.programmers.Q76501.model.SignedSumInput;

@AutoExamples(
    input = SignedSumInput.class,
    output = Integer.class
)
public final class SignedSumModule implements SingleAutoModule<SignedSumInput, Integer> {

  @Override
  public String name() {
    return "programmers/Q76501";
  }

  @Override
  public List<?> rawSolutions() {
    return List.of(
        new SubmissionSol1()
    );
  }
}
```
- @AutoExamples를 붙이면 tests/examples/inputs.txt, outputs.txt가 자동 로드됩니다.
- 파일 로딩 코드를 직접 작성할 필요가 없습니다.

### 배치 문제 (Batch)
```Java
@AutoBatchExamples(
    bi = String[][].class,
    eoArray = int[].class
)
public final class DiceModule implements BatchAutoModule<...> {
    ...
}
```
- 입력 한 줄 = 배치 입력(BI)
- 출력 한 줄 = 요소별 기대값 배열(EO[])
- 자동으로 BatchTestSuite 생성

## 7️⃣ 실행 목록에 추가
```Java
// RunAll.java
AutoRunner.runAll(
    List.of(
        new SignedSumModule()
    ),
    cfg,
    opt
);
```

이제 RunAll.main 메서드를 실행하면:
- 예제 테스트가 자동 로드되고
- 채점 / 벤치마크 수행
- 정답/오답/스킵된 경우 각각에 대해 로그가 출력됩니다

## ✅ 체크리스트
- [ ] 문제 폴더에 풀이 코드 작성 및 @SubmitMethod 적용
- [ ] tests/examples 경로에 inputs.txt, outputs.txt 추가
- [ ] Module작성 및 @AutoExamples 또는 @AutoBatchExamples 적용
- [ ] RunAll에 Module 등록

이것만 하면 새 문제 추가 완료입니다. 🎉