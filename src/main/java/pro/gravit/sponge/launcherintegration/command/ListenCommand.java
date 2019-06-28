package pro.gravit.sponge.launcherintegration.command;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import pro.gravit.launcher.LauncherNetworkAPI;
import pro.gravit.launcher.events.request.LogEvent;
import pro.gravit.launcher.request.Request;
import pro.gravit.launcher.request.websockets.ClientWebSocketService;
import pro.gravit.launcher.request.websockets.RequestInterface;
import pro.gravit.sponge.launcherintegration.Launcherintegration;
import pro.gravit.utils.helper.LogHelper;

import java.io.IOException;

public class ListenCommand implements CommandExecutor {
    @Inject
    private Logger logger;
    public class LogListenerRequest implements RequestInterface {
        @LauncherNetworkAPI
        public LogHelper.OutputTypes outputType;

        public LogListenerRequest(LogHelper.OutputTypes outputType) {
            this.outputType = outputType;
        }

        @Override
        public String getType() {
            return "addLogListener";
        }
    }
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        try {
            if(!Launcherintegration.registerHandler)
            {
                LogListenerRequest request = new LogListenerRequest(LogHelper.OutputTypes.PLAIN);
                Request.service.sendObject(request);
            }
            if(src instanceof Player)
            {
                Player player = (Player) src;
                if(Launcherintegration.listens.contains(player))
                    Launcherintegration.listens.remove(player);
                else
                    Launcherintegration.listens.add(player);
                if(!Launcherintegration.registerHandler)
                {
                    ClientWebSocketService.EventHandler handler = (result) -> {
                        if (result instanceof LogEvent) {
                            for(Player p : Launcherintegration.listens)
                            {
                                try {
                                    p.sendMessage(Text.of(((LogEvent) result).string));
                                } catch (Throwable ex)
                                {
                                    logger.error("SendMessage", ex);
                                }
                            }
                        }
                    };
                    Request.service.registerHandler(handler);
                    Launcherintegration.registerHandler = true;
                }

            }
        } catch (IOException e) {
            logger.error("Listen Exception", e);
            src.sendMessage(Text.of("Error listen: %s %s", e.getClass().getSimpleName(), e.getMessage()));
        }
        return CommandResult.success();
    }
}
