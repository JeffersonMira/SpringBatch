package jeff.learning.batch.errorhandling.Skip.components;

public class CustomRetryableException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public CustomRetryableException() {
		super();
	}

	
	public CustomRetryableException(String message){
		super(message);
	}
}
