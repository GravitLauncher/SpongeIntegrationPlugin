package pro.gravit.sponge.launcherintegration.command;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import pro.gravit.launcher.request.admin.ExecCommandRequest;

public class ExecCommand implements CommandExecutor {
    @Inject
    private Logger logger;
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        ExecCommandRequest request = new ExecCommandRequest((String) args.getOne("cmd").get());
        try {
            request.request();
            src.sendMessage(Text.of("Execute Success"));
        } catch (Exception e) {
            logger.error("Execute Exception", e);
            src.sendMessage(Text.of("Error execute: %s %s", e.getClass().getSimpleName(), e.getMessage()));
        }
        return CommandResult.success();
    }
}
