# 🧬 BatchAutoModule 기반 메타모픽 테스트 가이드
이 문서는 BatchAutoModule을 사용해 
메타모픽 테스트를 실행하는 방법을 설명합니다.

---
## 1) 구현 방법: 메타모픽 전용 BatchTestSuite 생성

메타모픽 테스트는 보통 “원본/변형”을 한 번에 실행해야 하므로,
단일 호출보다 **배치 형태**로 만드는 게 간단합니다.

### 1.1) 입력 모델 정의

예: 원본과 변형을 한 번에 들고 다니는 EI 타입을 만든다.

```java
public record MetaPair<I>(I base, I mutated) {}
```

### 1.2) Batch 입력(BI) 구성 규칙

BatchJudge.runOne()을 보면 batchInput은 다음처럼 만들어집니다:
- suite.cases의 tc.input들을 모아서 List<EI>
- suite.batchInputBuilder.apply(List<EI>)로 BI 생성
- sol.solve(BI) 1회 호출
- splitter.apply(BO)로 EO 리스트 획득 후 케이스별 채점

따라서 메타모픽 테스트에서는:
- EI = MetaPair<I>
- EO = CheckResult (또는 Boolean) 같은 “property 검증 결과”
- BI = MetaPair<I>[] (혹은 List)

로 구성하면 간단합니다.


### 1.3) 메타모픽 suite 생성
```Java
public final class AnagramMetamorphicSuites {
  private AnagramMetamorphicSuites() {}

  public static BatchTestSuite<MetaPair<String>, Boolean, MetaPair<String>[]> suite(
      List<String> seeds
  ) {
    List<BatchTestCase<MetaPair<String>, Boolean>> cases = new ArrayList<>();
    for (int i = 0; i < seeds.size(); i++) {
      String base = seeds.get(i);
      String mut = shuffle(base); // 변형
      cases.add(BatchTestCase.oracle(
          new MetaPair<>(base, mut),
          (in, out) -> out ? CheckResult.pass() : CheckResult.fail("property violated"),
          "meta#" + i,
          "metamorphic"
      ));
    }

    return BatchTestSuite.of(
        "anagram/metamorphic",
        cases,
        list -> list.toArray(new MetaPair[0]),
        null
    );
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
> 위 예시는 “oracle이 out(Boolean)만 보고 PASS/FAIL”하는 형태로 단순화한 버전입니다.

### 1.4) 메타모픽 솔루션 래핑

메타모픽의 핵심은 “base와 mutated를 둘 다 풀어보고 property 검사”입니다.

기존 단일 솔루션이 Solution<I,O>라면,  
메타모픽용 솔루션은 Solution<MetaPair<I>[], List<O>> 처럼 만들거나,  
(권장) Solution<MetaPair<I>[], O[]>로 만들고 splitter로 분해합니다.

예시(개념):
```Java
public final class MetaSolutions {
  private MetaSolutions() {}

  public static <I,O> Solution<MetaPair<I>[], Boolean[]> wrapTwice(
      Solution<I,O> baseSol,
      MetamorphicProperty<I,O> prop
  ) {
    return new Solution<>() {
      @Override public String name() { return baseSol.name() + "/metamorphic"; }

      @Override public Boolean[] solve(MetaPair<I>[] pairs) {
        Boolean[] res = new Boolean[pairs.length];
        for (int i = 0; i < pairs.length; i++) {
          MetaPair<I> p = pairs[i];
          O out1 = baseSol.solve(p.base());
          O out2 = baseSol.solve(p.mutated());
          res[i] = prop.check(p.base(), p.mutated(), out1, out2).ok();
        }
        return res;
      }
    };
  }

  @FunctionalInterface
  public interface MetamorphicProperty<I,O> {
    CheckResult check(I base, I mutated, O baseOut, O mutOut);
  }
}
```
이제 BatchJudge에서:
- EO = Boolean
- actual = Boolean
- caseOracle로 out(true/false) 검증 또는 expected=true로 검증 가능

### 1.5) 메타모픽 Module 작성
```Java
public final class AnagramMetamorphicModule
    implements BatchAutoModule<MetaPair<String>, Boolean, MetaPair<String>[], Boolean[]> {

  @Override
  public String name() { return "leetcode/Q242/metamorphic"; }

  @Override
  public Map<String, BatchTestSuite<MetaPair<String>, Boolean, MetaPair<String>[]>> suites() {
    List<String> seeds = List.of("abc", "aabbcc", "xyzxyz", "hello");
    return Map.of("meta", AnagramMetamorphicSuites.suite(seeds));
  }

  @Override
  public List<?> rawSolutions() {
    // raw 제출 클래스 -> ReflectiveAdapter로 Solution<I,O>로 변환한다고 가정
    var base = List.of(new SubmissionAnagramSol1(), new SubmissionAnagramSol1_1());
    // base 솔루션을 프레임워크 방식으로 adapt한 다음 wrapTwice 적용
    // (아래는 개념 코드: 실제 프로젝트의 adapter 호출에 맞게 수정)
    List<Solution<String, Boolean>> adapted = adaptAll(base);
    var prop = (MetaSolutions.MetamorphicProperty<String, Boolean>)
        (b, m, ob, om) -> (ob.equals(om))
            ? CheckResult.pass()
            : CheckResult.fail("shuffle should not change result");

    List<Solution<MetaPair<String>[], Boolean[]>> out = new ArrayList<>();
    for (Solution<String, Boolean> s : adapted) out.add(MetaSolutions.wrapTwice(s, prop));
    return (List) out;
  }
}

```
---
## 2) 구현 방법 B (더 쉬움):“기존 suite를 변형해서 메타모픽 suite 만들기”

이미 examples/random suite가 있고 “입력만 변형해서 property 체크”를 하고 싶다면:
- 기존 suite에서 EI를 뽑는다
- mutate해서 MetaPair<EI>를 만든다
- 메타모픽 suite로 재조립한다

장점:
- 별도 seed 준비 필요 없음
- 이미 있는 케이스 기반으로 변형 가능
---
## 3) 태그/옵션으로 메타모픽만 실행하기
메타모픽 케이스에는 tag를 붙이는 것을 추천합니다.
- "metamorphic"

RunOptions 예시:
```Java
RunOptions opt = RunOptions.builder()
    .includeTags("metamorphic")
    .build();
```

또는 반대로 메타모픽을 제외:
```Java
RunOptions opt = RunOptions.builder()
    .excludeTags("metamorphic")
    .build();

```