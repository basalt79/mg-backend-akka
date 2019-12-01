package com.mg.backend.player.commands;

import akka.actor.typed.ActorRef;
import com.mg.backend.Command;
import com.mg.backend.player.Player;
import com.mg.backend.player.PlayerRegistry;

public class CreatePlayer implements Command {
  public final Player player;
  public final ActorRef<PlayerRegistry.ActionPerformed> replyTo;

  public CreatePlayer(Player player, ActorRef<PlayerRegistry.ActionPerformed> replyTo) {
    this.player = player;
    this.replyTo = replyTo;
  }

}


