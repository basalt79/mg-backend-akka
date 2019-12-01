package com.mg.backend.player;

import java.util.List;

public class Players {

  public List<Player> getPlayers() {
    return players;
  }

  private final List<Player> players;

    public Players(List<Player> users) {
        this.players = users;
    }
}
