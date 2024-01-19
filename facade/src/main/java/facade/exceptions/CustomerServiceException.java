package facade.exceptions;

/*
  ##############################################################################
  # Developed by Andreas (s176334) and Christian (s194578) as pair programming #
  ##############################################################################
*/
public class CustomerServiceException extends Exception {

    public CustomerServiceException() {
    }

    public CustomerServiceException(String string) {
        super(string);
    }
}