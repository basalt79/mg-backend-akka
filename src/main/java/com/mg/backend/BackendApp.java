package com.mg.backend;

import akka.NotUsed;
import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Adapter;
import akka.actor.typed.javadsl.Behaviors;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.Directives;
import akka.http.javadsl.server.Route;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import com.mg.backend.player.PlayerRegistry;
import com.mg.backend.player.PlayerRoutes;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletionStage;

//#main-class
public class BackendApp {
  // #start-http-server
  static void startHttpServer(Route route, ActorSystem<?> system) {
    // Akka HTTP still needs a classic ActorSystem to start
    akka.actor.ActorSystem classicSystem = Adapter.toClassic(system);
    final Http http = Http.get(classicSystem);
    final Materializer materializer = Materializer.matFromSystem(system);

    final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = route.flow(classicSystem, materializer);
    CompletionStage<ServerBinding> futureBinding =
      http.bindAndHandle(routeFlow, ConnectHttp.toHost("localhost", 8080), materializer);

    futureBinding.whenComplete((binding, exception) -> {
      if (binding != null) {
        InetSocketAddress address = binding.localAddress();
        system.log().info("Server online at http://{}:{}/",
          address.getHostString(),
          address.getPort());
      } else {
        system.log().error("Failed to bind HTTP endpoint, terminating system", exception);
        system.terminate();
      }
    });
  }
  // #start-http-server

  public static void main(String[] args) throws Exception {
    //#server-bootstrapping
    Behavior<NotUsed> rootBehavior = Behaviors.setup(context -> {
      ActorRef<UserRegistry.Command> userRegistryActor = context.spawn(UserRegistry.create(), "UserRegistry");
      ActorRef<Command> playerRegistryActor = context.spawn(PlayerRegistry.create(), "PlayerRegistry");

      UserRoutes userRoutes = new UserRoutes(context.getSystem(), userRegistryActor);
      PlayerRoutes playerRoutes = new PlayerRoutes(context.getSystem(), playerRegistryActor);

      Route route = Directives.concat(userRoutes.userRoutes(), playerRoutes.playerRoutes());
//          Directives.pathPrefix("users", userRoutes.userRoutes())
      startHttpServer(route, context.getSystem());

      return Behaviors.empty();
    });

    // boot up server using the route as defined below
    ActorSystem.create(rootBehavior, "HelloAkkaHttpServer");
    //#server-bootstrapping
  }

}
//#main-class


