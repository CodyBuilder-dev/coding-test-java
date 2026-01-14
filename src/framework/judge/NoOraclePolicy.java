package framework.judge;

public enum NoOraclePolicy {
  SKIP,   // expected도 없고 oracle도 없으면 SKIP
  FAIL    // expected도 없고 oracle도 없으면 FAIL
}