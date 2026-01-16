package framework.runner.auto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public sealed interface AutoModule permits SingleAutoModule, BatchAutoModule {
  String name();
}