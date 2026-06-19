package kr.pyke.deceased_croa.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import kr.pyke.PykeLib;
import kr.pyke.deceased_croa.data.MailboxData;
import kr.pyke.deceased_croa.registry.component.ModComponents;
import kr.pyke.deceased_croa.util.MailboxOpener;
import kr.pyke.util.constants.COLOR;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Collection;

public class MailboxCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        dispatcher.register(Commands.literal("우편함").executes(MailboxCommand::openMailbox));
        dispatcher.register(Commands.literal("mail")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("send")
                .then(Commands.argument("target", EntityArgument.players())
                    .executes(MailboxCommand::sendMailbox)
                )
            )
            .then(Commands.literal("clear")
                .then(Commands.argument("target", EntityArgument.players())
                    .executes(MailboxCommand::clearMailbox)
                )
            )
            .then(Commands.literal("debug").executes(MailboxCommand::debug))
        );
        dispatcher.register(Commands.literal("모두받기").executes(MailboxCommand::claimAllMailbox));
    }

    public static int openMailbox(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        MailboxOpener.open(player);

        return 1;
    }

    public static int debug(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        for (int i = 0; i < 45; ++i) {
            MailboxData mailboxData = MailboxData.create(new ItemStack(Items.STONE, 64));
            ModComponents.MAILBOX.get(player).addMail(mailboxData, true);
        }

        return 1;
    }

    public static int sendMailbox(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "target");

        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.isEmpty()) {
            PykeLib.sendSystemMessage(player, COLOR.RED.getColor(), "메일로 보낼 아이템이 없습니다.");
            return 0;
        }

        for (ServerPlayer target : players) {
            MailboxData mailboxData = MailboxData.create(heldItem.copy());
            ModComponents.MAILBOX.get(target).addMail(mailboxData, true);
        }
        PykeLib.sendSystemMessage(player, COLOR.LIME.getColor(), "메일 전송 완료!");

        return 1;
    }

    public static int clearMailbox(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "target");

        for (ServerPlayer target : players) {
            ModComponents.MAILBOX.get(target).clearAll();
            PykeLib.sendSystemMessage(target, COLOR.RED.getColor(), "관리자가 당신의 메일함을 초기화하였습니다.");
        }
        PykeLib.sendSystemMessage(player, COLOR.LIME.getColor(), String.format("§e%s§r명의 메일함을 초기화하였습니다.", players.size()));

        return 1;
    }

    public static int claimAllMailbox(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        int count = ModComponents.MAILBOX.get(player).claimAllMails(player);
        PykeLib.sendSystemMessage(player, COLOR.LIME.getColor(), String.format("메일 §e%s§r개를 모두 받으셨습니다.", count));

        return 1;
    }
}
