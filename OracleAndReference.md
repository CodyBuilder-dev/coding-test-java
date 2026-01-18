# 🧪 Oracle / Reference 적용 가이드

이 문서는 “프레임워크를 개발하는 입장”이 아니라, **실제로 문제를 푸는 입장(문제 패키지 작성자)** 에서  
내 풀이를 더 안전하게 검증하기 위해 **Oracle / Reference를 어떻게 붙여 쓰는지**를 설명합니다.

> 목표: “예제(expected) 몇 개만으로는 불안한데,  
> 랜덤/엣지 케이스까지 돌리면서도 믿을 수 있게 검증하고 싶다.”

---

## 0) 결론부터: 언제 뭘 쓰면 되나?

- **Reference(정답 생성기)**  
  ✅ “정답을 구하는 느린 풀이를 만들 수 있을 때”  
  → 랜덤 테스트를 마음껏 만들어도 자동으로 expected가 생김.

- **Oracle(불변식 검증)**  
  ✅ “정답은 모르겠는데, 맞으면 반드시 성립해야 하는 조건은 알 때”  
  → expected 없이도 실패를 잡아냄. (특히 메타모픽에 강함)

- 둘 다 가능하면  
  ✅ Reference로 채점 + Oracle로 추가 안전장치 (추천)

---

## 1) 최소 준비물(문제 폴더에서 내가 만드는 것)

보통 문제 하나에 대해 아래 3개만 만들면 된다.

1) **내 제출 풀이(솔루션)**: `solutions/SubmissionSol1.java`
2) **Module**: 어떤 테스트/솔루션을 돌릴지 모아주는 클래스
3) (선택) **tests/oracle** 또는 **tests/reference**: 검증 도구

```
platforms/<site>/<Qxxxx>/
├─ model/
├─ solutions/
├─ tests/
│ ├─ examples/ # inputs.txt / outputs.txt (있으면)
│ ├─ oracle/ # Oracle 모음 (선택)
│ └─ reference/ # Reference 구현 (선택)
└─ <Qxxxx>Module.java # Module
```

---

## 2) Reference 적용하기 (가장 강력한 방법)

### 2.1 언제 Reference를 쓰나?
- 랜덤 테스트를 많이 만들고 싶은데 expected를 손으로 못 쓰겠음
- O(n log n)이나 브루트포스 같은 느린 정답을 만들 수 있음

### 2.2 Reference 작성 방법(내 문제 폴더에 추가)

예: Programmers 76501 (음양 더하기)

`platforms/programmers/Q76501/tests/reference/SignedSumReference.java`

```java
package platforms.programmers.Q76501.tests.reference;

import framework.oracle.Reference;
import platforms.programmers.Q76501.model.SignedSumInput;

public final class SignedSumReference implements Reference<SignedSumInput, Integer> {
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

### 2.3 Module에 Reference 연결(내가 하는 일)

Module에서 “내 문제의 Reference는 이거야”를 반환하도록 하면 끝.

`platforms/programmers/Q76501/SignedSumModule.java`
```java
@Override
public Reference<SignedSumInput, Integer> reference() {
  return new SignedSumReference();
}
```
> 이렇게 해두면, expected가 없는 랜덤 케이스도 자동으로 expected가 계산되어 채점됩니다.

## 3) Oracle 적용하기 (expected 없이도 잡아내는 안전장치)
### 3.1 언제 Oracle을 쓰나?

- Reference를 만들기 어렵다
- 정답은 몰라도 “맞으면 반드시 만족해야 하는 조건”은 있다
- 출력이 너무 커서 expected를 만들기 힘들다
- 메타모픽(입력 변형 후 성질 유지) 검증을 하고 싶다

### 3.2 Oracle 작성 방법(내 문제 폴더에 추가)

예: “출력은 절대 null이면 안 됨” 같은 기본 검증

`platforms/programmers/Q76501/tests/oracle/SignedSumOracles.java`
```java
package platforms.programmers.Q76501.tests.oracle;

import framework.oracle.*;

import platforms.programmers.Q76501.model.SignedSumInput;

public final class SignedSumOracles {
  private SignedSumOracles() {}

  public static Oracle<SignedSumInput, Integer> notNull() {
    return (in, out) -> (out == null)
        ? CheckResult.fail("output is null")
        : CheckResult.pass();
  }
}
```
### 3.3 Module에 Oracle 연결(내가 하는 일)

보통은 suite 전체에 공통으로 적용하는 게 편함.

`platforms/programmers/Q76501/SignedSumModule.java`
```java
@Override
public Oracle<SignedSumInput, Integer> suiteOracle() {
  return SignedSumOracles.notNull();
}
```
> 이제 expected가 없어도, 최소한 “null 출력” 같은 명백한 버그는 잡힙니다.


## 4) “케이스 단위 Oracle”은 언제 쓰나?

케이스 단위 oracle은 “특정 케이스만” 더 강하게 검증하고 싶을 때 쓴다.

예:
- 특정 입력에서만 특별한 조건을 검증하고 싶다
- 랜덤 생성 케이스 중 일부만 “추가 확인”을 걸고 싶다
- 디버깅 중 특정 케이스에서만 로그/검증을 강화하고 싶다

### 예시: 특정 케이스만 더 엄격히 체크
```java
TestCase.expectAndOracle(
    input,
    expected,
    (in, out) -> (out >= 0) ? CheckResult.pass() : CheckResult.fail("must be non-negative"),
    "special-case",
    "examples", "strict"
);
```
> 보통은 suiteOracle 하나로 충분하고,  
케이스 단위 oracle은 “특정 케이스만 예외적으로” 쓰는 게 깔끔합니다.

## 5) Reference + Oracle 같이 쓰는 추천 조합
### 왜 같이 쓰나?
- Reference는 “정답 일치”를 보장하지만  
Reference 자체가 버그일 가능성도 아주 낮지만 0은 아님
- Oracle은 “정답과 무관한 성질”로 한 번 더 걸러줌

### 실전 권장

- examples: expected
- random: expected 없음 → Reference로 expected 계산
- 전 케이스 공통: suiteOracle로 기본 불변식 체크

## 6) Batch 문제에서 Reference / Oracle 적용 (문제 푸는 사람 입장)
Batch도 원리는 동일하지만 단위가 “element”입니다.
- Reference 타입: Reference<EI, EO>
- Oracle 타입: Oracle<EI, EO>

즉, Batch 전체가 아니라 **원소 케이스(EI → EO)** 를 기준으로 작성합니다.

예: 주사위 문제에서 EI가 String[](두 주사위), EO가 Integer(0/1)라면

```java
public final class DiceReference implements Reference<String[], Integer> {
  @Override public Integer computeExpected(String[] in) { ... }
}
```

그리고 Module에서 reference()로 반환하면,
배치 케이스 중 expected가 비어도 element 단위로 자동 채점됩니다.

## 7) 자주 쓰는 Oracle 아이디어 모음(문제 푸는 사람용)

- null 금지: 출력이 null이면 무조건 FAIL 
- 범위 체크: 결과가 특정 범위 내여야 함 
- 길이 체크: 출력 배열 길이가 입력과 일치해야 함 
- 정렬 문제: 결과가 정렬돼 있어야 함 
- 집합 문제: 결과 원소가 입력 원소의 부분집합이어야 함 
- 문자열 문제: 결과가 입력에서 허용되는 문자만 포함해야 함 
- 메타모픽: 입력을 변형해도 결과 성질이 유지돼야 함 (별도 가이드 참고)

## 8) 체크리스트(내가 문제 풀 때 실제로 하는 순서)

1. 예제 몇 개만으로 불안하다 
2. 랜덤 테스트를 만들고 싶다 
3. 정답 느린 버전이 가능하면 → **Reference** 추가 
4. 정답 만들기 어렵다면 → **Oracle** 추가
5. 둘 다 가능하면 → **둘 다 적용**
6. RunOptions로 `random/examples/metamorphic` 태그를 켜고 끄며 검증

## 9) 흔한 실수

- Reference를 만들었는데 Module에 연결을 안 해서 “expected가 null이라 SKIP” 나는 경우
- Oracle이 너무 약해서 “틀린데도 PASS”가 되는 경우  
→ 여러 oracle을 조합하거나(Oracles.all) property를 강화
- 랜덤 케이스를 너무 크게 만들어 Reference가 너무 느려지는 경우  
→ 랜덤 크기를 줄이거나, Reference는 소형 입력만 검증하도록 tag로 분리