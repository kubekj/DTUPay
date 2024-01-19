package exceptions;

/*
  #############################################################################
  # Developed by Andreas (s176334) and Christian (s194578) as pair programing #
  #############################################################################
*/
public class CustomerServiceException extends Exception {

    public CustomerServiceException() {
    }

    public CustomerServiceException(String string) {
        super(string);
    }
}