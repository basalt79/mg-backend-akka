package com.mg.backend.player.commands;

import akka.actor.typed.ActorRef;
import com.mg.backend.Command;
import com.mg.backend.player.PlayerRegistry;

public class DeletePlayer implements Command {

  public final String name;
  public final ActorRef<PlayerRegistry.ActionPerformed> replyTo;

  public DeletePlayer(String name, ActorRef<PlayerRegistry.ActionPerformed> replyTo) {
    this.name = name;
    this.replyTo = replyTo;
  }

}
