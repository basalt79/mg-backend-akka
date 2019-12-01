package com.mg.backend.player.commands;

import akka.actor.typed.ActorRef;
import com.mg.backend.Command;
import com.mg.backend.player.Players;

public class GetPlayers implements Command {

  public final ActorRef<Players> replyTo;

  public GetPlayers(ActorRef<Players> replyTo) {
    this.replyTo = replyTo;
  }
}

