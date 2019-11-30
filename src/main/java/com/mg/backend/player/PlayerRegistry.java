package com.mg.backend.player;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.mg.backend.Command;
import com.mg.backend.player.commands.CreatePlayer;
import com.mg.backend.player.commands.DeletePlayer;
import com.mg.backend.player.commands.GetPlayer;
import com.mg.backend.player.commands.GetPlayers;

public class PlayerRegistry extends AbstractBehavior<Command> {
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

    private Behavior<Command> onGetPlayers(GetPlayers command) {
        return this;
    }

    private Behavior<Command> onCreatePlayer(CreatePlayer command) {
        return this;
    }

    private Behavior<Command> onGetPlayer(GetPlayer command) {
        return this;
    }

    private Behavior<Command> onDeletePlayer(DeletePlayer command) {
        return this;
    }
}
