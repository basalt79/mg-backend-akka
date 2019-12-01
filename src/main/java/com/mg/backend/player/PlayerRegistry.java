package com.mg.backend.player;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.mg.backend.Command;
import com.mg.backend.player.commands.CreatePlayer;
import com.mg.backend.player.commands.DeletePlayer;
import com.mg.backend.player.commands.FindByClub;
import com.mg.backend.player.commands.GetPlayer;
import com.mg.backend.player.commands.GetPlayerResponse;
import com.mg.backend.player.commands.GetPlayers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PlayerRegistry extends AbstractBehavior<Command> {

  private final List<Player> player = new ArrayList<>();

  private PlayerRegistry(ActorContext<Command> context) {
    super(context);
  }

  public static Behavior<Command> create() {
    return Behaviors.setup(PlayerRegistry::new);
  }

  @Override
  public Receive<Command> createReceive() {
    return newReceiveBuilder()
      .onMessage(GetPlayers.class, this::onGetPlayers)
      .onMessage(CreatePlayer.class, this::onCreatePlayer)
      .onMessage(GetPlayer.class, this::onGetPlayer)
      .onMessage(DeletePlayer.class, this::onDeletePlayer)
      .build();
  }

  public final static class ActionPerformed implements Command {
    public final String description;
    public ActionPerformed(String description) {
      this.description = description;
    }
  }

  private Behavior<Command> onGetPlayers(GetPlayers command) {
    command.replyTo.tell(new Players(Collections.unmodifiableList(new ArrayList<>(player))));
    return this;
  }

  private Behavior<Command> onCreatePlayer(CreatePlayer command) {
    player.add(command.player);
    command.replyTo.tell(new ActionPerformed(String.format("User %s created.", command.player.getFirstName())));
    return this;
  }

  private Behavior<Command> onGetPlayer(GetPlayer command) {
    Optional<Player> maybePlayer = player.stream()
      .filter(user -> user.getFirstName().equals(command.name))
      .findFirst();
    command.replyTo.tell(new GetPlayerResponse(maybePlayer));
    return this;
  }

  private Behavior<Command> onDeletePlayer(DeletePlayer command) {
    player.removeIf(player -> player.getId().equals(command.name));
    command.replyTo.tell(new ActionPerformed(String.format("User %s deleted.", command.name)));
    return this;
  }

  private Behavior<Command> onFindByClub(FindByClub command) {
    player.removeIf(player -> player.getClub().equals(command.query));
    command.replyTo.tell(new ActionPerformed(String.format("Club Search %s.", command.query)));
    return this;
  }
}
