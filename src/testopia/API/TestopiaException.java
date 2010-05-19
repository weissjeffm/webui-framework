package testopia.API;

public class TestopiaException extends RuntimeException {
	
	public TestopiaException(String message) {
		super(message);
	}
	public TestopiaException(Throwable e) {
		super(e);
	}
	public TestopiaException(String message, Throwable e) {
		super(message, e);
	}

	

}
