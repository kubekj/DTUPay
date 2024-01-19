package token.service;

import lombok.Getter;
/*
  ##############################################################################
  # Developed by Andreas (s176334) and Christian (s194578) as pair programming #
  ##############################################################################
*/
public class User {
    @Getter
    private final String id;

    public User(String id) {
        this.id = id;
    }
}
