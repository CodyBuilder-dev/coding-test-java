package framework.io;

import java.nio.file.Files;
import java.nio.file.Path;

public final class ProjectPaths {
  private ProjectPaths() {}

  /** 실행 위치(workdir)에서 시작해 상위로 올라가며 project root를 탐색 */
  public static Path projectRoot() {
    Path cur = Path.of("").toAbsolutePath().normalize();

    for (int i = 0; i < 12; i++) { // 깊이 제한(너무 오래 뒤지는 것 방지)
      if (Files.isDirectory(cur.resolve("src"))) {
        return cur;
      }
      Path parent = cur.getParent();
      if (parent == null) break;
      cur = parent;
    }

    // 마지막 fallback: 현재 디렉토리
    return Path.of("").toAbsolutePath().normalize();
  }
}