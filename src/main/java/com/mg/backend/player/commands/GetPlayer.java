package com.mg.backend.player.commands;

import akka.actor.typed.ActorRef;
import com.mg.backend.Command;

public class GetPlayer implements Command {

  public final String name;
  public final ActorRef<GetPlayerResponse> replyTo;

  public GetPlayer(String name, ActorRef<GetPlayerResponse> replyTo) {
    this.name = name;
    this.replyTo = replyTo;
  }

}
