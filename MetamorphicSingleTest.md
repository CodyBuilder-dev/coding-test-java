# 🧬 SingleAutoModule 기반 메타모픽 테스트 가이드

이 문서는 **BatchAutoModule을 쓰지 않고**, 기존 `SingleAutoModule<I,O>` 형태를 유지한 채로  
메타모픽 테스트를 실행하는 방법을 설명합니다.

핵심 아이디어는 2가지 중 하나입니다.

1) **입력 자체를 “메타모픽 입력(원본+변형)”으로 바꾼다**
2) 기존 입력 `I`를 유지하고, **oracle만으로 메타모픽 성질을 검증한다** (제약이 있음)

대부분의 경우 **1번이 가장 현실적이고 깔끔**합니다.

---

## 0) 전제: Single 채점 흐름

Single Judge는 보통 다음 중 하나로 검증합니다.

- `expected`가 있으면 expected 비교
- 없으면 `Reference`로 expected 계산
- 그것도 없으면 `Oracle`로 검증
- 없으면 SKIP/FAIL

Single에서 “메타모픽”을 하려면, 출력 `O`만으로는 부족하고  
**원본/변형 입력 둘 다를 알아야** 하므로, 입력을 감싸는 방식이 가장 일반적입니다.

---

## 1) 방법 : 입력을 `MetaInput<I>`로 감싸서 SingleAutoModule로 실행

### 1.1) 메타모픽 입력 모델 정의

```java
public record MetaInput<I>(I base, I mutated) {}
```
이제 Single 문제의 입력 타입은 I가 아니라 MetaInput<I>가 됩니다.

---
### 1.2) 메타모픽 property 인터페이스 정의
```Java
@FunctionalInterface
public interface MetamorphicProperty<I, O> {
  CheckResult check(I base, I mutated, O baseOut, O mutatedOut);
}
```

### 1.3) 기존 솔루션을 “메타모픽 솔루션”으로 래핑

기존: Solution<I,O>  
메타모픽: Solution<MetaInput<I>, CheckResult> 또는 Solution<MetaInput<I>, Boolean>

권장: CheckResult를 결과로 사용하면 실패 메시지를 그대로 남길 수 있어 디버깅이 편합니다.

```Java
public final class MetaSingleWrappers {
  private MetaSingleWrappers() {}

  public static <I, O> Solution<MetaInput<I>, CheckResult> wrap(
          Solution<I, O> baseSol,
          MetamorphicProperty<I, O> prop
  ) {
    return new Solution<>() {
      @Override public String name() { return baseSol.name() + "/metamorphic"; }

      @Override public CheckResult solve(MetaInput<I> in) {
        O out1 = baseSol.solve(in.base());
        O out2 = baseSol.solve(in.mutated());
        return prop.check(in.base(), in.mutated(), out1, out2);
      }
    };
  }
}
```
---
### 1.4) 메타모픽 테스트 케이스 생성 (expected 없이 oracle만 사용)

메타모픽은 정답이 아니라 “성질”이므로, 보통 expected는 없고 oracle로 PASS/FAIL만 판정합니다.

O = CheckResult인 경우, oracle은 아래처럼 간단해집니다: 

```Java
Oracle<MetaInput<I>, CheckResult> suiteOracle =
        (in, out) -> (out == null) ? CheckResult.fail("null result") : out;
```

```Java
List<TestCase<MetaInput<String>, CheckResult>> cases = new ArrayList<>();
        cases.add(TestCase.oracle(
        new MetaInput<>("aabbcc", shuffle("aabbcc")),
        null, // caseOracle 생략 가능, suiteOracle로 처리
        "meta#0",
        "metamorphic"
));
```
---
### 1.5) SingleAutoModule 메타모픽 모듈 작성

예: 애너그램 문제에서 “문자열을 shuffle해도 결과는 동일해야 한다”를 검증.
```Java
public final class AnagramMetamorphicSingleModule
    implements SingleAutoModule<MetaInput<String>, CheckResult> {

  @Override
  public String name() {
    return "leetcode/Q242/metamorphic_single";
  }

  @Override
  public TestSuite<MetaInput<String>, CheckResult> suite() {
    List<String> seeds = List.of("abc", "aabbcc", "xyzxyz", "hello");

    List<TestCase<MetaInput<String>, CheckResult>> cases = new ArrayList<>();
    for (int i = 0; i < seeds.size(); i++) {
      String base = seeds.get(i);
      String mut = shuffle(base);

      cases.add(TestCase.oracle(
          new MetaInput<>(base, mut),
          null,
          "meta#" + i,
          "metamorphic"
      ));
    }

    Oracle<MetaInput<String>, CheckResult> suiteOracle =
        (in, out) -> (out == null) ? CheckResult.fail("null result") : out;

    return new TestSuite<>("anagram/metamorphic_single", cases, suiteOracle);
  }

  @Override
  public List<?> rawSolutions() {
    // 제출 클래스 목록 (플랫폼 시그니처 그대로)
    var raws = List.of(new SubmissionAnagramSol1(), new SubmissionAnagramSol1_1());

    // (개념 코드) raw -> Solution<String,Boolean> 로 adapt
    List<Solution<String, Boolean>> adapted = adaptAll(raws);

    MetamorphicProperty<String, Boolean> prop =
        (b, m, ob, om) -> ob.equals(om)
            ? CheckResult.pass()
            : CheckResult.fail("shuffle should not change isAnagram result");

    List<Solution<MetaInput<String>, CheckResult>> out = new ArrayList<>();
    for (Solution<String, Boolean> s : adapted) out.add(MetaSingleWrappers.wrap(s, prop));
    return (List) out;
  }

  private static String shuffle(String s) {
    List<Character> ch = new ArrayList<>();
    for (char c : s.toCharArray()) ch.add(c);
    Collections.shuffle(ch, new Random(1));
    StringBuilder sb = new StringBuilder();
    for (char c : ch) sb.append(c);
    return sb.toString();
  }
}
```
> 위 코드의 adaptAll(...)은 프로젝트의 ReflectiveAdapter 등 실제 어댑터 호출로 교체하세요.

---
## 2) 방법 B (제약 있음): 입력은 그대로 두고 “oracle만으로” 메타모픽 검증

이 방법은 Single 실행이 1회만 호출되는 구조라서,  
oracle만으로는 f(x')를 얻을 수 없습니다.

따라서 다음 조건을 만족할 때만 가능합니다.

- 변형 입력 x'에 대한 출력 f(x')가
  - f(x)만으로도 계산되거나,
  - 출력 자체에 변형 결과가 포함되는 문제

예) “정렬 결과” 문제에서
- x' = shuffle(x)
- f(x)는 이미 정렬된 배열이므로
- oracle에서 “정렬된 상태인지”만 확인하면 shuffle property가 사실상 포함됨

하지만 일반적으로는 “같은 솔루션을 두 번 실행”해야 하므로  
대부분 문제에는 방법 A를 추천합니다.

---
## 3) 실행 및 필터링

메타모픽 케이스에는 tag를 붙이는 것을 권장합니다.
- "metamorphic"

### 메타모픽만 실행
```Java
RunOptions opt = RunOptions.builder()
    .includeTags("metamorphic")
    .build();
```

```Java
RunOptions opt = RunOptions.builder()
    .excludeTags("metamorphic")
    .build();
```