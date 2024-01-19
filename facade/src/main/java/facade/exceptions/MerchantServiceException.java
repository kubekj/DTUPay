package facade.exceptions;

/*
  ##############################################################################
  # Developed by Andreas (s176334) and Christian (s194578) as pair programming #
  ##############################################################################
*/
public class MerchantServiceException extends Exception {

    public MerchantServiceException() {
    }

    public MerchantServiceException(String string) {
        super(string);
    }
}