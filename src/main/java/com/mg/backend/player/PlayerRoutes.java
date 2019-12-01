package com.mg.backend.player;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Scheduler;
import akka.actor.typed.javadsl.AskPattern;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.Directives;
import akka.http.javadsl.server.PathMatchers;
import akka.http.javadsl.server.Route;
import com.mg.backend.Command;
import com.mg.backend.player.commands.CreatePlayer;
import com.mg.backend.player.commands.DeletePlayer;
import com.mg.backend.player.commands.FindByClub;
import com.mg.backend.player.commands.GetPlayer;
import com.mg.backend.player.commands.GetPlayerResponse;
import com.mg.backend.player.commands.GetPlayers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.server.Directives.complete;
import static akka.http.javadsl.server.Directives.concat;
import static akka.http.javadsl.server.Directives.delete;
import static akka.http.javadsl.server.Directives.entity;
import static akka.http.javadsl.server.Directives.get;
import static akka.http.javadsl.server.Directives.onSuccess;
import static akka.http.javadsl.server.Directives.parameter;
import static akka.http.javadsl.server.Directives.path;
import static akka.http.javadsl.server.Directives.pathEnd;
import static akka.http.javadsl.server.Directives.pathPrefix;
import static akka.http.javadsl.server.Directives.post;
import static akka.http.javadsl.server.Directives.rejectEmptyResponse;

/**
 * Routes can be defined in separated classes like shown in here
 */
//#user-routes-class
public class PlayerRoutes {
  //#user-routes-class
  private final static Logger log = LoggerFactory.getLogger(PlayerRoutes.class);
  private final ActorRef<Command> userRegistryActor;
  private final Duration askTimeout;
  private final Scheduler scheduler;

  public PlayerRoutes(ActorSystem<?> system, ActorRef<Command> userRegistryActor) {
    this.userRegistryActor = userRegistryActor;
    scheduler = system.scheduler();
    askTimeout = system.settings().config().getDuration("my-app.routes.ask-timeout");
  }

  private CompletionStage<GetPlayerResponse> getUser(String name) {
    return AskPattern.ask(userRegistryActor, ref -> new GetPlayer(name, ref), askTimeout, scheduler);
  }

  private CompletionStage<PlayerRegistry.ActionPerformed> deleteUser(String name) {
    return AskPattern.ask(userRegistryActor, ref -> new DeletePlayer(name, ref), askTimeout, scheduler);
  }

  private CompletionStage<Players> getPlayers() {
    return AskPattern.ask(userRegistryActor, GetPlayers::new, askTimeout, scheduler);
  }

  private CompletionStage<PlayerRegistry.ActionPerformed> findByClub(String query) {
    return AskPattern.ask(userRegistryActor, ref -> new FindByClub(query, ref), askTimeout, scheduler);
  }

  private CompletionStage<PlayerRegistry.ActionPerformed> createUser(Player player) {
    return AskPattern.ask(userRegistryActor, ref -> new CreatePlayer(player, ref), askTimeout, scheduler);
  }

  /**
   * This method creates one route (of possibly many more that will be part of your Web App)
   */
  //#all-routes
  public Route playerRoutes() {
    return Directives.concat(player(), club());
  }

  private Route club() {
    return pathPrefix("player", () ->
      pathPrefix("club", () -> pathEnd(() ->
          get(() ->
            parameter("q", query ->
              onSuccess(findByClub(query),
                players -> complete(StatusCodes.OK, players, Jackson.marshaller())
              )
            )
          )
        )
      )
    );
  }

  private Route player() {
    return pathPrefix("player", () ->
      concat(
        //#users-get-delete
        pathEnd(() ->
          concat(
            get(() ->
              onSuccess(getPlayers(),
                players -> complete(StatusCodes.OK, players, Jackson.marshaller())
              )
            ),
            post(() ->
              entity(
                Jackson.unmarshaller(Player.class),
                player ->
                  onSuccess(createUser(player), performed -> {
                    log.info("Create result: {}", performed.description);
                    return complete(StatusCodes.CREATED, performed, Jackson.marshaller());
                  })
              )
            )
          )
        ),
        //#users-get-delete
        //#users-get-post
        path(PathMatchers.segment(), (String name) ->
          concat(
            get(() ->
                //#retrieve-user-info
                rejectEmptyResponse(() ->
                  onSuccess(getUser(name), performed ->
                    complete(StatusCodes.OK, performed.maybePlayer, Jackson.marshaller())
                  )
                )
              //#retrieve-user-info
            ),
            delete(() ->
                //#users-delete-logic
                onSuccess(deleteUser(name), performed -> {
                    log.info("Delete result: {}", performed.description);
                    return complete(StatusCodes.OK, performed, Jackson.marshaller());
                  }
                )
              //#users-delete-logic
            )
          )
        )
        //#users-get-post
      )
    );
  }
  //#all-routes

}
