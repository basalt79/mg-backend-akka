package com.mg.backend.player.commands;

import com.mg.backend.player.Player;

import java.util.Optional;

public class GetPlayerResponse {

  public final Optional<Player> maybePlayer;

  public GetPlayerResponse(Optional<Player> maybeUser) {
    this.maybePlayer = maybeUser;
  }

}
