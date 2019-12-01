package com.mg.backend.player.commands;

import akka.actor.typed.ActorRef;
import com.mg.backend.Command;
import com.mg.backend.player.PlayerRegistry;

public class FindByClub implements Command {

  public final String query;
  public final ActorRef<PlayerRegistry.ActionPerformed> replyTo;

  public FindByClub(String query, ActorRef<PlayerRegistry.ActionPerformed> replyTo) {
    this.query = query;
    this.replyTo = replyTo;
  }

}
