package exceptions;

import lombok.EqualsAndHashCode;

/*
  ################################
  # Developed by Jakub (s232946) #
  ################################
*/
@EqualsAndHashCode(callSuper = false)
public class ReportCreationException extends Exception {
    public ReportCreationException() {
        super("There are no transactions to report");
    }
}
