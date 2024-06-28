package com.solidsilver.papermc

import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player


class CompressCommand(private val plugin: Compressor): TabExecutor {

    companion object {
        fun registerSelf(plugin: Compressor) {
            val cmd = CompressCommand(plugin)
            plugin.getCommand("give")?.setExecutor(cmd)
            plugin.getCommand("give")?.tabCompleter = cmd
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Only players can run this command !");
            return false;
        }
        val numArgs = args?.size ?: 0
        if (args == null || args.size < 2 || args[0] == "help") {
            showHelp(sender)
            return false
        }

        try {
            val block = args[0]
            val level = args[1].toInt()
            val count: Int = when(numArgs) {
                3 -> args[2].toInt()
                else -> 1
            }


            if (level > Compressor.MAX_LEVEL) {
                sender.sendMessage("max level supported is ${Compressor.MAX_LEVEL}")
                showHelp(sender)
                return false
            }

            val mat = Material.matchMaterial(block)

            if (mat != null) {
                val compItem = plugin.createCompressedItem(mat, level, count)
                sender.inventory.addItem(compItem)
            } else {
                sender.sendMessage("block not supported")
                showHelp(sender)
                return false
            }
        } catch (e: NumberFormatException) {
            showHelp(sender)
           return false
        }







        return true
    }


    private fun showHelp(p: Player) {
        p.sendMessage("usage: /<command> <block> <level> (amount)")
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): MutableList<String>? {
        return when(args?.size) {
            1 -> {
                val blocksList =  listOf(Material.COBBLESTONE,
                    Material.COBBLED_DEEPSLATE,
                    Material.GOLD_BLOCK,
                    Material.STONE,
                    Material.QUARTZ_BLOCK,
                    Material.IRON_BLOCK,
                    Material.DIORITE,
                    Material.ANDESITE).map { it.name.lowercase() }.toMutableList()
                blocksList.sort()
                return blocksList
            }
            2 -> return mutableListOf("1", "2", "3", "4", "5")
            3 -> mutableListOf("1", "64")
            else -> null
        }
    }
}

