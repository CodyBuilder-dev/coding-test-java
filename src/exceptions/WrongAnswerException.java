package exceptions;

public class WrongAnswerException extends RuntimeException{

  public WrongAnswerException() {
    super("오답입니다.");
  }

  public WrongAnswerException(String message) {
    super(message);
  }
}
