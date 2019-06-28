package pro.gravit.sponge.launcherintegration;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import pro.gravit.launcher.request.Request;
import pro.gravit.launcher.request.websockets.ClientWebSocketService;
import pro.gravit.launcher.server.ServerWrapper;
import pro.gravit.sponge.launcherintegration.command.ExecCommand;
import pro.gravit.sponge.launcherintegration.command.ListenCommand;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Plugin(
        id = "launcherintegration",
        name = "GravitLauncherIntegration",
        description = "Gravit Launcher Integration",
        authors = {
                "Gravit"
        }
)
public class Launcherintegration {
    public static HashSet<Player> listens = new HashSet<>();
    public static boolean registerHandler = false;

    @Inject
    private Logger logger;

    public ServerWrapper serverWrapper;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        logger.info("GravitLauncher Integration Plugin setup");
        serverWrapper = ServerWrapper.wrapper;
        logger.info("ServerWrapper connected to {}", serverWrapper.config.websocket.address);
        CommandSpec execute = CommandSpec.builder().description(Text.of("Execute some command on LaunchServer"))
                .arguments(GenericArguments.remainingJoinedStrings(Text.of("cmd")))
                .permission("gravitlauncher.admin.execute")
                .executor(new ExecCommand()).build();
        CommandSpec listen = CommandSpec.builder().description(Text.of("Listen log LaunchServer"))
                .permission("gravitlauncher.admin.listen")
                .executor(new ListenCommand()).build();
        Sponge.getCommandManager().register(this, execute, "ls_exec");
        Sponge.getCommandManager().register(this, listen, "ls_listen");
    }
    @Listener
    public void exitPlayer(ClientConnectionEvent.Disconnect event)
    {
        Player player = event.getTargetEntity();
        listens.remove(player);
    }
}
