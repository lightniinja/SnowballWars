package com.lightniinja.snowballwars;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command
  implements CommandExecutor
{
  public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args)
  {
    if (!(sender instanceof Player)) {
      return false;
    }
    Player p = (Player)sender;
    if (args.length == 1) {
      if (args[0].equalsIgnoreCase("leave")) {
        if (ArenaManager.getManager().getArena(p) == null) {
          return false;
        }
        ArenaManager.getManager().removePlayer(p);
      } else if (args[0].equalsIgnoreCase("vote")) {
        if (ArenaManager.getManager().getArena(p) == null) {
          return false;
        }
        if (ArenaManager.getManager().getArena(p).hasVoted(p)) {
          p.sendMessage(ChatColor.RED + "Already voted for arena to start.");
          return true;
        }
        ArenaManager.getManager().getArena(p).addVote(p);
        p.sendMessage(ChatColor.GREEN + "You have voted for the match to start.");
      }
      else if (args[0].equalsIgnoreCase("start")) {
        if (ArenaManager.getManager().getArena(p) == null) {
          return false;
        }
        if (sender.hasPermission("snowballwars.admin")) {
          Arena a = ArenaManager.getManager().getArena(p);
          a.setTime(1);
          p.sendMessage(ChatColor.GREEN + "Arena started!");
        } else {
          return false;
        }
      } else {
        return false;
      }
    }
    else return false;

    return true;
  }
}