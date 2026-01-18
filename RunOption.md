# ⚙️ RunOptions 가이드

`RunOptions`는 실행 시 **어떤 모듈/스위트/솔루션/케이스를 돌릴지**, 그리고 **스킵 사유 로그를 어떻게 남길지**를 제어하는 옵션 모음입니다.

> 아래 문서는 “프레임워크가 이미 AutoRunner + RunOptions를 가지고 있다”는 전제에서,
> 실사용에 필요한 옵션들을 정리한 것입니다.  
> (현재 코드에 없는 필드는 이 스펙대로 추가하면 됩니다.)

---

## 1) 목표

- 특정 모듈만 빠르게 돌리기
- 특정 suite만 골라 돌리기 (예: examples만 / random만)
- 특정 tag가 붙은 테스트만 실행/제외
- 특정 솔루션만 실행 (A/B 비교)
- SKIP이 발생하면 **왜 스킵되었는지** 항상 로그로 남기기

---

---

---

## 2) 권장 RunOptions 형태 (단일/배치 공통)

```java
package framework.runner.auto;

import java.util.Set;
import java.util.function.Predicate;

public final class RunOptions {
  // ✅ module/suite/solution 선택
  public final Set<String> includeModules;   // null or empty -> all
  public final Set<String> excludeModules;

  public final Set<String> includeSuites;    // suite key 기준 (ex: "examples_auto#0")
  public final Set<String> excludeSuites;

  public final Set<String> includeSolutions; // sol.name() 기준
  public final Set<String> excludeSolutions;

  // ✅ testcase tags 필터링 (tag 기반)
  public final Set<String> includeTags;      // tc.tags 중 하나라도 포함하면 통과
  public final Set<String> excludeTags;      // tc.tags에 포함되면 제외

  // ✅ benchmark 관련
  public final boolean enableBenchmark;      // false면 채점만 수행
  public final boolean excludeSkippedFromBenchmark; // true 권장

  // ✅ 로그
  public final boolean logSkips;             // SKIP 사유 출력
  public final boolean logSuiteSummary;      // suite 단위 요약 출력
  public final boolean logCaseFailuresOnly;  // FAIL 케이스만 상세 출력

  // ✅ 고급: 직접 predicate로 case 포함/제외
  public final Predicate<Object> includeCasePredicate; // (TestCase or BatchTestCase)

  // ✅ 생성자 및 빌더(생략)
  private RunOptions(Builder b) {
    // 생략
  }

  public static Builder builder() { return new Builder(); }
}
```
---
## 3) 필터링 규칙 (권장 동작)
✅ module 포함/제외
- includeModules가 비어있으면: 전체 모듈 대상
- includeModules가 있으면: 해당 이름만 실행
- excludeModules는 항상 마지막에 적용

✅ suite 포함/제외
- suite key는 module.suites()의 key (예: examples_auto#0)
- includeSuites가 있으면 그 key만 실행
- excludeSuites는 마지막에 적용

✅ solution 포함/제외
- solution name은 보통 Solution.name()
- includeSolutions가 있으면 지정한 솔루션만 실행

✅ tag 기반 케이스 필터
- includeTags가 비어있으면: tag 기준 필터링 안 함
- includeTags가 있으면: tc.tags에 includeTags 중 하나라도 있으면 포함
- excludeTags는 항상 마지막에 적용
---
## 4) SKIP 로그 권장 포맷
옵션/필터로 인해 생략되는 경우도 로그로 남기면 헷갈리지 않습니다.

권장 포맷:
- 모듈 스킵:
```css
[SKIP][MODULE] programmers/Q76501 : not in includeModules
```

- 스위트 스킵:
```css
[SKIP][SUITE ] examples_auto#1 : excluded by excludeSuites
```

- 솔루션 스킵:
```css
[SKIP][SOL   ] SubmissionSol2 : not in includeSolutions
```

- 케이스 스킵(태그/프레디케이트):
```css
[SKIP][CASE  ] examples_auto#0::3 : excluded by tag (random)
```

## 5) 실사용 예시
### 예시 A) 특정 모듈만 실행
```Java
RunOptions opt = RunOptions.builder()
  .includeModules("programmers/Q76501")
  .build();
```
### 예시 B) examples suite만 실행

```Java
RunOptions opt = RunOptions.builder()
  .includeSuites("examples_auto#0", "examples_auto#1")
  .build();
```

### 예시 C) 특정 솔루션만 실행 (A/B 비교)
```Java
RunOptions opt = RunOptions.builder()
  .includeSolutions("SubmissionSol1")
  .build();
```
### 예시 D) random 케이스는 제외하고 실행
```Java
RunOptions opt = RunOptions.builder()
  .excludeTags("random")
  .build();
```
### 예시 E) 벤치마크는 끄고 채점만
```Java
RunOptions opt = RunOptions.builder()
  .enableBenchmark(false)
  .build();
```

### 예시 F) 실패 케이스만 상세 출력
```Java
RunOptions opt = RunOptions.builder()
.logCaseFailuresOnly(true)
.build();
```
---
##6) AutoRunner에 적용 위치(개념)

runAll() 내부에서 아래 흐름으로 적용하는 것을 권장합니다.
1. 모듈 필터 
2. suites 로딩 (+ auto examples 주입)
3. suite 필터 
4. 솔루션 필터 
5. 케이스 필터 (tags / predicate)
6. 채점 실행
7. (enableBenchmark면) 벤치 실행 (skip 제외 옵션 적용)

## 7) 기본 권장값

- enableBenchmark = true
- excludeSkippedFromBenchmark = true
- logSkips = true
- logSuiteSummary = true
- logCaseFailuresOnly = false

> “안 돌린 이유가 로그로 남는다”가 디버깅 시간을 크게 줄여줍니다.